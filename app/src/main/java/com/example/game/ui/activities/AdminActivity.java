package com.example.game.ui.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.game.database.AppDatabase;
import com.example.game.database.SupabaseService;
import com.example.game.databinding.ActivityAdminBinding;
import com.example.game.models.Usuario;
import com.example.game.recycler.UsuarioAdapter;
import com.example.game.utils.SenhaUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;
    private UsuarioAdapter usuarioAdapter;
    private AppDatabase db;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final List<Usuario> listaUsuarios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        carregarUsuarios();
        inicializarBanco();
        inicializarRecyclerView();
        configurarListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarUsuarios();
    }

    private void inicializarRecyclerView() {
        binding.recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(this));
        usuarioAdapter = new UsuarioAdapter(listaUsuarios, new UsuarioAdapter.OnUsuarioClickListener() {
            @Override
            public void onEditarClick(Usuario usuario) {
                binding.nomeUsuario.setText(usuario.getNome());
                binding.emailUsuario.setText(usuario.getEmail());
                binding.senhaUsuario.setText(usuario.getSenha());
                Toast.makeText(AdminActivity.this, "Pronto para editar: " + usuario.getEmail(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onExcluirClick(Usuario usuario) {
                excluirUsuarioSupabase(usuario.getEmail());
            }

            @Override
            public void onAdicionarClick(Usuario usuario) {
                mensagemAdicionado(usuario);
            }
        });
        binding.recyclerViewUsuarios.setAdapter(usuarioAdapter);
    }

    private void inicializarBanco() {
        db = AppDatabase.getDatabase(this);
    }

    private void configurarListeners() {
        binding.btnAdicionarUsuario.setOnClickListener(v -> {
            String nome = binding.nomeUsuario.getText().toString().trim();
            String email = binding.emailUsuario.getText().toString().trim();
            String senha = binding.emailUsuario.getText().toString().trim();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                return;
            }
            executor.execute(() -> {
                String senhaSegura = SenhaUtils.gerarSenhaSegura(SenhaUtils.gerarSenhaSegura(senha));
                Usuario novoUsuario = new Usuario(nome, email, senhaSegura);

                db.usuarioDao().inserir(novoUsuario); //Insere no Room
                SupabaseService.salvarSupabase(novoUsuario); //Insere no supabase
                runOnUiThread(() -> {
                    carregarUsuarios();
                    mensagemAdicionado(novoUsuario);
                });
            });
        });

        binding.btnAtualizarUsuario.setOnClickListener(v -> {
            String nome = binding.nomeUsuario.getText().toString().trim();
            String email = binding.emailUsuario.getText().toString().trim();
            String senhaSegura = binding.senhaUsuario.getText().toString().trim();


            //Outra instancia de Usuario
            Usuario atualizado = new Usuario(nome, email, senhaSegura);
            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, informe o email do usuário a ser atualizado.", Toast.LENGTH_SHORT).show();
                return;
            }

            executor.execute(() -> {
                SupabaseService.atualizarPorEmail(email, atualizado);
                db.usuarioDao().atualizarUsuarioPorEmail(nome, email, senhaSegura);
                runOnUiThread(() -> {
                    carregarUsuarios();
                    Toast.makeText(this, "Usuário atualizado!", Toast.LENGTH_SHORT).show();
                });
            });
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void carregarUsuarios() {
        executor.execute(() -> {
            final List<Usuario> usuarios = db.usuarioDao().getAll();
            runOnUiThread(() -> {
                listaUsuarios.clear();
                listaUsuarios.addAll(usuarios);
                usuarioAdapter.notifyDataSetChanged();
            });
        });
    }

    private void excluirUsuarioSupabase(String email) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir usuário")
                .setMessage("Deseja realmente excluir o usuário com email " + email + "?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    executor.execute(() -> {
                        SupabaseService.excluirPorEmail(email);
                        db.usuarioDao().excluirUsuarioPorEmail(email);
                        runOnUiThread(() -> {
                            carregarUsuarios();
                            Toast.makeText(this, "Usuário excluído!", Toast.LENGTH_SHORT).show();
                        });
                    });
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void mensagemAdicionado(Usuario usuario) {
        Toast.makeText(this, "Usuário " + usuario.getNome() + " adicionado com sucesso!", Toast.LENGTH_SHORT).show();
    }
}