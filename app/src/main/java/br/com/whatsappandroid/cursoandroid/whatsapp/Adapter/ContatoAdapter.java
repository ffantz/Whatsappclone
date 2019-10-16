package br.com.whatsappandroid.cursoandroid.whatsapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Contato;

public class ContatoAdapter extends ArrayAdapter<Contato> {
    private ArrayList<Contato> contatos;
    private Context context;
    private TextView textoNome;
    private TextView textoEmail;

    public ContatoAdapter(@NonNull Context c, ArrayList<Contato> objects) {
        super(c, 0, objects);
        this.contatos = objects;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        if(contatos != null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = layoutInflater.inflate(R.layout.lista_contato, parent, false);

            textoNome = (TextView) view.findViewById(R.id.text_titulo);
            textoEmail = (TextView) view.findViewById(R.id.text_subtitulo);

            Contato contato = contatos.get(position);
            textoNome.setText(contato.getNome());
            textoEmail.setText(contato.getEmail());

        }

        return view;
    }
}
