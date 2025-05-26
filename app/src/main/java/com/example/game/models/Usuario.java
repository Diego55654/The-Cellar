package com.example.game.models;

import androidx.room.*;

import com.example.game.utils.SenhaUtils;

import java.util.Objects;
import java.util.regex.Pattern;

@Entity(tableName = "usuarios")
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "nome")
    public String nome;
    @ColumnInfo(name = "senha")
    public String email;
    @ColumnInfo(name = "email")

    public String senha;

    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\." +
            "[a-zA-Z]{2,}$";
    private static final Pattern SENHA_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private final static Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    //Objeto Usuario vazio devido a problemas com o Room
    public Usuario() {
    }

    // Construtor customizado com validação de campos null
    public Usuario(String nome, String senha, String email) {
        this.nome = Objects.requireNonNull(nome, "O campo nome não pode ser nulo");
        this.senha = Objects.requireNonNull(senha, "O campo senha não pode ser nulo");
        this.email = Objects.requireNonNull(email, "O campo email não pode ser nulo");

        if (nome.isEmpty() || senha.isEmpty() || email.isEmpty()) {
            throw new IllegalArgumentException("Nenhum dos campos pode estar vazio.");
        }
    }
    public static boolean ValidarEmail(String email){
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    // Método para validar senha
    public static boolean ValidarSenha(String senha) {
        return senha != null && SENHA_PATTERN.matcher(senha).matches();
    }

    //Getters e Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if(!ValidarEmail(email)){
            throw new IllegalArgumentException("Formato de email não aceito");
        }
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

    public void transformarSenha(String novaSenha){
        if (novaSenha != null && SENHA_PATTERN.matcher(novaSenha).matches()) {
            this.senha = SenhaUtils.gerarSenhaSegura(novaSenha); // Criptografa nova senha
            System.out.println("Senha alterada com sucesso!");
        } else {
            System.out.println("Senha inválida! A senha deve conter no mínimo 8 caracteres, " +
                    "incluindo pelo menos uma letra maiúscula, uma letra minúscula e um número.");
        }
    }
    // Método para verificar se a senha digitada está correta
    public boolean verificarSenha(String senhaDigitada) {
        return SenhaUtils.verificarSenha(senhaDigitada, this.senha);
    }
}
