package com.example.consertaaqui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private String tipoUsuario, emailUsuario;
    private LinearLayout layoutCliente, layoutPrestador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Recuperar dados enviados pelo login
        emailUsuario = getIntent().getStringExtra("email_usuario");
        tipoUsuario = new DatabaseHelper(this).buscarTipoUsuario(emailUsuario);

        layoutCliente = findViewById(R.id.layoutCliente);
        layoutPrestador = findViewById(R.id.layoutPrestador);

        // Exibir conforme tipo
        if ("Cliente".equalsIgnoreCase(tipoUsuario)) {
            layoutCliente.setVisibility(View.VISIBLE);
            layoutPrestador.setVisibility(View.GONE);
        } else if ("Prestador".equalsIgnoreCase(tipoUsuario)) {
            layoutCliente.setVisibility(View.GONE);
            layoutPrestador.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Tipo de usuário desconhecido", Toast.LENGTH_SHORT).show();
        }

        // Ações dos botões (exemplo)
        Button btnSolicitar = findViewById(R.id.btnSolicitarServico);
        Button btnHistorico = findViewById(R.id.btnHistoricoCliente);
        Button btnCadastrarServico = findViewById(R.id.btnCadastrarServico);
        Button btnServicosPrestador = findViewById(R.id.btnMeusServicos);

        btnSolicitar.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SolicitarServicoActivity.class);
            intent.putExtra("email_usuario", emailUsuario);
            startActivity(intent);
        });

        btnHistorico.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HistoricoSolicitacoesActivity.class);
            intent.putExtra("email_usuario", emailUsuario);
            startActivity(intent);
        });

        btnCadastrarServico.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CadastrarServicoActivity.class);
            intent.putExtra("email_usuario", emailUsuario);
            startActivity(intent);
        });

        btnServicosPrestador.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MeusServicosActivity.class);
            intent.putExtra("email_usuario", emailUsuario);
            startActivity(intent);
        });
    }
}
