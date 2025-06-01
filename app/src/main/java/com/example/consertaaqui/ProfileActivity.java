package com.example.consertaaqui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtTipoServico;
    private Button btnSalvar;
    private DatabaseHelper dbHelper;
    private String emailLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        btnSalvar = findViewById(R.id.btnSalvar);

        dbHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
        emailLogado = prefs.getString("email", null);

        if (emailLogado != null) {
            String[] dados = dbHelper.buscarDadosUsuario(emailLogado);
            if (dados != null) {
                edtNome.setText(dados[0]);
                edtEmail.setText(dados[1]);
                edtTipoServico.setText(dbHelper.buscarTipoServico(emailLogado));
            }
        }

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = edtNome.getText().toString();
                String email = edtEmail.getText().toString();
                String tipoServico = edtTipoServico.getText().toString();

                if (nome.isEmpty() || email.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else {
                    boolean atualizado = dbHelper.atualizarPerfil(emailLogado, nome, email, tipoServico);
                    if (atualizado) {
                        Toast.makeText(ProfileActivity.this, "Perfil salvo com sucesso", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Erro ao atualizar perfil", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
