package com.example.game.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.database.AppDatabase;
import com.example.game.database.UsuarioDAO;
import com.example.game.databinding.ActivityLoginBinding;
import com.example.game.models.Usuario;
import com.example.game.session.AppSession;
import com.example.game.utils.SenhaUtils;
import com.example.game.database.SupabaseService;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UsuarioDAO usuarioDAO;
    private AppSession appSession;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private static final String ADMIN_EMAIL = "tcc";
    private static final String ADMIN_PASS = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        appSession = (AppSession) getApplication();

        if (appSession.isLoggedIn()) {
            direcionaActivity();
            return;
        }

        AppDatabase db = AppDatabase.getDatabase(this);
        usuarioDAO = db.usuarioDao();

        binding.btnLogin.setOnClickListener(v -> validarLogin());

        binding.btnCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });
    }

    private void validarLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String senha = binding.etSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha email e senha.", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnLogin.setEnabled(false);

        if (email.equals(ADMIN_EMAIL) && senha.equals(ADMIN_PASS)) {
            appSession.loginAdmin("Administrador");
            Toast.makeText(this, "Login como administrador!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AdminActivity.class));
            finish();
            return;
        }

        // Primeiro tenta o login remoto (Supabase)
        executor.execute(() -> {
            Usuario usuarioRemoto = SupabaseService.autenticarUsuarioRemoto(email, senha);

            runOnUiThread(() -> {
                if (usuarioRemoto != null) {
                    appSession.login(
                            usuarioRemoto.getId(),
                            usuarioRemoto.getNome(),
                            usuarioRemoto.getEmail()
                    );
                    Toast.makeText(
                            LoginActivity.this,
                            "Seja bem-vindo(a), " + usuarioRemoto.getNome(),
                            Toast.LENGTH_SHORT
                    ).show();
                    binding.btnLogin.setEnabled(true);
                    direcionaActivity();
                } else {
                    // Fallback: login local (Room)
                    autenticaLocal(email, senha);
                }
            });
        });
    }

    private void autenticaLocal(String email, String senha) {
        executor.execute(() -> {
            try {
                final Usuario usuarioLocal = usuarioDAO.getUsuarioByEmail(email);

                runOnUiThread(() -> {
                    binding.btnLogin.setEnabled(true);

                    if (usuarioLocal != null
                            && usuarioLocal.autenticarEmail(email)
                            && SenhaUtils.verificarSenha(senha, usuarioLocal.getSenha())) {

                        appSession.login(
                                usuarioLocal.getId(),
                                usuarioLocal.getNome(),
                                usuarioLocal.getEmail()
                        );
                        Toast.makeText(
                                LoginActivity.this,
                                "Login local bem-sucedido!",
                                Toast.LENGTH_SHORT
                        ).show();
                        direcionaActivity();
                    } else {
                        Toast.makeText(
                                LoginActivity.this,
                                "Email ou senha incorretos!",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    binding.btnLogin.setEnabled(true);
                    Toast.makeText(
                            LoginActivity.this,
                            "Erro ao fazer login local: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }
        });
    }

    private void direcionaActivity() {
        Intent intent = appSession.isAdmin()
                /*
                 * Explorando opções de operadores ternários
                 * if/else por: condicao ? valorVerdadeiro : valorFalso
                 */
                ? new Intent(this, AdminActivity.class)
                : new Intent(this, MainActivity.class);

        startActivity(intent);
        finish();
    }
}
