package com.example.game.database;


import androidx.room.*;

import com.example.game.models.Usuario;

import java.util.List;

@Dao
public interface UsuarioDAO {

    @Insert
    void inserir(Usuario usuario);

    @Update
    void update(Usuario usuario);

    @Delete
    void delete(Usuario usuario);

    // Busca o usuário pelo e-mail
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    Usuario getUsuarioByEmail(String email);
    @Query("SELECT * FROM usuarios")
    List<Usuario> getAll();
    @Query("UPDATE usuarios SET nome = :nome, senha = :senha WHERE email = :email")
    void atualizarUsuarioPorEmail(String nome, String senha, String email);

    @Query("DELETE FROM usuarios WHERE email = :email")
    void excluirUsuarioPorEmail(String email);

    @Query("UPDATE usuarios SET nome = :nome, email = :email, senha = :senha WHERE id = :id")
    void atualizarUsuarioPorId(int id, String nome, String email, String senha);

    @Query("SELECT * FROM usuarios WHERE id = :id")
    Usuario getUsuarioById(int id);

    @Update
    int atualizarUsuario(Usuario usuario);

}
