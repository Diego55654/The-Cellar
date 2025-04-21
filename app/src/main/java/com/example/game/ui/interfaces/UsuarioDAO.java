package com.example.jogo2.ui.interfaces;

import androidx.room.*;

import com.example.jogo2.models.Usuario;

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
}