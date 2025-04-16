package com.example.game.models;

import com.example.game.ui.interfaces.Combate;

public abstract class Personagem implements Combate {
    protected String nome;
    protected int forca;
    protected int dano;
    protected String alvo;

    // Construtor da classe
    public Personagem(String nome, int forca, int dano, String alvo) {
        this.nome = nome;
        this.forca = forca;
        this.dano = dano;
        this.alvo = alvo;
    }

    @Override
    public abstract void atacar();
    @Override
    public void defender() {
        System.out.println(nome + " se defendeu!");
    }
}
