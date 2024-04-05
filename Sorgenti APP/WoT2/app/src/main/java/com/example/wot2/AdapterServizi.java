package com.example.wot2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdapterServizi extends RecyclerView.Adapter<AdapterServizi.ReportViewHolder> {

    private Context con;
    private List<Servizio> servizio;
    private OnItemClickListener listener;
    public static final String SHARED_PREFS="sharedPrefs";

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder{
        public TextView id;
        public TextView nome;
        public TextView metadata;
        public Button modifica;
        public Button elimina;
        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            id=itemView.findViewById(R.id.ID);
            nome=itemView.findViewById(R.id.Nome);
            metadata=itemView.findViewById(R.id.metadata);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public AdapterServizi(Context con, List<Servizio> servizio){
        this.con=con;
        this.servizio=servizio;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.servizio,parent,false);
        ReportViewHolder rvh= new ReportViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, final int position) {
        final Servizio rd= this.servizio.get(position);
        String indirizzo="Indirizzo: "+rd.getIndirizzo()+":"+Integer.toString(rd.getPorta());
        String nome="Nome Servizio: "+rd.getNome();
        holder.id.setText(indirizzo);
        holder.nome.setText(nome);
      //  holder.metadata.setText(rd.getTD());
    }

    @Override
    public int getItemCount() {
        return this.servizio.size();
    }
}

