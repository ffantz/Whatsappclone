package br.com.whatsappandroid.cursoandroid.whatsapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class ConfiguracaoFirebase {
    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth firebaseAuth;

    public static DatabaseReference getReferenciaFirebase() {
        if(referenciaFirebase == null)
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();

        return referenciaFirebase;
    }

    public static FirebaseAuth getFirebaseAuth(){
        if(firebaseAuth == null)
            firebaseAuth = FirebaseAuth.getInstance();

        return firebaseAuth;
    }

}
