package com.example.game.database;

import android.content.Context;

import androidx.room.*;

import com.example.game.models.Usuario;

@Database(entities = {Usuario.class}, version = 2, exportSchema = false) // Versao 2 inclui o campo dataCriacao
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract com.example.game.database.UsuarioDAO usuarioDao(); // DAO

    // Método singleton para obter instância do banco
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "app_db"
                            )
                            .fallbackToDestructiveMigration() // Migracoes do Banco
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
