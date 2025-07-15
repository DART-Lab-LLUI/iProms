package fr.thomas.menard.iproms.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;


import java.util.Objects;

import fr.thomas.menard.iproms.App.MyApplication;
import fr.thomas.menard.iproms.Enum.Language;
import fr.thomas.menard.iproms.Interfaces.IActivityCreator;
import fr.thomas.menard.iproms.Interfaces.IIntentHandler;
import fr.thomas.menard.iproms.Model.Patient;
import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.LocaleHelper;

/**
 * BaseActivity is an abstract class that provides common functionality
 * for all activities in the application.
 */
public abstract class BaseActivity extends AppCompatActivity implements IIntentHandler, IActivityCreator {
    protected Patient patientInfo;
    private Class<?> navBackArrowActivityClass;

    @Override
    protected void attachBaseContext(Context newBase) {
        // Load language preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(newBase);
        String langCode = prefs.getString("language", "en");

        // Apply locale using your LocaleHelper
        Context contextWithLocale = LocaleHelper.setLocale(newBase, langCode);

        // Pass the wrapped context to super
        super.attachBaseContext(contextWithLocale);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String langCode = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("language", "en");
        MyApplication.language = Language.fromCode(langCode);

        patientInfo = Patient.getPatient();
        retrieveIntent();
        setBinding();
        init();
        listenBtn();
    }

    protected void enableNavBackArrow(Class<?> navBackArrowActivityClass){
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        this.navBackArrowActivityClass = navBackArrowActivityClass;
    }

    public void navigateToNextActivity(Class<?> nextClass) {
        Intent intent = new Intent(this, nextClass);
        prepareIntent(intent);
        this.startActivity(intent);
        this.finish();
    }

    public void navigateToNextActivityWithoutFinish(Class<?> nextClass) {
        Intent intent = new Intent(this, nextClass);
        this.startActivity(intent);
    }

    public void navigateToNextActivityWithoutFinish(Intent intent) {
        // If you still want prepareIntent(...) to run (e.g. to
        // add common extras), leave the next line in. Otherwise you
        // can omit it and just startActivity(intent).
        prepareIntent(intent);
        this.startActivity(intent);
    }

    protected void retrieveIntent() {
        Intent intent = this.getIntent();
        if (intent != null) {
            processReceivedIntent(intent);
        }
    }


    @Override
    public void prepareIntent(Intent intent) {
    }

    @Override
    public void processReceivedIntent(Intent intent) {
    }
}
