package fr.thomas.menard.iproms.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import java.util.Objects;

import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.WriteCSV;
import fr.thomas.menard.iproms.databinding.ActivityIntroductionBinding;

public class IntroductionActivity extends AppCompatActivity {

    ActivityIntroductionBinding binding;
    String patientID, date, caseID, langue, diagnosis, questionnaire;

    private WriteCSV writeCSVClass;

    boolean exist_file = false;

    String info_csv_path_first, info_csv_path_second, csv_path_PID;

    String fatigue, depression, bdi, promis, qol, fsmc, sleep, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroductionBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        checkUser(patientID);
        listenBtnConfirm();

    }

    private void init() {
        Intent intent = getIntent();
        langue = intent.getStringExtra("langue");
        patientID = intent.getStringExtra("patientID");
        caseID = intent.getStringExtra("caseID");
        diagnosis = intent.getStringExtra("diagnosis");
        date = intent.getStringExtra("date");

        writeCSVClass = WriteCSV.getInstance(this);
    }

    private void listenBtnConfirm() {
        binding.btnConfirmIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("langue", langue);
                intent.putExtra("patientID", patientID);
                intent.putExtra("caseID", caseID);
                intent.putExtra("diagnosis", diagnosis);
                intent.putExtra("type",type);
                startActivity(intent);
                finish();
            }
        });
    }


    private void retrieveInfos() {

        String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/" + patientID + "/first/infos.csv";

        Log.d("TEST", "path " + csvFilePath);
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            // Read the header to get column indices
            int fatigueColumnIndex = 3;
            int depressionColumnIndex = 7;
            int bdiIndex = 13;
            int promisIndex = 17;
            int qolColumnIndex = 21;
            int sleepIndex = 66;
            int FSMCIndex = 70;

            List<String[]> csvEntries = reader.readAll();
            String[] firstRow = csvEntries.get(1);

            date = firstRow[2];
            fatigue = firstRow[fatigueColumnIndex];
            depression = firstRow[depressionColumnIndex];
            bdi = firstRow[bdiIndex];
            promis = firstRow[promisIndex];
            qol = firstRow[qolColumnIndex];
            sleep = firstRow[sleepIndex];
            fsmc = firstRow[FSMCIndex];


            reader.close();


        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean suptwoWeeks() {
        // Parse the timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        Date fileDate = null;
        try {
            fileDate = sdf.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Calculate the difference between the current date and the file date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, +14); // Two weeks ago
        Date twoWeeksAgo = calendar.getTime();


        // Return true if the file date is before two weeks ago
        return fileDate == null || fileDate.before(twoWeeksAgo);
    }

    private String checkTypeScreening() {
        retrieveInfos();
        Log.d("TEST", "test " + fatigue + depression + bdi + promis + sleep + fsmc);
        if (fatigue.equals("done") && depression.equals("done") && bdi.equals("done") && promis.equals("done") && sleep.equals("done") && fsmc.equals("done")) {
                return "second";
            } else if (!suptwoWeeks()) {
                return "second";
            }
         else {
            return "first";
        }


    }

    private void checkUser(String patientID) {

        csv_path_PID = getExternalFilesDir(null).getAbsolutePath() + "/" + patientID + "/";

        File folder = new File(csv_path_PID);

        // Check if the folder exists
        if (!folder.exists() || !folder.isDirectory()) {
            type = "first";
            // If the folder does not exist, create it
            if (folder.mkdirs()) {
                File f2 = new File(csv_path_PID + "/first");
                if (!f2.exists() || !f2.isDirectory()) {
                    if(f2.mkdirs()){
                        info_csv_path_first = csv_path_PID+ "/first/infos.csv";
                        writeCSVClass.createAndWriteInfos(info_csv_path_first, patientID, caseID, date,
                                "null", "0", "0", "0",
                                "null", "0", "0", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0","0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0"
                        );
                    }
                }



            }

        }
        else {

            if(checkTypeScreening().equals("first")){

                type = "first";
                
            }else{
                type = "second";
                File f2 = new File(csv_path_PID + "/second");
                if (!f2.exists() || !f2.isDirectory()) {
                    if(f2.mkdirs()){
                        info_csv_path_first = csv_path_PID+ "/second/infos.csv";
                        writeCSVClass.createAndWriteInfos(info_csv_path_first, patientID, caseID, date,
                                "null", "0", "0", "0",
                                "null", "0", "0", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0","0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0",
                                "null", "0", "0", "0"
                        );
                    }
                }
            }
        }
    }
}



