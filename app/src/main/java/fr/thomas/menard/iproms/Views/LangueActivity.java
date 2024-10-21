package fr.thomas.menard.iproms.Views;

import android.content.Intent;
import android.view.LayoutInflater;
import fr.thomas.menard.iproms.databinding.ActivityLangueBinding;

public class LangueActivity extends BaseActivity {

    private ActivityLangueBinding binding;
    private String language;

    private void listenBtnLangue(){
        binding.btnDe.setOnClickListener(v -> {
            language = "de";
            navigateToNextActivity(IdentificationActivity.class);
        });

        binding.btnEn.setOnClickListener(v -> {
            language = "en";
            navigateToNextActivity(IdentificationActivity.class);
        });
    }

    @Override
    public void init() {

    }

    @Override
    public void listenBtn() {
        listenBtnLangue();
    }

    @Override
    public void setBinding() {
        binding = ActivityLangueBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    @Override
    public void prepareIntent(Intent intent) {
        intent.putExtra("langue", language);
    }
}