package com.example.game.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.game.database.AppDatabase;
import com.example.game.databinding.ActivityLoginBinding;
import com.example.game.database.UsuarioDAO; // Import corrigido
import com.example.game.models.Usuario;
import com.example.game.utils.SenhaUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AppDatabase db;
    private UsuarioDAO usuarioDAO;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private static final String ADMIN_EMAIL = "tcc";
    private static final String ADMIN_PASS = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializa o banco de dados - []removido allowMainThreadQueries()
        db = AppDatabase.getDatabase(this);
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

        // Inicialmente, desabilita o botão para evitar cliques repetidos
        binding.btnLogin.setEnabled(false);

        // Verifica as credenciais do administrador
        if (email.equals(ADMIN_EMAIL) && senha.equals(ADMIN_PASS)) {
            Toast.makeText(this, "Login como administrador!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AdminActivity.class));
            finish();
            return;
        }

        // Executa a consulta numa Thread separada
        executor.execute(() -> {
            final Usuario usuario = usuarioDAO.getUsuarioByEmail(email);

            runOnUiThread(() -> {
                // Ocultar progresso
                binding.btnLogin.setEnabled(true);

                // Login assíncrono com verificação de senha
                if (usuario != null && SenhaUtils.verificarSenha(senha, usuario.getSenha())) {
                    Toast.makeText(LoginActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Usuário ou senha incorretos!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}