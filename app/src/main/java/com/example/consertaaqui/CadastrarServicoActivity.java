package com.example.consertaaqui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CadastrarServicoActivity extends AppCompatActivity {

    private EditText edtTitulo, edtDescricao;
    private Button btnCadastrarServico;
    private DatabaseHelper dbHelper;
    private String emailPrestador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_servico);

        dbHelper = new DatabaseHelper(this);

        edtTitulo = findViewById(R.id.edtTitulo);
        edtDescricao = findViewById(R.id.edtDescricao);
        btnCadastrarServico = findViewById(R.id.btnCadastrarServico);

        // Recebe o email do prestador (enviado por HomeActivity)
        emailPrestador = getIntent().getStringExtra("email_usuario");

        btnCadastrarServico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = edtTitulo.getText().toString();
                String descricao = edtDescricao.getText().toString();

                if (titulo.isEmpty() || descricao.isEmpty()) {
                    Toast.makeText(CadastrarServicoActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else {
                    boolean sucesso = dbHelper.cadastrarServico(titulo, descricao, emailPrestador);
                    if (sucesso) {
                        Toast.makeText(CadastrarServicoActivity.this, "Serviço cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                        edtTitulo.setText("");
                        edtDescricao.setText("");
                    } else {
                        Toast.makeText(CadastrarServicoActivity.this, "Erro ao cadastrar serviço", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
