package com.llui.iproms.App;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.llui.iproms.Enum.Language;

public class MyApplication extends Application {
    public static Language language;
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // restore language when the app starts
        loadLanguagePreference();
    }

    private void loadLanguagePreference() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String languageCode = prefs.getString("language", "en");
        switch (languageCode) {
            case "de":
                language = Language.GERMAN;
                break;
            case "en":
                language = Language.ENGLISH;
                break;
            default:
                language = Language.ENGLISH;
                break;
        }
    }

    public static MyApplication getInstance() {
        return instance;
    }

}
