package com.example.game.models;

public class Administrador extends Usuario {

    private boolean permissaoTotal;

    public Administrador(String nome, String email, String senha, boolean permissaoTotal) {
        super(nome, email, senha); // Chama o construtor da classe base
        this.permissaoTotal = permissaoTotal;
    }

    public void gerenciarUsuarios() {
        if (permissaoTotal) {
            System.out.println("Gerenciando usuários...");
        } else {
            System.out.println("Permissão insuficiente.");
        }
    }
}
