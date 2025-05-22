package com.example.consertaaqui;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class SolicitarServicoActivity extends AppCompatActivity {

    private ListView listViewServicos;
    private DatabaseHelper dbHelper;
    private String emailCliente;
    private ArrayList<Integer> listaIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_servico);

        listViewServicos = findViewById(R.id.listViewServicosCliente);
        dbHelper = new DatabaseHelper(this);
        emailCliente = getIntent().getStringExtra("email_usuario");

        carregarServicos();

        listViewServicos.setOnItemClickListener((parent, view, position, id) -> {
            int idServico = listaIds.get(position);
            exibirDialogoSolicitacao(idServico);
        });
    }

    private void carregarServicos() {
        Cursor cursor = dbHelper.getReadableDatabase().query(
                DatabaseHelper.TABLE_SERVICOS,
                new String[]{"id", "titulo", "descricao"},
                null, null, null, null, null
        );

        ArrayList<String> lista = new ArrayList<>();
        listaIds = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao"));
                lista.add("• " + titulo + "\n" + descricao);
                listaIds.add(id);
            } while (cursor.moveToNext());
        } else {
            lista.add("Nenhum serviço disponível no momento.");
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, lista
        );
        listViewServicos.setAdapter(adapter);
    }

    private void exibirDialogoSolicitacao(int idServico) {
        View view = getLayoutInflater().inflate(R.layout.dialog_solicitar_servico, null);
        EditText edtObservacao = view.findViewById(R.id.edtObservacao);

        new AlertDialog.Builder(this)
                .setTitle("Solicitar Serviço")
                .setView(view)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    String observacao = edtObservacao.getText().toString();
                    if (observacao.isEmpty()) observacao = "(sem observações)";

                    ContentValues values = new ContentValues();
                    values.put("id_servico", idServico);
                    values.put("email_cliente", emailCliente);
                    values.put("observacao", observacao);

                    long result = dbHelper.getWritableDatabase()
                            .insert(DatabaseHelper.TABLE_SOLICITACOES, null, values);

                    if (result != -1) {
                        Toast.makeText(this, "Solicitação enviada com sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Erro ao enviar solicitação.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}