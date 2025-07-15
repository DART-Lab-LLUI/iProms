package fr.thomas.menard.iproms.App;

import android.app.Application;
import android.app.Presentation;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import fr.thomas.menard.iproms.Enum.Language;
import fr.thomas.menard.iproms.Enum.Type;
import fr.thomas.menard.iproms.Utils.CustomExceptionHandler;

public class MyApplication extends Application {
    public static Language language;
    private static Type type;  // Keep for runtime use
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Restore type when the app starts
        type = getStoredType();

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

    // Save type persistently
    public static void setType(Type newType) {
        type = newType;
        SharedPreferences prefs = instance.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        prefs.edit().putString("type", newType.name()).apply();
    }

    public static Type getType() {
        if(type == null){
            return getStoredType();
        }

        return type;
    }

    // Retrieve type when needed
    public static Type getStoredType() {
        SharedPreferences prefs = instance.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String typeName = prefs.getString("type", null);
        return typeName != null ? Type.valueOf(typeName) : Type.FIRST; // Default value
    }
}
