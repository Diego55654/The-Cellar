package com.example.game.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.game.R;
import com.example.game.databinding.ActivityMainBinding;
import com.example.game.session.AppSession;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding; // ViewBinding
    private AppSession appSession; // Gerenciador de sessão do usuário

    // Verifica a permanência do usuário quando a Activity é executada
    @Override
    protected void onResume() {
        super.onResume();
        if (appSession.reautenticarSessao()) {
            redirecionaLogin();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        // Obtém a sessão do usuário
        appSession = (AppSession) getApplication();

        // Verifica se está logado; se não, redireciona para a tela de login
        if (!appSession.isLoggedIn()) {
            redirecionaLogin();
            return;
        }

        // Configura a interface do usuário com base nos dados da sessão
        setupUserInterface();

        // Redireciona para o site do jogo ao clicar no botão "Entrar"
        binding.btnEntrar.setOnClickListener(view -> {
            String url = "https://github.com/Diego55654/The-Cellar"; //Teste: aqui em breve vai entrar a URL do game
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        /* Botao que chama um metodo que encerra a sessao atual e
         outro que encaminha para LoginActivity, respectivamente */
        binding.btnSair.setOnClickListener(view -> {
            appSession.logout();
            redirecionaLogin();
        });
    }

    // Redireciona para a tela de login, eliminando as telas anteriores.
    private void redirecionaLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    //  A interface com informações do usuário logado -- [Em desenvolvimento]
    private void setupUserInterface() {
        String welcomeMessage = "Bem-vindo, " + appSession.getUserName() + "!";

        // Se for admin, pode liberar funcionalidades extras
        if (appSession.isAdmin()) {
            // Exemplo: Adicionar botões administrativos

        }
    }

    // Cria o menu de opções na barra superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // Define ações ao clicar em itens do menu (logout e perfil) -- [Em desenvolvimento]
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            Sair(); // Realiza logout
            return true;
        } else if (id == R.id.action_profile) {
            // Abrir tela de perfil
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Encerra a sessão do usuário e redireciona para a tela de login
    private void Sair() {
        appSession.logout();
        redirecionaLogin();
    }
}
