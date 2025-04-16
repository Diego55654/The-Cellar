package com.example.game.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.R;
import com.example.game.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    // Credenciais do administrador (predefinidas)
    private static final String ADMIN_EMAIL = "tcc";
    private static final String ADMIN_PASS = "123456";  // 🔒 Troque por uma senha mais segura depois!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializando o View Binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ação do botão de login
        binding.btnLogin.setOnClickListener(view -> validarLogin());

        // Ação do botão de cadastro para ir à tela CadastroActivity
        binding.btnCadastro.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent); // Redireciona para a tela de cadastro
        });
    }

    private void validarLogin() {
        String usuario = binding.etEmail.getText().toString().trim();
        String senha = binding.etSenha.getText().toString().trim();

        // Verifica se os campos estão vazios
        if (usuario.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica credenciais do administrador
        if (usuario.equals(ADMIN_EMAIL) && senha.equals(ADMIN_PASS)) {
            Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
            finish(); // Fecha a tela de login
        } else {
            Toast.makeText(this, "Usuário ou senha incorretos!", Toast.LENGTH_SHORT).show();
        }
    }
}




