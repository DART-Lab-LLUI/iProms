package fr.thomas.menard.iproms.Model;

import android.app.Application;
import android.content.res.Resources;

import fr.thomas.menard.iproms.Enum.Language;
import fr.thomas.menard.iproms.Enum.Type;

public class MyApplication extends Application {
    public static Language language;
    public static Type type;
    private static MyApplication instance;
    private static Resources resources;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
