package fr.thomas.menard.iproms.Views;

import android.view.LayoutInflater;

import fr.thomas.menard.iproms.Enum.Language;
import fr.thomas.menard.iproms.Model.MyApplication;
import fr.thomas.menard.iproms.databinding.ActivityLangueBinding;

public class LanguageActivity extends BaseActivity {

    private ActivityLangueBinding binding;

    private void listenBtnLanguage(){
        binding.btnDe.setOnClickListener(v -> {
            MyApplication.language = Language.GERMAN;
            navigateToNextActivity(IdentificationActivity.class);
        });

        binding.btnEn.setOnClickListener(v -> {
            MyApplication.language = Language.ENGLISH;
            navigateToNextActivity(IdentificationActivity.class);
        });
    }

    @Override
    public void init() {

    }

    @Override
    public void listenBtn() {
        listenBtnLanguage();
    }

    @Override
    public void setBinding() {
        binding = ActivityLangueBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }
}