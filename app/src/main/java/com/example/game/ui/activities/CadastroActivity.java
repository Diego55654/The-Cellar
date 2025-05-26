package com.example.game.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
        String nome = binding.cadNome.getText().toString().trim();

        // Inicialmente, desabilita o botão para evitar cliques repetidos
        binding.btnCadastrar.setEnabled(false);

        // Validação de campos vazios
        if (email.isEmpty() || senha.isEmpty() || nome.isEmpty()) {
            exibirErro("Por favor, preencha todos os campos!");
            return;
        }

        // Valida requisitos da senha (ex.: mínimo de 6 caracteres)
        if (senha.length() < 6) {
            exibirErro("A senha deve ter pelo menos 6 caracteres!");
            return;
        }

        // Senha segura
        SenhaUtils.generateSecurePassword(senha);

        // Exibe mensagem de sucesso
        exibirSucesso();
    }

    //Funcao para mostrar que algo deu errado
    private void exibirErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();

        // Reabilita o botão para novas tentativas
        binding.btnCadastrar.setEnabled(true);
    }

    //Funcao para mostrar que tudo funcionou corretamente
    private void exibirSucesso() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Sucesso")
                .setMessage("Cadastro realizado com sucesso!")
                .setCancelable(false)
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finish(); // Volta para a activity anterior
                })
                .create();

        dialog.show();

        // Cria um Delay após o alerta de sucesso
        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                finish();
            }
        }, 3000); // 3 segundos de Delay
    }
}