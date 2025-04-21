package com.example.game.ui.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.game.R;
import com.example.game.models.Usuario;
import com.example.game.recycler.UsuarioAdapter;
import com.example.game.databinding.ActivityAdminBinding;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

   private ActivityAdminBinding binding;


    private RecyclerView recyclerViewUsuarios;
    private Button btnAdicionarUsuario;
    private UsuarioAdapter usuarioAdapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewUsuarios = findViewById(R.id.recyclerViewUsuarios);
        btnAdicionarUsuario = findViewById(R.id.btnAdicionarUsuario);

        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(this));

        db = AppDatabase.getDatabase(this);
        carregarUsuarios();

        btnAdicionarUsuario.setOnClickListener(view -> {
            Toast.makeText(this, "Funcionalidade de adicionar ainda não implementada", Toast.LENGTH_SHORT).show();
        });
    }

    private void carregarUsuarios() {
        List<Usuario> usuarios = db.usuarioDao().getAll();
        usuarioAdapter = new UsuarioAdapter(usuarios, new UsuarioAdapter.OnUsuarioClickListener() {
            @Override
            public void onEditarClick(Usuario usuario) {
                // Aqui você pode abrir um dialog ou nova tela para editar
                Toast.makeText(AdminActivity.this, "Editar: " + usuario.getNome(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onExcluirClick(Usuario usuario) {
                mostrarDialogExcluir(usuario);
            }
        });
        recyclerViewUsuarios.setAdapter(usuarioAdapter);
    }

    private void mostrarDialogExcluir(Usuario usuario) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir usuário")
                .setMessage("Deseja realmente excluir " + usuario.getNome() + "?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    db.usuarioDao().delete(usuario);
                    carregarUsuarios();
                    Toast.makeText(this, "Usuário excluído", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Não", null)
                .show();
    }
}
