package com.example.consertaaqui;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class SolicitacoesRecebidasActivity extends AppCompatActivity {

    private ListView listViewRecebidas;
    private DatabaseHelper dbHelper;
    private String emailPrestador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitacoes_recebidas);

        listViewRecebidas = findViewById(R.id.listViewRecebidas);
        dbHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
        emailPrestador = prefs.getString("email", null);

        if (emailPrestador == null) {
            Toast.makeText(this, "Erro ao identificar prestador.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        carregarSolicitacoes();
    }

    private void carregarSolicitacoes() {
        String query = "SELECT s.titulo, sol.email_cliente, sol.observacao " +
                "FROM solicitacoes sol " +
                "JOIN servicos s ON sol.id_servico = s.id " +
                "WHERE s.email_prestador = ?";

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, new String[]{emailPrestador});

        ArrayList<String> lista = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String titulo = cursor.getString(0);
                String cliente = cursor.getString(1);
                String observacao = cursor.getString(2);
                lista.add("• " + titulo + "\nCliente: " + cliente + "\nObs: " + observacao);
            } while (cursor.moveToNext());
        } else {
            lista.add("Nenhuma solicitação recebida ainda.");
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, lista
        );
        listViewRecebidas.setAdapter(adapter);
    }
}
