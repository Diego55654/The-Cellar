package com.example.game.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.game.models.Usuario;

/**
 * Classe responsável pela criação da instância do banco de dados
 * utilizando Room.
 *
 * Versão 2: inclui o campo dataCriacao na entidade Usuario.
 */
@Database(entities = {Usuario.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // DAO de acesso à entidade Usuario
    public abstract UsuarioDAO usuarioDao();

    /**
     * Método Singleton para obter instância única do banco de dados.
     *
     * @param context Contexto da aplicação
     * @return Instância de AppDatabase
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "app_db"
                            )
                            // Destrói e recria o banco em caso de incompatibilidade de versão
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
