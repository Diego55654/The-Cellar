package com.example.game.ui.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.game.databinding.ActivityMainBinding;
import com.example.game.models.Administrador;
import com.example.game.models.Personagem;
import com.example.game.models.Usuario;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        Usuario usuario = new Usuario("Daniel", "12345", "dadada@gmail.com");
        Administrador adm = new Administrador("csheila", "ddededed", "1234", true);

    }

}