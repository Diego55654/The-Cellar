package com.example.game.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.game.models.Usuario;

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
            HttpURLConnection conn = getHttpURLConnection(SupabaseConfig.API_URL + "/rest/v1/" + SupabaseConfig.TABLE_NAME, "POST");


            JSONObject json = new JSONObject();
                    json.put("nome", usuario.getNome());
                    json.put("email", usuario.getEmail());
                    json.put("senha", usuario.getSenha());
                    json.put("criado_em", usuario.getDataCriacao());
                            //Depurador
                            Log.d("SUPABASE_JSON", "JSON enviado: " + json.toString());

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.flush();
                os.close();


                    int responseCode = conn.getResponseCode();
                    Log.e("Supabase", "Resposta HTTP: "+ responseCode);
                    if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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
    private static HttpURLConnection getHttpURLConnection(String API_URL, String POST) throws IOException {
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
            URL url = new URL(SupabaseConfig.API_URL + "/rest/v1/" + SupabaseConfig.TABLE_NAME + "?select=*");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            conn.setRequestMethod("GET");
            conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);


            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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
            HttpURLConnection conn = getHttpURLConnection(SupabaseConfig.API_URL + "/rest/v1/" + SupabaseConfig.TABLE_NAME + "?email=eq." + email, "PATCH");

            JSONObject json = new JSONObject();
            json.put("nome", usuario.getNome());
            json.put("senha", usuario.getSenha());

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
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
        URL url = new URL(SupabaseConfig.API_URL + "/rest/v1/" + SupabaseConfig.TABLE_NAME + "?email=eq." + email);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
        conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);
        conn.setRequestProperty("Prefer", "return=representation");

        int responseCode = conn.getResponseCode();
        return responseCode;
    }

}
