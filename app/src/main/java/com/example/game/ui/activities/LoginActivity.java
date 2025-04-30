package com.example.game.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.game.database.AppDatabase;
import com.example.game.databinding.ActivityLoginBinding;
import com.example.game.ui.interfaces.UsuarioDAO;
import com.example.game.models.Usuario;
import com.example.game.utils.SenhaUtils;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AppDatabase db;
    private UsuarioDAO usuarioDAO;

    private static final String ADMIN_EMAIL = "tcc";
    private static final String ADMIN_PASS = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializa o banco de dados permitindo queries na thread principal
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-db")
                .allowMainThreadQueries() // ⚠️ Apenas para testes, não recomendado para produção
                .build();
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

        // Verifica as credenciais do administrador
        if (email.equals(ADMIN_EMAIL) && senha.equals(ADMIN_PASS)) {
            Toast.makeText(this, "Login como administrador!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AdminActivity.class));
            finish();
            return;
        }

        // Executa a consulta numa Thread separada
        new Thread(() -> {
            Usuario usuario = usuarioDAO.getUsuarioByEmail(email);

            runOnUiThread(() -> {
                if (usuario != null && SenhaUtils.verifyPassword(senha, usuario.getSenha())) {
                    Toast.makeText(LoginActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Usuário ou senha incorretos!", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
