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
        String query = "SELECT s.id, s.titulo, sol.email_cliente, sol.observacao " +
                "FROM solicitacoes sol " +
                "JOIN servicos s ON sol.id_servico = s.id " +
                "WHERE s.email_prestador = ?";

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, new String[]{emailPrestador});
        ArrayList<String> lista = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int idServico = cursor.getInt(0);
                String titulo = cursor.getString(1);
                String cliente = cursor.getString(2);
                String observacao = cursor.getString(3);

                String avaliacaoStr = obterMediaAvaliacoes(idServico);

                lista.add("• " + titulo + "\nCliente: " + cliente + "\nObs: " + observacao + avaliacaoStr);
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

    private String obterMediaAvaliacoes(int idServico) {
        String query = "SELECT AVG(nota) as media, COUNT(*) as total FROM avaliacoes WHERE id_servico = ?";
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, new String[]{String.valueOf(idServico)});
        String resultado = "";

        if (cursor.moveToFirst()) {
            double media = cursor.getDouble(cursor.getColumnIndexOrThrow("media"));
            int total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
            if (total > 0) {
                resultado = String.format("\n⭐ Média: %.1f (%d avaliações)", media, total);
            }
        }

        cursor.close();
        return resultado;
    }
}
