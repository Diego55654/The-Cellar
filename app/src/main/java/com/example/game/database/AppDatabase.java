package com.example.game.database;

import android.content.Context;

import androidx.room.*;

import com.example.game.models.Usuario;
import com.example.game.ui.interfaces.UsuarioDAO;

@Database(entities = {Usuario.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract UsuarioDAO usuarioDao(); // DAO

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
                            .allowMainThreadQueries() // pode ser removido e usar async (Thread/LiveData)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
