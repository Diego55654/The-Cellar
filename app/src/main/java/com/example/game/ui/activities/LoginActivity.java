package com.example.game.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.*;

import com.example..game.database.AppDatabase;
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

        // Inicializa o banco de dados
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-db")
                .allowMainThreadQueries() // ⚠️ Apenas para testes!
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

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica credenciais do administrador
        if (email.equals(ADMIN_EMAIL) && senha.equals(ADMIN_PASS)) {
            Toast.makeText(this, "Login como administrador!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AdminActivity.class));
            finish();
            return;
        }

        // Verifica se o usuário existe no banco de dados
        Usuario usuario = usuarioDAO.getUsuarioByEmail(email);

        if (usuario != null && SenhaUtils.verifyPassword(senha, usuario.getSenha())) {
            Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class)); // troque para sua tela principal
            finish();
        } else {
            Toast.makeText(this, "Usuário ou senha incorretos!", Toast.LENGTH_SHORT).show();
        }
    }
}




