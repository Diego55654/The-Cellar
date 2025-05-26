package com.example.game.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.game.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        //Envia a o usuário ao navegador onde acontece o jogo
        binding.btnEntrar.setOnClickListener(view -> {
            // Teste: Aqui futuramente deve entrar o site com todo o comnteúdo do jogo
            String url = "https://github.com/Diego55654/The-Cellar";

            // Cria um intent para a string que foi convertida numa URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
    }

}