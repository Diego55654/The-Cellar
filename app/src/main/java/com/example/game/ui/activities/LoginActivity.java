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

        // Obtém a instância do AppSession
        appSession = (AppSession) getApplication();

        // Verifica se já está logado
        if (appSession.isLoggedIn()) {
            direcionaActivity();
            return;
        }

        // Inicializa o banco de dados
        AppDatabase db = AppDatabase.getDatabase(this);
        usuarioDAO = db.usuarioDao();

        binding.btnLogin.setOnClickListener(view -> validarLogin());

        binding.btnCadastro.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });
    }

    private void validarLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String senha = binding.etSenha.getText().toString().trim();

        // Desabilita o botão para evitar cliques repetidos
        binding.btnLogin.setEnabled(false);

        // Verifica as credenciais do administrador
        if (email.equals(ADMIN_EMAIL) && senha.equals(ADMIN_PASS)) {
            appSession.loginAdmin("Administrador");
            Toast.makeText(this, "Login como administrador!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AdminActivity.class));
            finish();
            return;
        }

        // Executa a consulta numa Thread separada
        executor.execute(() -> {
            try {
                final Usuario usuario = usuarioDAO.getUsuarioByEmail(email);

                runOnUiThread(() -> {
                    binding.btnLogin.setEnabled(true);

                    if (usuario != null) {
                        if (usuario.autenticarEmail(email) &&
                                SenhaUtils.verificarSenha(senha, usuario.getSenha())) {

                            appSession.login(usuario.getId(), usuario.getNome(), usuario.getEmail());
                            Toast.makeText(LoginActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                            direcionaActivity();
                        } else {
                            Toast.makeText(LoginActivity.this, "Email ou senha incorretos!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    binding.btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this, "Erro ao fazer login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void direcionaActivity() {
        Intent intent;
        if (appSession.isAdmin()) {
            intent = new Intent(this, AdminActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
