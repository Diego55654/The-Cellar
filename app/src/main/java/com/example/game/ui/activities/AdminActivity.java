package com.example.game.ui.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.game.database.AppDatabase;
import com.example.game.models.Usuario;
import com.example.game.recycler.UsuarioAdapter;
import com.example.game.databinding.ActivityAdminBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;
    private UsuarioAdapter usuarioAdapter;
    private AppDatabase db;

    // Executor com uma única thread para tarefas assíncronas (ex: consultas ao banco)
    private final Executor executor = Executors.newSingleThreadExecutor();

    //Lista que serve para armazenar os usuarios do bd com RecyclerView
    private final List<Usuario> listaUsuarios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Layout e adapter
        binding.recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(this));

        usuarioAdapter = new UsuarioAdapter(listaUsuarios, new UsuarioAdapter.OnUsuarioClickListener() {

            @Override //Caso Editar
            public void onEditarClick(Usuario usuario) {
                Toast.makeText(AdminActivity.this, "Editar: " + usuario.getNome(), Toast.LENGTH_SHORT).show();
            }

            @Override //Caso Excluir (FEITO)
            public void onExcluirClick(Usuario usuario) {
                mensagemExcluir(usuario);
            }

            @Override //Caso Adicionar (em andamento)
            public void onAdicionarClick(Usuario usuario) {
            mensagemAdicionado(usuario);
        }
    });
        //Adapter
        binding.recyclerViewUsuarios.setAdapter(usuarioAdapter);

        //Instancia do banco de dados
        db = AppDatabase.getDatabase(this);

        //Adiciona usuario do BD para o RecyclerView
        carregarUsuarios();

        binding.btnAdicionarUsuario.setOnClickListener(view -> {
            // Ação do botão adicionar
        });
    }

    //Força a atualização completa da lista sem diferenciar quais itens mudaram
    @SuppressLint("NotifyDataSetChanged")
    private void carregarUsuarios() {
        executor.execute(() -> {
            final List<Usuario> usuarios = db.usuarioDao().getAll();

        //Responsavel por atualizar toda a lista
            runOnUiThread(() -> {
                listaUsuarios.clear();
                listaUsuarios.addAll(usuarios);
                usuarioAdapter.notifyDataSetChanged();
            });
        });
    }
    private void  mensagemAdicionado(Usuario usuario){
        Toast.makeText(this, "Usuario" + usuario.getNome() + "adicionado com sucesso!", Toast.LENGTH_SHORT).show();
    }

    //Mensagem personalizada ao excluir o usuario
    private void mensagemExcluir(Usuario usuario) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir usuário")
                .setMessage("Deseja realmente excluir " + usuario.getNome() + "?")
                .setPositiveButton("Sim", (dialog, which) -> {
                        executor.execute(() -> {
                            db.usuarioDao().delete(usuario);
                                runOnUiThread(() -> {
                                carregarUsuarios();
                                Toast.makeText(this, "Usuário excluído", Toast.LENGTH_SHORT).show();
                        });
                    });
                })
                .setNegativeButton("Não", null)
                .show();
    }
    //Garante que os usuarios serao recarregados automaticamente
    @Override
    protected void onResume() {
        super.onResume();
        carregarUsuarios();
    }
}
