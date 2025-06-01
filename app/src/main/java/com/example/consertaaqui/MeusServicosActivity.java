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

public class MeusServicosActivity extends AppCompatActivity {

    private ListView listViewServicos;
    private DatabaseHelper dbHelper;
    private String emailPrestador;
    private ArrayList<String> listaServicos;
    private ArrayList<Integer> listaIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_servicos);

        listViewServicos = findViewById(R.id.listViewServicos);
        dbHelper = new DatabaseHelper(this);
        emailPrestador = getIntent().getStringExtra("email_usuario");

        carregarServicos();

        listViewServicos.setOnItemClickListener((parent, view, position, id) -> {
            int idServico = listaIds.get(position);
            exibirDialogoOpcoes(idServico);
        });
    }

    private void carregarServicos() {
        Cursor cursor = dbHelper.listarServicosPorPrestador(emailPrestador);
        listaServicos = new ArrayList<>();
        listaIds = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao"));

                String avaliacaoStr = obterMediaAvaliacoes(id);

                listaServicos.add("• " + titulo + "\n" + descricao + avaliacaoStr);
                listaIds.add(id);
            } while (cursor.moveToNext());
        } else {
            listaServicos.add("Nenhum serviço encontrado.");
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, listaServicos
        );
        listViewServicos.setAdapter(adapter);
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

    private void exibirDialogoOpcoes(int idServico) {
        new AlertDialog.Builder(this)
                .setTitle("Escolha uma ação")
                .setItems(new CharSequence[]{"Editar", "Excluir"}, (dialog, which) -> {
                    if (which == 0) {
                        exibirDialogoEdicao(idServico);
                    } else {
                        dbHelper.excluirServico(idServico);
                        carregarServicos();
                        Toast.makeText(this, "Serviço excluído", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void exibirDialogoEdicao(int idServico) {
        View view = getLayoutInflater().inflate(R.layout.dialog_editar_servico, null);
        EditText edtNovoTitulo = view.findViewById(R.id.edtNovoTitulo);
        EditText edtNovaDescricao = view.findViewById(R.id.edtNovaDescricao);

        new AlertDialog.Builder(this)
                .setTitle("Editar Serviço")
                .setView(view)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String novoTitulo = edtNovoTitulo.getText().toString();
                    String novaDescricao = edtNovaDescricao.getText().toString();

                    boolean sucesso = dbHelper.editarServico(idServico, novoTitulo, novaDescricao);
                    if (sucesso) {
                        Toast.makeText(this, "Serviço atualizado", Toast.LENGTH_SHORT).show();
                        carregarServicos();
                    } else {
                        Toast.makeText(this, "Erro ao atualizar serviço", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
