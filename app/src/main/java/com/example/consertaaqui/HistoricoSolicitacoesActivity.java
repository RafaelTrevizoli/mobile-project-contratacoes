package com.example.consertaaqui;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class HistoricoSolicitacoesActivity extends AppCompatActivity {

    private ListView listViewHistorico;
    private DatabaseHelper dbHelper;
    private String emailCliente;
    private ArrayList<Integer> listaIdsServicos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_solicitacoes);

        listViewHistorico = findViewById(R.id.listViewHistorico);
        dbHelper = new DatabaseHelper(this);
        emailCliente = getIntent().getStringExtra("email_usuario");

        carregarHistorico();

        listViewHistorico.setOnItemClickListener((parent, view, position, id) -> {
            if (listaIdsServicos != null && position < listaIdsServicos.size()) {
                int idServico = listaIdsServicos.get(position);

                // Verifica se já existe avaliação
                Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                        "SELECT nota, comentario FROM avaliacoes WHERE id_servico = ? AND email_cliente = ?",
                        new String[]{String.valueOf(idServico), emailCliente}
                );

                if (cursor.moveToFirst()) {
                    int nota = cursor.getInt(0);
                    String comentario = cursor.getString(1);
                    exibirDialogoVisualizacao(nota, comentario);
                } else {
                    exibirDialogoAvaliacao(idServico);
                }

                cursor.close();
            }
        });
    }

    private void carregarHistorico() {
        String query = "SELECT s.id, s.titulo, sol.observacao FROM " + DatabaseHelper.TABLE_SOLICITACOES + " sol " +
                "JOIN " + DatabaseHelper.TABLE_SERVICOS + " s ON sol.id_servico = s.id " +
                "WHERE sol.email_cliente = ?";

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, new String[]{emailCliente});

        ArrayList<String> lista = new ArrayList<>();
        listaIdsServicos = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int idServico = cursor.getInt(0);
                String titulo = cursor.getString(1);
                String observacao = cursor.getString(2);
                lista.add("• " + titulo + "\nObs: " + observacao);
                listaIdsServicos.add(idServico);
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

    private void exibirDialogoAvaliacao(int idServico) {
        View view = getLayoutInflater().inflate(R.layout.dialog_avaliar_servico, null);
        EditText edtNota = view.findViewById(R.id.edtNota);
        EditText edtComentario = view.findViewById(R.id.edtComentario);

        new AlertDialog.Builder(this)
                .setTitle("Avaliar Serviço")
                .setView(view)
                .setPositiveButton("Enviar Avaliação", (dialog, which) -> {
                    String notaStr = edtNota.getText().toString().trim();
                    String comentario = edtComentario.getText().toString().trim();

                    int nota = 0;
                    try {
                        nota = Integer.parseInt(notaStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Nota inválida.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (nota < 1 || nota > 5) {
                        Toast.makeText(this, "Nota deve ser entre 1 e 5.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (comentario.isEmpty()) comentario = "(sem comentário)";

                    boolean sucesso = dbHelper.cadastrarAvaliacao(idServico, emailCliente, nota, comentario);
                    if (sucesso) {
                        Toast.makeText(this, "Avaliação enviada com sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Erro ao enviar avaliação.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void exibirDialogoVisualizacao(int nota, String comentario) {
        String mensagem = "Nota: " + nota + "\nComentário: " + comentario;

        new AlertDialog.Builder(this)
                .setTitle("Avaliação Existente")
                .setMessage(mensagem)
                .setPositiveButton("Fechar", null)
                .show();
    }
}
