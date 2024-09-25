package fr.thomas.menard.iproms.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import fr.thomas.menard.iproms.Utils.RankingBarView;
import fr.thomas.menard.iproms.databinding.ActivityLangueBinding;

public class LangueActivity extends AppCompatActivity {

    ActivityLangueBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLangueBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        listenBtnLangue();
    }



    private void listenBtnLangue(){
        binding.btnDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), IdentificationActivity.class);
                intent.putExtra("langue", "de");
                startActivity(intent);
            }
        });
        binding.btnEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), IdentificationActivity.class);
                intent.putExtra("langue", "en");
                startActivity(intent);
            }
        });

    }
}