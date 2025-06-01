package com.example.consertaaqui;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ServiceListActivity extends AppCompatActivity {

    private ListView listViewServicos;
    private EditText edtBusca;
    private ArrayList<String> listaServicos;
    private ArrayList<String> listaFiltrada;
    private ArrayAdapter<String> adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        listViewServicos = findViewById(R.id.listViewServicosCliente);
        edtBusca = findViewById(R.id.edtBuscaSolicitacoes);
        dbHelper = new DatabaseHelper(this);

        listaServicos = new ArrayList<>();
        listaFiltrada = new ArrayList<>();

        carregarServicos();

        edtBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarServicos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void carregarServicos() {
        Cursor cursor = dbHelper.getReadableDatabase().query(
                DatabaseHelper.TABLE_SERVICOS,
                new String[]{"titulo", "descricao"},
                null, null, null, null, null
        );

        listaServicos.clear();

        if (cursor.moveToFirst()) {
            do {
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao"));
                listaServicos.add("• " + titulo + "\n" + descricao);
            } while (cursor.moveToNext());
        } else {
            listaServicos.add("Nenhum serviço disponível.");
        }

        cursor.close();

        listaFiltrada.clear();
        listaFiltrada.addAll(listaServicos);

        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, listaFiltrada
        );
        listViewServicos.setAdapter(adapter);
    }

    private void filtrarServicos(String texto) {
        listaFiltrada.clear();
        for (String item : listaServicos) {
            if (item.toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
