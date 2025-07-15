package fr.thomas.menard.iproms.Views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;

import fr.thomas.menard.iproms.App.MyApplication;
import fr.thomas.menard.iproms.Enum.Language;
import fr.thomas.menard.iproms.Utils.LocaleHelper;
import fr.thomas.menard.iproms.databinding.ActivityLangueBinding;

public class LanguageActivity extends BaseActivity {

    private ActivityLangueBinding binding;


    // method to load language from SharedPreferences
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load previously selected language from SharedPreferences
        loadlanguagePreference();
    }

    private void listenBtnLanguage(){
        binding.btnDe.setOnClickListener(v -> {
            MyApplication.language = Language.GERMAN;
            saveLanguagePreference(Language.GERMAN);

            // before navigateToNextAcitvity(), ensuring locale set correctly before next activity stars
            LocaleHelper.setLocale(this, Language.GERMAN.getLanguage());
            navigateToNextActivity(IdentificationActivity.class);
        });

        binding.btnEn.setOnClickListener(v -> {
            MyApplication.language = Language.ENGLISH;
            saveLanguagePreference(Language.ENGLISH);

            // before navigateToNextAcitvity(), ensuring locale set correctly before next activity stars
            LocaleHelper.setLocale(this, Language.ENGLISH.getLanguage());
            navigateToNextActivity(IdentificationActivity.class);
        });
    }

    // loads previously selected language from SharedPreferences
    private void loadlanguagePreference() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String languageCode = prefs.getString("language", "en");

        switch (languageCode) {
            case "de":
                MyApplication.language = Language.GERMAN;
                break;
            case "en":
                MyApplication.language = Language.ENGLISH;
        }
    }

    // save selected language to SharedPreferences
    private void saveLanguagePreference(Language language) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("language", language.name().toLowerCase()).apply();
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