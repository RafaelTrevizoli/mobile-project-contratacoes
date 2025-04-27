package com.example.consertaaqui;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private EditText edtNome, edtEmail;
    private Button btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        btnSalvar = findViewById(R.id.btnSalvar);

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = edtNome.getText().toString();
                String email = edtEmail.getText().toString();

                if (nome.isEmpty() || email.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Lógica para salvar as informações do perfil
                    Toast.makeText(ProfileActivity.this, "Perfil salvo com sucesso", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
