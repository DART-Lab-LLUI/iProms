package fr.thomas.menard.iproms.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.WriteCSV;
import fr.thomas.menard.iproms.databinding.ActivityIntroductionBinding;

public class IntroductionActivity extends AppCompatActivity {

    ActivityIntroductionBinding binding;
    String patientID, date, caseID, langue, diagnosis, questionnaire;

    private WriteCSV writeCSVClass;

    boolean exist_file = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroductionBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        checkDate();
        listenBtnConfirm();

    }

    private void init(){
        Intent intent = getIntent();
        langue = intent.getStringExtra("langue");
        patientID = intent.getStringExtra("patientID");
        caseID = intent.getStringExtra("caseID");
        diagnosis = intent.getStringExtra("diagnosis");

        writeCSVClass = WriteCSV.getInstance(this);
    }

    private void checkDate(){
        String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+patientID+"/infos.csv";

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> csvEntries = reader.readAll();
            String[] firstRow = csvEntries.get(1);

            date = firstRow[2];

            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void listenBtnConfirm(){
        binding.btnConfirmIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("langue", langue);
                intent.putExtra("patientID", patientID);
                intent.putExtra("caseID", caseID);
                intent.putExtra("diagnosis", diagnosis);
                startActivity(intent);
                finish();
            }
        });
    }
}