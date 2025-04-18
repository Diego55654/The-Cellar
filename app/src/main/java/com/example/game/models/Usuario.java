package com.example.game.models;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.game.utils.SenhaUtils;

import java.util.regex.Pattern;
@Dao
@Entity(tableName = "usuarios")
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "nome")
    public String nome;
    @ColumnInfo(name = "email")

    public String email;
    @ColumnInfo(name = "senha")

    public String senha;

    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

    private static final Pattern PATTERN = Pattern.compile(PASSWORD_REGEX);

    public Usuario(String nome, String senha, String email) {
        this.email = email;
        this.nome = nome;
        this.senha = senha;
    }

    public String getEmail() {
        return nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
    public void trocarSenha(String novaSenha){
        if (novaSenha != null && PATTERN.matcher(novaSenha).matches()) {
            this.senha = SenhaUtils.generateSecurePassword(novaSenha); // Criptografa nova senha
            System.out.println("Senha alterada com sucesso!");
        } else {
            System.out.println("Senha inválida! A senha deve conter no mínimo 8 caracteres, " +
                    "incluindo pelo menos uma letra maiúscula, uma letra minúscula e um número.");
        }
    }

    // Método para verificar se a senha digitada está correta
    public boolean verifyPassword(String senhaDigitada) {
        return SenhaUtils.verifyPassword(senhaDigitada, this.senha);
    }


}
