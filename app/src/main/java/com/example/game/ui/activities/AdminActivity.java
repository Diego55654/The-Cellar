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

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUsuarios;
    private Button btnAdicionarUsuario;
    private List<Usuario> listaUsuarios;
    private UsuarioAdapter usuarioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        // Ajuste de layout para insets da tela
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configuração dos elementos da tela
        recyclerViewUsuarios = findViewById(R.id.recyclerViewUsuarios);
        btnAdicionarUsuario = findViewById(R.id.btnAdicionarUsuario);

        // Configuração do RecyclerView
        recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(this));

        // Carregar usuários fictícios (simulação de banco de dados)
        listaUsuarios = carregarUsuariosFalsos();
        usuarioAdapter = new UsuarioAdapter(listaUsuarios);
        recyclerViewUsuarios.setAdapter(usuarioAdapter);

        // Ação do botão para adicionar usuário
        btnAdicionarUsuario.setOnClickListener(view -> {
            Toast.makeText(AdminActivity.this, "Adicionar usuário (Funcionalidade futura)", Toast.LENGTH_SHORT).show();
        });
    }

    // Simulação de carregamento de usuários
    private List<Usuario> carregarUsuariosFalsos() {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(new Usuario("João", "123", "dadad@"));
        usuarios.add(new Usuario("Maria", "123", "dadada@"));
        return usuarios;
    }
}
