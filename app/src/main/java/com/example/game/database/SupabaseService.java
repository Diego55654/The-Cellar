package com.example.game.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.game.models.Usuario;
import com.example.game.ui.activities.AdminActivity;
import com.example.game.utils.SenhaUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SupabaseService {

    public static String salvarSupabase(Usuario usuario) {
        HttpURLConnection conn = null;
        try {
            conn = getHttpURLConnection(
                    SupabaseConfig.API_URL + "/rest/v1/" + SupabaseConfig.TABLE_NAME, "POST"
            );
            conn.setRequestProperty("Prefer", "return=representation");

            // Monta o corpo JSON
            JSONObject json = new JSONObject();
            json.put("nome", usuario.getNome());
            json.put("email", usuario.getEmail());

            Log.d("SUPABASE_JSON", "JSON enviado: " + json.toString());

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes("utf-8"));
            }

            int responseCode = conn.getResponseCode();
            Log.d("Supabase", "Resposta HTTP: " + responseCode);

            // Lê corpo da resposta (seja sucesso ou erro)
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

            Log.d("Supabase", "Resposta corpo: " + response.toString());

            if (responseCode == HttpURLConnection.HTTP_CREATED
                    || responseCode == HttpURLConnection.HTTP_OK) {

                JSONArray arr = new JSONArray(response.toString());
                if (arr.length() > 0) {
                    JSONObject obj = arr.getJSONObject(0);
                    usuario.setId(obj.getInt("id")); // pega ID real do Supabase
                    Log.d("Supabase", "ID atribuído: " + usuario.getId());
                }
                return response.toString();
            } else {
                return "Erro: " + responseCode + " - " + response.toString();
            }
        } catch (Exception e) {
            Log.e("Supabase", "Erro ao salvar usuário", e);
            return "Erro: " + e.getMessage();
        } finally {
            if (conn != null) conn.disconnect();
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

    public static Usuario atualizarUsuarioPorId(int idNecessario, Usuario usuarioEditado) {
        HttpURLConnection conn = null;

        try {
            // Monta a URL com filtro por ID
            URL url = new URL(
                    SupabaseConfig.API_URL + "/rest/v1/" + SupabaseConfig.TABLE_NAME + "?id=eq." + idNecessario
            );
            conn = (HttpURLConnection) url.openConnection();

            // Configura os cabeçalhos da requisição ANTES de abrir a conexão
            conn.setRequestMethod("PATCH");
            conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Prefer", "return=representation"); // retorna o objeto atualizado
            conn.setDoOutput(true);

            // Monta o corpo JSON com os dados atualizados
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("nome", usuarioEditado.getNome());
            jsonBody.put("email", usuarioEditado.getEmail());

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

            // Lê o corpo da resposta (se houver)tcc
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

            // Se sucesso, retorna o objeto atualizado
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                JSONArray arr = new JSONArray(response.toString());
                if (arr.length() > 0) {
                    JSONObject obj = arr.getJSONObject(0);
                    return Usuario.fromJson(obj);
                }
            }

            return null;

        } catch (Exception e) {
            Log.e("Supabase", "Erro ao atualizar usuário", e);
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }



    public static List<Usuario> buscarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        HttpURLConnection conn = null;

        try {
            // Monta a URL para listar todos os usuários
            URL url = new URL(
                    SupabaseConfig.API_URL + "/rest/v1/" + SupabaseConfig.TABLE_NAME + "?select=*"
            );
            conn = (HttpURLConnection) url.openConnection();

            // Configura os cabeçalhos da requisição
            conn.setRequestMethod("GET");
            conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");

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

                // Converte resposta em JSONArray
                JSONArray arr = new JSONArray(response.toString());

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Usuario usuario = Usuario.fromJson(obj);
                    lista.add(usuario);
                }
            } else {
                Log.e("Supabase", "Erro ao buscar usuários. Código: " + responseCode);
            }
        } catch (Exception e) {
            Log.e("Supabase", "Erro ao buscar usuários", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return lista;
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
                //Exone retorno do objeto enviado
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
