package fr.thomas.menard.iproms.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.LocaleHelper;
import fr.thomas.menard.iproms.Utils.WriteCSV;
import fr.thomas.menard.iproms.databinding.ActivityIdentificationBinding;

public class IdentificationActivity extends AppCompatActivity {

    ActivityIdentificationBinding binding;

    String date, patientID, caseID, langue, diagnosis;

    Context context;
    Resources resources;



    private WriteCSV writeCSVClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIdentificationBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        listenBtnConfirn();
        listenDiagnosis();


    }

    private void init(){
        Intent intent = getIntent();
        langue = intent.getStringExtra("langue");
        diagnosis  ="";
        context = LocaleHelper.setLocale(IdentificationActivity.this, langue);
        resources = context.getResources();
        binding.txtWelcome.setText(resources.getString(R.string.welcome));
        binding.txtSide.setText(resources.getString(R.string.diagnosis));



        writeCSVClass = WriteCSV.getInstance(this);

    }

    private void listenBtnConfirn(){
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date =new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                patientID = binding.APatientTxtIdPatient.getText().toString().trim();
                caseID = binding.APatientTxtIdCase.getText().toString().trim();

                Log.d("TEST", "id" + patientID);
                if(patientID.length()!=7 || caseID.length()!=7){
                    Toast.makeText(context, "Please, write a correct PID & FID", Toast.LENGTH_SHORT).show();
                } else if (diagnosis.equals("")) {
                    Toast.makeText(context, "Please select a diagnosis", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(getApplicationContext(), IntroductionActivity.class);
                    if(diagnosis.equals("Others")){
                        diagnosis = binding.editOtherDiagnosis.getText().toString();
                    }
                    intent.putExtra("date", date);
                    intent.putExtra("langue", langue);
                    intent.putExtra("patientID", patientID);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("diagnosis", diagnosis);
                    startActivity(intent);
                    finish();
                }


            }
        });
    }


    private void listenDiagnosis(){
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rdBtnParki) {
                    diagnosis = "Parkinson";
                    binding.tilOther.setVisibility(View.GONE);
                } else if (checkedId == R.id.rdBtnStroke) {
                    diagnosis = "Stroke";
                    binding.tilOther.setVisibility(View.GONE);
                } else if (checkedId == R.id.rdBtnMS) {
                    diagnosis = "MS";
                    binding.tilOther.setVisibility(View.GONE);
                }else if (checkedId == R.id.rdBtnOther) {
                    diagnosis = "Others";
                    binding.tilOther.setVisibility(View.VISIBLE);
                } else {
                    diagnosis = "";
                }
            }
        });

    }
}