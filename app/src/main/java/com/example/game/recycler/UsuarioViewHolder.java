package com.example.game.recycler;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.game.R;

public class UsuarioViewHolder extends RecyclerView.ViewHolder {
    public TextView txtNome;
    public TextView txtEmail;

    public UsuarioViewHolder(View itemView) {
        super(itemView);
        txtNome = itemView.findViewById(R.id.txtNome);
        txtEmail = itemView.findViewById(R.id.txtEmail);
    }
}
