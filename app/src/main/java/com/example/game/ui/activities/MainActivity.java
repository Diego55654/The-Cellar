package com.example.game.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.game.R;
import com.example.game.databinding.ActivityMainBinding;
import com.example.game.session.AppSession;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding; // ViewBinding
    private AppSession appSession;       // Gerenciador de sessão do usuário

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

        WebView webView = binding.webView;
        webView.getSettings().setJavaScriptEnabled(true); // Para uso de Javascript
        webView.setWebViewClient(new WebViewClient()); //Mantém navegação interna

        // Obtém a sessão do usuário
        appSession = (AppSession) getApplication();

        // Verifica se está logado; se não, redireciona para a tela de login
        if (appSession == null || !appSession.isLoggedIn()){
            redirecionaLogin();
            return;
        }

        // Configura a interface do usuário com base nos dados da sessão
        setupUserInterface();

        // Redireciona para o site do jogo ao clicar no botão "Entrar"
        binding.btnEntrar.setOnClickListener(view -> {
            //Link com o jogo.
            binding.webView.loadUrl("https://carlosdk5.github.io/Gamer-of-the-Cellar/Game%201.V/index.html");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webView.getUrl()));
            startActivity(intent);
        });

        // Botão que encerra a sessão e redireciona para LoginActivity
        binding.btnSair.setOnClickListener(view -> sair()); //Reescrita a chamada da função sair em forma de lambda
    }

    // Redireciona para a tela de login, eliminando as telas anteriores
    private void redirecionaLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Interface com informações do usuário logado -- [Em desenvolvimento]
    private void setupUserInterface() {
        String welcomeMessage = "Bem-vindo, " + appSession.getUserName() + "!";
    }

    // Encerra a sessão do usuário e redireciona para a tela de login
    private void sair() {
        appSession.logout();
        redirecionaLogin();
    }
}
