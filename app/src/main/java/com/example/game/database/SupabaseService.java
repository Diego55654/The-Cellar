package com.example.game.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.game.models.Usuario;
import com.example.game.ui.activities.AdminActivity;
import com.example.game.utils.SenhaUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SupabaseService {

    public static String salvarSupabase(Usuario usuario) {
        try {
            HttpURLConnection conn = getHttpURLConnection(
                    SupabaseConfig.API_URL + "/rest/v1/" + SupabaseConfig.TABLE_NAME, "POST"
            );

            JSONObject json = new JSONObject();
            json.put("nome", usuario.getNome());
            json.put("email", usuario.getEmail());
            json.put("senha", usuario.getSenha());
            json.put("criado_em", usuario.getDataCriacao());

            // Depurador
            Log.d("SUPABASE_JSON", "JSON enviado: " + json.toString());

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            Log.e("Supabase", "Resposta HTTP: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_CREATED
                    || responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.d("Supabase", "Resposta corpo: " + response.toString());
                return response.toString();

            } else {
                return "Erro: " + responseCode;
            }
        } catch (Exception e) {
            Log.e("Supabase", "Erro ao salvar usuário", e);
            e.printStackTrace();
            return "Erro: " + e.getMessage();
        }
    }

    @NonNull
    private static HttpURLConnection getHttpURLConnection(String API_URL, String POST)
            throws IOException {

        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod(POST);
        conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Prefer", "return=representation");
        conn.setDoOutput(true);

        return conn;
    }

    public static String listarUsuarios() {
        try {
            URL url = new URL(
                    SupabaseConfig.API_URL + "/rest/v1/" + SupabaseConfig.TABLE_NAME + "?select=*"
            );
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            } else {
                return "Erro: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro: " + e.getMessage();
        }
    }

    public static String atualizarPorEmail(String email, Usuario usuario) {
        try {
            HttpURLConnection conn = getHttpURLConnection(
                    SupabaseConfig.API_URL + "/rest/v1/" + SupabaseConfig.TABLE_NAME + "?email=eq." + email,
                    "PATCH"
            );

            JSONObject json = new JSONObject();
            json.put("nome", usuario.getNome());
            json.put("senha", usuario.getSenha());

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK
                    || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                return "Usuário atualizado no Supabase.";
            } else {
                return "Erro Supabase: " + responseCode;
            }
        } catch (Exception e) {
            return "Erro Supabase: " + e.getMessage();
        }
    }

    public static String excluirPorEmail(String email) {
        try {
            int responseCode = getResponseCode(email);

            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                return "Usuário excluído do Supabase.";
            } else {
                return "Erro ao excluir: " + responseCode;
            }
        } catch (Exception e) {
            return "Erro Supabase: " + e.getMessage();
        }
    }

    private static int getResponseCode(String email) throws IOException {
        URL url = new URL(
                SupabaseConfig.API_URL + "/rest/v1/" + SupabaseConfig.TABLE_NAME + "?email=eq." + email
        );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);
        conn.setRequestProperty("Prefer", "return=representation");

        return conn.getResponseCode();
    }

    // Obtém a instância do SupabaseService
    public static SupabaseService getInstance(AdminActivity adminActivity) {
        return new SupabaseService();
    }

    public static int atualizarUsuarioPorId(int idNecessario, Usuario usuarioEditado) {
        HttpURLConnection conn = null;

        try {
            // Monta a URL com filtro por ID
            URL url = new URL(
                    SupabaseConfig.API_URL + "/rest/v1/" + SupabaseConfig.TABLE_NAME + "?id=eq." + idNecessario
            );
            conn = (HttpURLConnection) url.openConnection();

            // Configura os cabeçalhos da requisição
            conn.setRequestMethod("PATCH");
            conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Prefer", "return=representation");
            conn.setDoOutput(true);

            // Monta o corpo JSON com os dados atualizados
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("nome", usuarioEditado.getNome());
            jsonBody.put("email", usuarioEditado.getEmail());

            // Só atualizará o campo senha se e somente se estiver preenchido
            if (usuarioEditado.getSenha() != null && !usuarioEditado.getSenha().isEmpty()) {
                jsonBody.put("senha", usuarioEditado.getSenha());
            }

            // Envia o corpo da requisição
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Captura o código de resposta
            int responseCode = conn.getResponseCode();

            // Captura o corpo da resposta (inclusive em caso de erro)
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream()
                    )
            );
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // Logs para depuração
            Log.d("Supabase", "Código HTTP: " + responseCode);
            Log.d("Supabase", "Resposta Supabase: " + response.toString());

            return responseCode;

        } catch (JSONException e) {
            Log.e("Supabase", "Erro ao montar JSON", e);
            throw new RuntimeException("Erro ao montar o JSON para atualização");
        } catch (IOException e) {
            Log.e("Supabase", "Erro de conexão", e);
            throw new RuntimeException("Erro ao atualizar o usuário por ID: " + e.getMessage());
        }
    }

    public static Usuario buscarUsuarioPorEmail(String email) {
        try {
            // Monta a URL com filtro por email
            URL url = new URL(
                    SupabaseConfig.API_URL + "/rest/v1/" + SupabaseConfig.TABLE_NAME + "?email=eq." + email
            );
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Configura os cabeçalhos da requisição
            conn.setRequestMethod("GET");
            conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");

            // Lê a resposta
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                // Converte a resposta em JSON
                JSONArray array = new JSONArray(response.toString());

                if (array.length() > 0) {
                    JSONObject obj = array.getJSONObject(0);
                    return new Usuario(
                            obj.getInt("id"),
                            obj.getString("nome"),
                            obj.getString("email"),
                            obj.getString("senha"),
                            obj.optString("criado_em", null)
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Retorna null se não encontrar ou der erro
    }
    public static Usuario autenticarUsuarioRemoto(String email, String senha) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(
                    SupabaseConfig.API_URL + "/rest/v1/"
                            + SupabaseConfig.TABLE_NAME
                            + "?email=eq." + email
            ).openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                JSONArray array = new JSONArray(response.toString());
                if (array.length() == 0) {
                    return null;
                }

                JSONObject obj = array.getJSONObject(0);
                String senhaHash = obj.getString("senha");

                if (!SenhaUtils.verificarSenha(senha, senhaHash)) {
                    return null;
                }

                return new Usuario(
                        obj.getInt("id"),
                        obj.getString("nome"),
                        obj.getString("email"),
                        senhaHash,
                        obj.optString("criado_em", null)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
