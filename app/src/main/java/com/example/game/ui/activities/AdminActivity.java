package com.example.game.ui.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.game.database.AppDatabase;
import com.example.game.database.SupabaseService;
import com.example.game.databinding.ActivityAdminBinding;
import com.example.game.models.Usuario;
import com.example.game.recycler.UsuarioAdapter;
import com.example.game.utils.SenhaUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;


public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;
    private UsuarioAdapter usuarioAdapter;
    private AppDatabase db;
    private SupabaseService su;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final List<Usuario> listaUsuarios = new ArrayList<>();

    private Usuario usuarioExistente = null;
    private String emailOriginal = null;

    private int idNecessario = 0;
    private Usuario usuarioEditado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inicializarBanco();
        inicializarRecyclerView();
        configurarListeners();
        carregarUsuarios();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarUsuarios();
    }

    private void inicializarRecyclerView() {
        binding.recyclerViewUsuarios.setLayoutManager(new LinearLayoutManager(this));
        usuarioAdapter = new UsuarioAdapter(listaUsuarios, new UsuarioAdapter.OnUsuarioClickListener() {
            @Override
            public void onEditarClick(Usuario usuario) {
                entrarModoEdicao(usuario);
            }

            @Override
            public void onExcluirClick(Usuario usuario) {
                excluirUsuario(usuario);
            }

            @Override
            public void onAdicionarClick(Usuario usuario) {
                Toast.makeText(AdminActivity.this, "Usuário " + usuario.getNome() + " selecionado!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.recyclerViewUsuarios.setAdapter(usuarioAdapter);
    }

    private void inicializarBanco() {
        db = AppDatabase.getDatabase(this);
        su = SupabaseService.getInstance(this);
    }

    private void configurarListeners() {
        binding.btnAdicionarUsuario.setOnClickListener(v -> {
            if (usuarioEditado != null) {
                usuarioEditado = null;
                cancelarEdicao();
                binding.btnAdicionarUsuario.setText("Adicionar Usuário");
                binding.btnAtualizarUsuario.setText("Atualizar Usuario");
            } else {
                processarnovosUsuario();
            }
        });

        binding.btnAtualizarUsuario.setOnClickListener(v -> {
            if (usuarioEditado != null) {
                processarEdicaoUsuario();
            } else {
                Toast.makeText(this, "Selecione um usuário para atualizar clicando em 'Editar'", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void entrarModoEdicao(Usuario usuario) {
        //Salva o usuário selecionado
                    idNecessario = usuario.getId();
                    emailOriginal = usuario.getEmail();
                    usuarioEditado = usuario;
                    binding.nomeUsuario.setText(usuario.getNome());
                    binding.emailUsuario.setText(usuario.getEmail());
                    binding.senhaUsuario.setText(""); //Campo vazio

        // Altera os texto dos botões
            binding.btnAdicionarUsuario.setText("Cancelar Edição");
            binding.btnAtualizarUsuario.setText("Salvar Alterações");

        Toast.makeText(this, "Editando: " + usuario.getNome(), Toast.LENGTH_SHORT).show();
    }

    private void processarnovosUsuario() {
            String nome = binding.nomeUsuario.getText().toString().trim();
            String email = binding.emailUsuario.getText().toString().trim();
            String senha = binding.senhaUsuario.getText().toString().trim();

            CadastroActivity.validarUsuarioCompleto(nome, email, senha, db, -1, false,
                    new CadastroActivity.ValidationCallback() {
                        @Override
                        public void onSuccess() {
                            adicionarUsuario(nome, email, senha);
                        }

                        @Override
                        public void onError(String mensagem) {
                            runOnUiThread(() -> {
                                Toast.makeText(AdminActivity.this, mensagem, Toast.LENGTH_LONG).show();
                                reabilitarBotoes();
                            });
                        }
                    });
        }
    private void processarEdicaoUsuario() {
        String nome = binding.nomeUsuario.getText().toString().trim();
        String email = binding.emailUsuario.getText().toString().trim();
        String senha = binding.senhaUsuario.getText().toString().trim();

        CadastroActivity.validarUsuarioCompleto(nome, email, senha, db, usuarioEditado.getId(), true,
                new CadastroActivity.ValidationCallback() {
                    @Override
                    public void onSuccess() {
                        atualizarUsuario(nome, email, senha);
                    }

                    @Override
                    public void onError(String mensagem) {
                        runOnUiThread(() -> {
                            Toast.makeText(AdminActivity.this, mensagem, Toast.LENGTH_LONG).show();
                            reabilitarBotoes();
                        });
                    }
                });
    }




    private void adicionarUsuario(String nome, String email, String senha) {
        executor.execute(() -> {
            try {
                String senhaSegura = SenhaUtils.gerarSenhaSegura(senha);
                Usuario novoUsuario = new Usuario(nome, email, senhaSegura);

                db.usuarioDao().inserir(novoUsuario); //Insere no ROOM
                SupabaseService.salvarSupabase(novoUsuario); // Insere no SUPABASE

                runOnUiThread(() -> {
                    carregarUsuarios();
                    limparCampos();
                    reabilitarBotoes();
                    Toast.makeText(this, "Usuário " + nome + " adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Erro ao adicionar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    reabilitarBotoes();
                });
            }
        });
    }

    private void atualizarUsuario(String nome, String email, String senha) {
        executor.execute(() -> {
            try {

                usuarioEditado.setNome(nome);
                usuarioEditado.setEmail(email);

                //Verifica o campo antes de adicionar
                if (!senha.isEmpty()) {
                    String senhaSegura = SenhaUtils.gerarSenhaSegura(senha);
                    usuarioEditado.setSenha(senhaSegura);
                }

                int codigo = SupabaseService.atualizarUsuarioPorId(idNecessario, usuarioEditado);
                if (codigo == 200 || codigo == 204) {
                    db.usuarioDao().atualizarUsuario(usuarioEditado);
                    runOnUiThread(() -> {
                        carregarUsuarios();
                        cancelarEdicao();
                        Toast.makeText(this, "Usuário atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Erro Supabase: " + codigo, Toast.LENGTH_SHORT).show();
                        reabilitarBotoes();
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Erro ao atualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    reabilitarBotoes();
                });
            }
        });
    }

    private void cancelarEdicao() {
        usuarioExistente = null;
        limparCampos();
        reabilitarBotoes();
        Toast.makeText(this, "Edição cancelada", Toast.LENGTH_SHORT).show();
    }

    private void limparCampos() {
        binding.nomeUsuario.setText("");
        binding.emailUsuario.setText("");
        binding.senhaUsuario.setText("");
    }

    private void reabilitarBotoes() {
        binding.btnAdicionarUsuario.setEnabled(true);
        binding.btnAtualizarUsuario.setEnabled(true);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void carregarUsuarios() {
        executor.execute(() -> {
            String json = SupabaseService.listarUsuarios();
            List<Usuario> usuarios = new ArrayList<>();

            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    usuarios.add(new Usuario(
                            obj.getInt("id"),
                            obj.getString("nome"),
                            obj.getString("email"),
                            obj.getString("senha"),
                            obj.optString("criado_em", null)
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                listaUsuarios.clear();
                listaUsuarios.addAll(usuarios);
                usuarioAdapter.notifyDataSetChanged();
            });
        });
    }


    private void excluirUsuario(Usuario usuario) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir usuário")
                .setMessage("Deseja realmente excluir o usuário " + usuario.getNome() + "?")
                .setPositiveButton("Sim", (dialog, which) -> executor.execute(() -> {
                    try {
                        db.usuarioDao().delete(usuario);
                        SupabaseService.excluirPorEmail(usuario.getEmail());

                        runOnUiThread(() -> {
                            if (usuarioEditado != null && usuarioEditado.getId() == usuario.getId()) {
                                cancelarEdicao();
                            }

                            carregarUsuarios();
                            Toast.makeText(this, "Usuário " + usuario.getNome() + " excluído!", Toast.LENGTH_SHORT).show();
                        });

                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(this, "Erro ao excluir: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }))
                .setNegativeButton("Não", null)
                .show();
    }
}