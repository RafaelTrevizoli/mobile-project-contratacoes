package com.example.consertaaqui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ServiceListActivity extends AppCompatActivity {

    private Button btnVerDetalhes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        btnVerDetalhes = findViewById(R.id.btnVerDetalhes);
        btnVerDetalhes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para ver os detalhes do serviço
            }
        });
    }
}
