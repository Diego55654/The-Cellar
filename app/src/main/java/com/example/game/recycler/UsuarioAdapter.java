package com.example.game.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.game.R;
import com.example.game.models.Usuario;
import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioViewHolder> {

    private List<Usuario> usuarios;

    public UsuarioAdapter(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        holder.txtNome.setText(usuario.getNome());
        holder.txtEmail.setText(usuario.getEmail());
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }
}
