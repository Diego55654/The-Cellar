package com.example.game.ui.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.game.databinding.ActivityMainBinding;
import com.example.game.session.AppSession;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public AppSession appSession;

    private boolean rotacao = false;

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

        // WebView CONFIGURAÇÕES
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        WebView.setWebContentsDebuggingEnabled(true);

        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);


        appSession = (AppSession) getApplication();

        if (appSession == null || !appSession.isLoggedIn()) {
            redirecionaLogin();
            return;
        } else {
            setupUserInterface();
        }



        binding.btnEntrar.setOnClickListener(view -> {

            /*
             * rotacao -> false
             * rotacao = true -> muda a orientação para landscape (PAISAGEM)
             */
            if (!rotacao) {
                rotacao = true;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                //Delay na transição de formato da tela
                new Handler().postDelayed(() -> {
                    iniciarJogo();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    }, 500); // 500ms
            } else {
                iniciarJogo();
            }
        });

        binding.btnSair.setOnClickListener(view -> sair());
    }

    private void iniciarJogo() {
        ocultarElementos();
        ModoImersivo();

        // Recupera o id da sessao
        int idUsuario = appSession.getUserId();

        //Endereço do jogo + o id obtido do respectivo usuario conectado
        String url_jogo = "https://MainGame-cellar.gamer.gd?id_usuario=" + idUsuario;

        //Depuração
        Log.d("GAME:", "Loading URL:" + url_jogo);

        // Inicia o jogo com Webview
        binding.webView.loadUrl(url_jogo);
    }
    //Do XML
    private void ocultarElementos() {
        binding.navBar.setVisibility(View.GONE);
        binding.tvBemVindo.setVisibility(View.GONE);
        binding.btnEntrar.setVisibility(View.GONE);
        binding.btnSair.setVisibility(View.GONE);
        binding.webView.setVisibility(View.VISIBLE);
    }

    // Oculta barras de status e navegação, deixando o layout em tela cheia.
    private void ModoImersivo() {
        View decorView = getWindow().getDecorView(); // Obtém a view raiz da janela atual.
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Esconde barras e reaplica automaticamente após interação.
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // Oculta barra de status (topo).
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Oculta barra de navegação (base).
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Permite que o layout ocupe área da barra de status.
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // Permite que o layout ocupe área da barra de navegação.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE // Evita mudanças inesperadas no layout. Ex: esconder/mostrar barras.
        );
    }



    // Reaplica modo imersivo quando a janela ganha foco e o WebView está visível.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && binding.webView.getVisibility() == View.VISIBLE) {
            iniciarJogo();
            ModoImersivo();
        }
    }



    // 2. Caso o botao sair seja acionado
    private void redirecionaLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    // Mensagem de boas vindas
    private void setupUserInterface() {
        String welcomeMessage = "Bem-vindo, " + appSession.getUserName() + "!";
        Toast.makeText(this, welcomeMessage, Toast.LENGTH_SHORT).show();
    }
    //1. Caso o botao sair seja acionado
    private void sair() {
        appSession.logout(); // sai da sessao
        redirecionaLogin(); // envia "para o login"
    }
}
