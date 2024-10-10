package fr.thomas.menard.iproms.Views;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import fr.thomas.menard.iproms.Utils.LocaleHelper;
import fr.thomas.menard.iproms.databinding.ActivityIntroductionFsmcactivityBinding;

public class IntroductionFSMCActivity extends AppCompatActivity {

    ActivityIntroductionFsmcactivityBinding binding;

    String question_num_string, csv_path, date, idPatient, caseID, diagnosis ,langue, questionnaire, type;

    Context context;
    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroductionFsmcactivityBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        listenBtn();
    }

    private void init(){
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        idPatient = intent.getStringExtra("patientID");
        caseID = intent.getStringExtra("caseID");
        langue = intent.getStringExtra("langue");
        diagnosis = intent.getStringExtra("diagnosis");
        questionnaire = intent.getStringExtra("questionnaire");
        type = intent.getStringExtra("type");

        context = LocaleHelper.setLocale(getApplicationContext(), langue);
        resources = context.getResources();
    }

    private void listenBtn(){
        binding.btnIntroFSMC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FSMCActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("patientID", idPatient);
                intent.putExtra("caseID", caseID);
                intent.putExtra("diagnosis", diagnosis);
                intent.putExtra("langue", langue);
                intent.putExtra("questionnaire", questionnaire);
                intent.putExtra("type", type);
                startActivity(intent);
                finish();
            }
        });
    }
}