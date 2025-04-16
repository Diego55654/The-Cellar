package com.example.game.ui.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.game.databinding.ActivityCadastroBinding;
import com.example.game.utils.SenhaUtils;

public class CadastroActivity extends AppCompatActivity {

    private ActivityCadastroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflando o layout com View Binding
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ação do botão de cadastro com validações
        binding.btnCadastrar.setOnClickListener(view -> validarCadastro());
    }

    private void validarCadastro() {
        String email = binding.cadEmail.getText().toString().trim();
        String senha = binding.cadSenha.getText().toString().trim();

        // Inicialmente, desabilita o botão para evitar cliques repetidos
        binding.btnCadastrar.setEnabled(false);

        // Validação de campos vazios
        if (email.isEmpty() || senha.isEmpty()) {
            exibirErro("Por favor, preencha todos os campos!");
            return;
        }

        // Valida requisitos da senha (ex.: mínimo de 6 caracteres)
        if (senha.length() < 6) {
            exibirErro("A senha deve ter pelo menos 6 caracteres!");
            return;
        }

        // Simula o armazenamento da senha segura
        String senhaSegura = SenhaUtils.generateSecurePassword(senha);
        // Exibe mensagem de sucesso
        Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
        finish(); // Fecha a tela de cadastro após o sucesso
    }

    // Método auxiliar para exibir o erro e reabilitar o botão
    private void exibirErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
        binding.btnCadastrar.setEnabled(true); // Reabilita o botão para novas tentativas
    }
}


