package br.com.whatsappandroid.cursoandroid.whatsapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.whatsappandroid.cursoandroid.whatsapp.Adapter.MensagemAdapter;
import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Conversa;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Mensagem;

public class ConversaActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText textoMensagem;
    private ImageButton botaoEnviar;
    private ListView listView;

    private String emailUsuarioDestinatario;
    private String nomeUsuarioDestinatario;

    private String emailUsuarioRemetente;
    private String nomeUsuarioRemetente;

    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListenerMensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (Toolbar) findViewById(R.id.toolbar_conversa);
        botaoEnviar = (ImageButton) findViewById(R.id.botao_enviar);
        textoMensagem = (EditText) findViewById(R.id.edit_mensagem);
        listView = (ListView) findViewById(R.id.list_conversas);

        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        emailUsuarioRemetente = preferencias.getIdentificador();
        nomeUsuarioRemetente = preferencias.getNomeUsuarioLogado();

        Bundle extra = getIntent().getExtras();
        if(extra != null){
            nomeUsuarioDestinatario = extra.getString("nome");
            emailUsuarioDestinatario = Base64Custom.codificarBase64(extra.getString("email"));

        }

        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        mensagens = new ArrayList<>();
        adapter = new MensagemAdapter(ConversaActivity.this, mensagens);
        listView.setAdapter(adapter);

        databaseReference = ConfiguracaoFirebase.getReferenciaFirebase()
                .child("mensagens")
                .child(emailUsuarioRemetente)
                .child(emailUsuarioDestinatario);

        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mensagens.clear();

                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    mensagens.add(mensagem);

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListenerMensagem);

        botaoEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String texto = textoMensagem.getText().toString();
                if(!texto.isEmpty()){
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(emailUsuarioRemetente);
                    mensagem.setMensagem(texto);

                    Boolean retornoRemetente = salvarMensagem(emailUsuarioRemetente, emailUsuarioDestinatario, mensagem);
                    if(!retornoRemetente){
                        Toast.makeText(getApplicationContext(), "Problema ao salvar a mensagem.", Toast.LENGTH_SHORT).show();

                    }else {
                        Boolean retornoDestinatario = salvarMensagem(emailUsuarioDestinatario, emailUsuarioRemetente, mensagem);

                        if(!retornoDestinatario)
                            Toast.makeText(getApplicationContext(), "Problema ao enviar a mensagem.", Toast.LENGTH_SHORT).show();

                    }

                    Conversa conversa = new Conversa();
                    conversa.setMensagem(texto);
                    conversa.setIdUsuario(emailUsuarioDestinatario);
                    conversa.setNome(nomeUsuarioDestinatario);
                    Boolean conversaRemetente = salvarConversa(emailUsuarioRemetente, emailUsuarioDestinatario, conversa);
                    if(!conversaRemetente){
                        Toast.makeText(getApplicationContext(), "Problema ao salvar a conversa.", Toast.LENGTH_SHORT).show();

                    }else{
                        conversa = new Conversa();
                        conversa.setMensagem(texto);
                        conversa.setIdUsuario(emailUsuarioRemetente);
                        conversa.setNome(nomeUsuarioRemetente);
                        Boolean conversaDestinatario = salvarConversa(emailUsuarioDestinatario, emailUsuarioRemetente, conversa);
                        if(!conversaDestinatario)
                            Toast.makeText(getApplicationContext(), "Problema ao salvar a conversa.", Toast.LENGTH_SHORT).show();

                    }

                    textoMensagem.setText("");

                }
            }
        });
    }

    private Boolean salvarConversa(String emailRemetente, String emailDestinatario, Conversa conversa){
        try{
            databaseReference = ConfiguracaoFirebase.getReferenciaFirebase().child("conversas");
            databaseReference.child(emailRemetente).child(emailDestinatario).setValue(conversa);

            return true;
        }catch (Exception e){
            e.printStackTrace();

            return false;
        }

    }

    private Boolean salvarMensagem(String emailRemetente, String emailDestinatario, Mensagem mensagem){
        try{
            databaseReference = ConfiguracaoFirebase.getReferenciaFirebase().child("mensagens");
            databaseReference.child(emailRemetente).child(emailDestinatario).push().setValue(mensagem);

            return true;
        }catch (Exception e){
            e.printStackTrace();

            return false;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.removeEventListener(valueEventListenerMensagem);
    }
}
