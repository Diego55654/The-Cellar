package com.example.game.session;

import android.app.Application;
import android.content.SharedPreferences;

public class AppSession extends Application {

    private static final String preferences_NAME = "user_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_IS_ADMIN = "is_admin";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences preferences;

    // Cache em memória para melhorar desempenho
    private Integer userId;
    private String userName;
    private String userEmail;
    private boolean isAdmin;
    private boolean isLoggedIn;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences(preferences_NAME, MODE_PRIVATE);
        loadSession(); // Carrega os dados da sessão ao iniciar o app
    }

    // Verifica se a sessão precisa ser reautenticada
    public boolean reautenticarSessao() {
        return !isLoggedIn || (userId == null && !isAdmin);
    }

    // Recupera sessão salva no SharedPreferences
    private void loadSession() {
        isLoggedIn = preferences.getBoolean(KEY_IS_LOGGED_IN, false);
        if (isLoggedIn) {
            userId = preferences.getInt(KEY_USER_ID, -1);
            userName = preferences.getString(KEY_USER_NAME, null);
            userEmail = preferences.getString(KEY_USER_EMAIL, null);
            isAdmin = preferences.getBoolean(KEY_IS_ADMIN, false);

            // Se os dados estiverem corrompidos, realiza logout
            if (userId == -1 || userName == null || userEmail == null) {
                logout();
            }
        }
    }

    // Realiza login para usuários comuns
    public void login(int userId, String userName, String userEmail) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.isAdmin = false;
        this.isLoggedIn = true;

        salvarSessao();
    }

    // Login exclusivo para administradores (não possuem ID fixo)
    public void loginAdmin(String adminName) {
        this.userId = -1; // Admin não tem ID no banco
        this.userName = adminName;
        this.userEmail = "admin@system.com";
        this.isAdmin = true;
        this.isLoggedIn = true;

        salvarSessao();
    }

    // Salva dados da sessão no SharedPreferences
    private void salvarSessao() {
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
            editor.putInt(KEY_USER_ID, userId != null ? userId : -1);
            editor.putString(KEY_USER_NAME, userName);
            editor.putString(KEY_USER_EMAIL, userEmail);
            editor.putBoolean(KEY_IS_ADMIN, isAdmin);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace(); // Em caso de erro, não interrompe o programa
        }
    }

    // Limpa a sessão e SharedPreferences ao realizar logout
    public void logout() {
        this.userId = null;
        this.userName = null;
        this.userEmail = null;
        this.isAdmin = false;
        this.isLoggedIn = false;

        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    // Métodos para recuperar os dados da sessão
    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
