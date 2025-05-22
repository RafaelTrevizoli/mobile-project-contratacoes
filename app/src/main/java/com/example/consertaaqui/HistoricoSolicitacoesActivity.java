package com.example.consertaaqui;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class HistoricoSolicitacoesActivity extends AppCompatActivity {

    private ListView listViewHistorico;
    private DatabaseHelper dbHelper;
    private String emailCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_solicitacoes);

        listViewHistorico = findViewById(R.id.listViewHistorico);
        dbHelper = new DatabaseHelper(this);
        emailCliente = getIntent().getStringExtra("email_usuario");

        carregarHistorico();
    }

    private void carregarHistorico() {
        String query = "SELECT s.titulo, sol.observacao FROM " + DatabaseHelper.TABLE_SOLICITACOES + " sol " +
                "JOIN " + DatabaseHelper.TABLE_SERVICOS + " s ON sol.id_servico = s.id " +
                "WHERE sol.email_cliente = ?";

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, new String[]{emailCliente});

        ArrayList<String> lista = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String titulo = cursor.getString(0);
                String observacao = cursor.getString(1);
                lista.add("• " + titulo + "\nObs: " + observacao);
            } while (cursor.moveToNext());
        } else {
            lista.add("Nenhuma solicitação encontrada.");
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, lista
        );
        listViewHistorico.setAdapter(adapter);
    }
}
