package com.example.game.database;

import com.example.game.models.Usuario;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SupabaseService {

    public static void salvarSupabase(Usuario usuario) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                URL url = new URL(SupabaseConfig.API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("apikey", SupabaseConfig.API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SupabaseConfig.API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Prefer", "return=representation");
                conn.setDoOutput(true);

                //Converte o objeto usuario para o formato Json
                String jsonInput = usuario.toJson();

                //Envia ao Supabase
                OutputStream os = conn.getOutputStream();
                os.write(jsonInput.getBytes());
                os.flush();
                os.close();

                //Validacao que retorna sucesso ou erro
                int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String inputLine;
                                while ((inputLine = in.readLine()) != null) {
                                    response.append(inputLine);
                                }
                    in.close();
                    System.out.println("Usuário salvo no Supabase: " + response);
                        } else {
                            System.err.println("Erro Supabase: " + responseCode);
                        }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
