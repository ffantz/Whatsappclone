package br.com.whatsappandroid.cursoandroid.whatsapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;

import br.com.whatsappandroid.cursoandroid.whatsapp.R;
import br.com.whatsappandroid.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Base64Custom;
import br.com.whatsappandroid.cursoandroid.whatsapp.helper.Preferencias;
import br.com.whatsappandroid.cursoandroid.whatsapp.model.Usuario;

public class CadastroUsuarioActivity extends AppCompatActivity {
    private EditText editNome;
    private EditText editEmail;
    private EditText editSenha;
    private Button botaoCadastro;
    private Usuario usuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        editNome = (EditText) findViewById(R.id.edit_cadastro_nome);
        editEmail = (EditText) findViewById(R.id.edit_cadastro_email);
        editSenha = (EditText) findViewById(R.id.edit_cadastro_senha);
        botaoCadastro = (Button) findViewById(R.id.botao_cadastrar);

        botaoCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario = new Usuario();
                usuario.setNome(editNome.getText().toString());
                usuario.setEmail(editEmail.getText().toString());
                usuario.setSenha(editSenha.getText().toString());
                cadastrarUsuario();

            }
        });

    }

    private void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Usuário cadastrado!", Toast.LENGTH_SHORT).show();
                    String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setId(identificadorUsuario);
                    usuario.salvar();

                    Preferencias preferencias = new Preferencias(getApplicationContext());
                    preferencias.salvarDados(identificadorUsuario, usuario.getNome());

                    abrirUsuarioLogado();

                }else {
                    String erroExcecao = "";

                    try{
                        throw task.getException();

                    } catch (FirebaseAuthWeakPasswordException e) {
                        erroExcecao = "Digite uma senha mais forte, com o mínimo de 6 caracteres, letras e números";

                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "Digite um email válido";

                    } catch (FirebaseAuthUserCollisionException e) {
                        erroExcecao = "Email já cadastrado";

                    } catch (Exception e){
                        erroExcecao = "Erro ao cadastrar Usuário";
                        e.printStackTrace();

                    }

                    Toast.makeText(getApplicationContext(), erroExcecao, Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void abrirUsuarioLogado(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();

    }
}
