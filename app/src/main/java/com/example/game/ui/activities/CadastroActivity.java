package com.example.game.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.game.database.AppDatabase;
import com.example.game.database.SupabaseService;
import com.example.game.databinding.ActivityCadastroBinding;
import com.example.game.models.Usuario;
import com.example.game.utils.SenhaUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CadastroActivity extends AppCompatActivity {

    private ActivityCadastroBinding binding;
    private AppDatabase db;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflando o layout com View Binding
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar banco de dados
        db = AppDatabase.getDatabase(this);
        
        // Ação do botão de cadastro com validações
        binding.btnCadastrar.setOnClickListener(view -> validarCadastro());
    }

    private void validarCadastro() {
        String nome = binding.cadNome.getText().toString().trim();
        String email = binding.cadEmail.getText().toString().trim();
        String senha = binding.cadSenha.getText().toString().trim();

                // Validação de campos vazios
                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    exibirErro("Por favor, preencha todos os campos!");
                    return;
                }

                // Validação do formato do email
                if (!Usuario.ValidarEmail(email)) {
                    exibirErro("Formato de email inválido. O email deve conter @ e um domínio válido (ex: usuario@email.com).");
                    return;
                }

                // Validação da força da senha
                if (!Usuario.ValidarSenha(senha)) {
                    exibirErro("Senha inválida! A senha deve conter no mínimo 8 caracteres, " +
                            "incluindo pelo menos uma letra maiúscula, uma letra minúscula e um número.");
                    return;
                }

                // Desabilita o botão após as autenticações passarem
                binding.btnCadastrar.setEnabled(false);

        //Verifica se email já existe no banco
        executor.execute(() -> {
            try {
                // Verifica se email já está cadastrado
                Usuario usuarioExistente = db.usuarioDao().getUsuarioByEmail(email);

                    if (usuarioExistente != null) {
                        runOnUiThread(() -> exibirErro("Este email já está cadastrado!"));
                        return;
                    }

                // Criptografar senha e cria usuário
                String senhaCriptografada = SenhaUtils.gerarSenhaSegura(senha);


                // Usuario : (nome, email, senha)
                Usuario novoUsuario = new Usuario(nome, email, senhaCriptografada);

                // Salvar no banco de dados
                db.usuarioDao().inserir(novoUsuario);
                
                //Salvar no Supabase
                SupabaseService.salvarSupabase(novoUsuario);
                runOnUiThread(this::exibirSucesso);

            } catch (Exception e) {
                runOnUiThread(() -> exibirErro("Erro ao cadastrar: " + e.getMessage()));
            }
        });
    }

    //Funcao para mostrar que algo deu errado
    private void exibirErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();

        // reabilita o botão para novas tentativas
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