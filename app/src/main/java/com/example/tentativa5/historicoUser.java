package com.example.tentativa5;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.ViewGroup; // certifique-se de ter esse import no topo

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class historicoUser extends AppCompatActivity {

    private AppCompatButton btnMostrarDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historico_user);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnMostrarDados = findViewById(R.id.btn_mostrar_dados);

        btnMostrarDados.setOnClickListener(view -> abrirPopupComProgress(view));
    }

    private void abrirPopupComProgress(View anchorView) {
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_historico, null);

        TextView txtDados = popupView.findViewById(R.id.txt_dados);
        ProgressBar progressBar = popupView.findViewById(R.id.progressBar);
        AppCompatButton btnFechar = popupView.findViewById(R.id.btn_fechar);

        // Mostrar a progress bar e esconder o texto
        progressBar.setVisibility(View.VISIBLE);
        txtDados.setVisibility(View.GONE);

        // Criar o popup
        PopupWindow popup = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popup.setElevation(10);
        popup.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        // Botão fechar
        btnFechar.setOnClickListener(v -> popup.dismiss());

        // Buscar dados do Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("dados_enviados");
        ref.limitToLast(9).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                txtDados.setVisibility(View.VISIBLE);

                if (snapshot.exists()) {
                    StringBuilder dadosFormatados = new StringBuilder();

                    for (DataSnapshot child : snapshot.getChildren()) {
                        for (DataSnapshot campo : child.getChildren()) {
                            String chave = campo.getKey();
                            String valor = String.valueOf(campo.getValue());
                            dadosFormatados.append(chave).append(": ").append(valor).append("\n");
                        }
                    }

                    txtDados.setText(dadosFormatados.toString());
                } else {
                    txtDados.setText("Nenhum dado encontrado.");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                txtDados.setVisibility(View.VISIBLE);
                txtDados.setText("Erro ao carregar dados.");
            }
        });
    }
}
