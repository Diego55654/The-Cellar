package com.example.game.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

    // Callback para comunicação com outras Activities
    public interface ValidationCallback {
        void onSuccess();
        void onError(String mensagem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getDatabase(this);

        binding.btnCadastrar.setOnClickListener(view -> validarCadastro());
    }

    private void validarCadastro() {
        String nome = binding.cadNome.getText().toString().trim();
        String email = binding.cadEmail.getText().toString().trim();
        String senha = binding.cadSenha.getText().toString().trim();

        binding.btnCadastrar.setEnabled(false);

        // Usa a função estática
        validarDadosUsuario(nome, email, senha, false, new ValidationCallback() {
            @Override
            public void onSuccess() {
                // Se validação passou, verifica email duplicado
                verificarEmailExistente(email, db, -1, new ValidationCallback() {
                    @Override
                    public void onSuccess() {
                        // Email não existe, pode cadastrar
                        cadastrarUsuario(nome, email, senha);
                    }

                    @Override
                    public void onError(String mensagem) {
                        runOnUiThread(() -> exibirErro(mensagem));
                    }
                });
            }

            @Override
            public void onError(String mensagem) {
                runOnUiThread(() -> exibirErro(mensagem));
            }
        });
    }

    private void cadastrarUsuario(String nome, String email, String senha) {
        executor.execute(() -> {
            try {
                String senhaCriptografada = SenhaUtils.gerarSenhaSegura(senha);
                Usuario novoUsuario = new Usuario(nome, email, senhaCriptografada);

                Log.d("DEBUG", "DATA DE CRIAÇÃO: " + novoUsuario.getDataCriacao());

                db.usuarioDao().inserir(novoUsuario);
                SupabaseService.salvarSupabase(novoUsuario);

                runOnUiThread(this::exibirSucesso);

            } catch (Exception e) {
                runOnUiThread(() -> exibirErro("Erro ao cadastrar: " + e.getMessage()));
            }
        });
    }

    // ========== MÉTODOS ESTÁTICOS PARA REUTILIZAÇÃO ==========

    /**
     * Valida os dados básicos do usuário (nome, email, senha)
     */
    public static void validarDadosUsuario(String nome, String email, String senha, boolean isUpdate, ValidationCallback callback) {
        // Validação de campos vazios
        if (nome.isEmpty() || email.isEmpty()) {
            callback.onError("Nome e email são obrigatórios!");
            return;
        }

        if (!isUpdate && senha.isEmpty()) {
            callback.onError("Senha é obrigatória!");
            return;
        }

        // Validação do formato do email
        if (!Usuario.ValidarEmail(email)) {
            callback.onError("Formato de email inválido. O email deve conter @ e um domínio válido (ex: usuario@email.com).");
            return;
        }

        // Validação da força da senha (só se foi informada)
        if (!senha.isEmpty() && !Usuario.ValidarSenha(senha)) {
            callback.onError("Senha inválida! A senha deve conter no mínimo 8 caracteres, " +
                    "incluindo pelo menos uma letra maiúscula, uma letra minúscula e um número.");
            return;
        }

        callback.onSuccess();
    }

    /**
     * Verifica se um email já existe no banco de dados
     */
    public static void verificarEmailExistente(String email, AppDatabase db, int usuarioAtualId, ValidationCallback callback) {
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                Usuario usuarioExistente = db.usuarioDao().getUsuarioByEmail(email);

                if (usuarioExistente != null) {
                    // Se é um update e o email pertence ao próprio usuário, não há problema
                    if (usuarioAtualId != -1 && usuarioExistente.getId() == usuarioAtualId) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Este email já está cadastrado!");
                    }
                } else {
                    callback.onSuccess();
                }

            } catch (Exception e) {
                callback.onError("Erro ao verificar email: " + e.getMessage());
            }
        });
    }

    /**
     * Valida usuário completo (dados + email único)
     * Combina validarDadosUsuario() + verificarEmailExistente()
     */
    public static void validarUsuarioCompleto(String nome, String email, String senha, AppDatabase db, int usuarioAtualId, boolean isUpdate, ValidationCallback callback) {

        validarDadosUsuario(nome, email, senha, isUpdate, new ValidationCallback() {
            @Override
            public void onSuccess() {
                // Se dados são válidos, verifica email duplicado
                verificarEmailExistente(email, db, usuarioAtualId, callback);
            }

            @Override
            public void onError(String mensagem) {
                callback.onError(mensagem);
            }
        });
    }

    // ========== MÉTODOS PRIVADOS DA ACTIVITY ==========

    private void exibirErro(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
        binding.btnCadastrar.setEnabled(true);
    }

    private void exibirSucesso() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Sucesso")
                .setMessage("Cadastro realizado com sucesso!")
                .setCancelable(false)
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finish();
                })
                .create();

        dialog.show();

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                finish();
            }
        }, 3000);
    }
}