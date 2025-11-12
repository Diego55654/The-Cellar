package com.example.game.models;

import androidx.room.*;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

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

    @ColumnInfo(name = "data_criacao")
    public String dataCriacao;

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern SENHA_PATTERN = Pattern.compile(PASSWORD_REGEX);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    // Objeto Usuario vazio devido a problemas com o Room
    public Usuario() {
    }

    // Construtor customizado com validação de campos null
    public Usuario(String nome, String email, String senha) {
        this.nome = Objects.requireNonNull(nome, "O campo nome não pode ser nulo");
        this.email = Objects.requireNonNull(email, "O campo email não pode ser nulo");
        this.senha = Objects.requireNonNull(senha, "O campo senha não pode ser nulo");
        this.dataCriacao = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                .format(new Date());

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            throw new IllegalArgumentException("Nenhum dos campos pode estar vazio.");
        }
    }

    // Métodos de validação do formato do Email/Senha no CadastroActivity
    public static boolean ValidarEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean ValidarSenha(String senha) {
        return senha != null && SENHA_PATTERN.matcher(senha).matches();
    }

    // Getters e Setters
    public String getEmail() {
        return email;
    }

    // Bloqueia emails fora do padrão
    public void setEmail(String email) {
        if (!ValidarEmail(email)) {
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

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    // Método para verificar se o email digitado está correto
    public boolean autenticarEmail(String emailDigitado) {
        return this.email != null && this.email.equalsIgnoreCase(emailDigitado);
    }

    // JSON para envio (não inclui o ID)
    public String toJson() {
        return "{"
                + "\"nome\":\"" + nome + "\","
                + "\"email\":\"" + email + "\","
                + "\"senha\":\"" + senha + "\","
                + "\"criado_em\":\"" + dataCriacao + "\""
                + "}";
    }
    // Converte JSON vindo do Supabase em objeto Usuario
    public static Usuario fromJson(JSONObject obj) {
        int id = obj.optInt("id", 0);
        String nome = obj.optString("nome", "");
        String email = obj.optString("email", "");
        String senha = obj.optString("senha", "");
        String dataCriacao = obj.optString("criado_em", "");

        return new Usuario(id, nome, email, senha, dataCriacao);
    }

    // Construtor para carregar usuários do banco remoto
    @Ignore
    public Usuario(int id, String nome, String email, String senha, String dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataCriacao = dataCriacao;
    }
}
