package fr.thomas.menard.iproms.Views;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.LocaleHelper;
import fr.thomas.menard.iproms.Utils.WriteCSV;
import fr.thomas.menard.iproms.databinding.ActivityFsmcactivityBinding;

public class FSMCActivity extends AppCompatActivity {

    ActivityFsmcactivityBinding binding;

    Context context;
    Resources resources;
    String langue, rating, questionnaire, type;
    int numberQuestion, total_Score, pourcentage = 0;
    String question_num_string, csv_path, date, idPatient, caseID, diagnosis;

    int questionAns, skipped_question;

    boolean exist_file = false, touched = false;

    private WriteCSV writeCSVClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFsmcactivityBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        listenSeekbar();
        retrieveGeneralInfos();
        displayQuestions();
        listenBtnSkip();
        listenBtnConfirm();
        finishQuestionnaire();
    }

    private void init(){
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        idPatient = intent.getStringExtra("patientID");
        caseID = intent.getStringExtra("caseID");
        total_Score = intent.getIntExtra("totalScore", 0);
        langue = intent.getStringExtra("langue");
        diagnosis = intent.getStringExtra("diagnosis");
        questionnaire = intent.getStringExtra("questionnaire");
        type = intent.getStringExtra("type");

        writeCSVClass = WriteCSV.getInstance(this);
        if(type.equals("first")){
            csv_path = getExternalFilesDir(null).getAbsolutePath() + "/" + idPatient + "/first/";

        }else{
            csv_path = getExternalFilesDir(null).getAbsolutePath() + "/" + idPatient + "/second/";

        }
        exist_file = writeCSVClass.checkFileName(idPatient + "_FSMC.csv", csv_path);


        context = LocaleHelper.setLocale(getApplicationContext(), langue);
        resources = context.getResources();

    }

    private void listenSeekbar() {
        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                rating = String.valueOf(progress);
                binding.txtRating.setText(rating);
                binding.btnConfirm.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                binding.btnConfirm.setVisibility(View.VISIBLE);
                if(!touched){
                    rating = String.valueOf(2);
                    binding.txtRating.setText(rating);
                    touched = true;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            //modifyLast_general_Question();
            write_csv("exit");
            //modifyCSVInfos("not finished", String.valueOf(total_Score), qol);
            Intent intent = new Intent(getApplicationContext(), OptionnalQuestionnairesActivity.class);
            intent.putExtra("langue", langue);
            intent.putExtra("patientID", idPatient);
            intent.putExtra("caseID", caseID);
            intent.putExtra("date", date);
            intent.putExtra("diagnosis", diagnosis);
            intent.putExtra("type", type);

            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_skip) {
            skip();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void displayQuestions(){
        Integer questionID = getResources().getIdentifier("question_fsmc_" + numberQuestion, "string", getPackageName());
        Integer txtinfo_0 = getResources().getIdentifier("txt_info_fsmc_0", "string", getPackageName());
        Integer txtinfo_1 = getResources().getIdentifier("txt_info_fsmc_1", "string", getPackageName());
        Integer txtinfo_2 = getResources().getIdentifier("txt_info_fsmc_2", "string", getPackageName());
        Integer txtinfo_3 = getResources().getIdentifier("txt_info_fsmc_3", "string", getPackageName());
        Integer txtinfo_4 = getResources().getIdentifier("txt_info_fsmc_4", "string", getPackageName());


        binding.txtQuestion.setText(getString(questionID));
        binding.txtinfo0.setText(getString(txtinfo_0));
        binding.txtinfo1.setText(getString(txtinfo_1));
        binding.txtinfo2.setText(getString(txtinfo_2));
        binding.txtinfo3.setText(getString(txtinfo_3));
        binding.txtinfo4.setText(getString(txtinfo_4));

    }

    private void retrieveGeneralInfos(){
        String csvFilePath;
        if(type.equals("first")){
            csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/first/infos.csv";
        }else{
            csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/second/infos.csv";
        }
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            // Read the header to get column indices
            int qolColumnIndex = 70;

            List<String[]> csvEntries = reader.readAll();
            String[] firstRow = csvEntries.get(1);

            numberQuestion = Integer.parseInt((firstRow[qolColumnIndex+2]));
            Log.d("TEST", "VALUUE " + numberQuestion);
            pourcentage = 100 * numberQuestion/ 20;
            binding.txtPoucentageDone.setText(String.valueOf(pourcentage));
            total_Score = Integer.parseInt(firstRow[qolColumnIndex+1]);
            skipped_question = Integer.parseInt(firstRow[qolColumnIndex+3]);

            if(numberQuestion==0){
                numberQuestion = 1;
            }

            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void listenBtnConfirm(){
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_csv(rating);
                total_Score = total_Score + Integer.parseInt(rating);
                questionAns = questionAns + 1;
                Log.d("TEST", "question number " + numberQuestion);

                //81 question
                Intent intent;
                if(numberQuestion==20){

                    modifyCSVInfos("done", String.valueOf(total_Score), false, false);

                    intent = new Intent(getApplicationContext(), OptionnalQuestionnairesActivity.class);
                    intent.putExtra("langue", langue);
                    intent.putExtra("patientID", idPatient);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("date", date);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("type", type);

                }
                else {
                    modifyCSVInfos("not finished", String.valueOf(total_Score), false, false);
                    intent = new Intent(getApplicationContext(), FSMCActivity.class);
                    intent.putExtra("num_question", numberQuestion);
                    intent.putExtra("patientID", idPatient);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("date", date);
                    intent.putExtra("langue", langue);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("type", type);

                }


                startActivity(intent);
                finish();

            }
        });
    }


    private void listenBtnSkip(){
        binding.btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skip();
            }
        });
    }

    private void skip(){
        write_csv("skip");
        Intent intent;

        if(numberQuestion==20){
            modifyCSVInfos("done", String.valueOf(total_Score), true, false);

            intent = new Intent(getApplicationContext(), OptionnalQuestionnairesActivity.class);
            intent.putExtra("langue", langue);
            intent.putExtra("patientID", idPatient);
            intent.putExtra("caseID", caseID);
            intent.putExtra("date", date);
            intent.putExtra("diagnosis", diagnosis);
            intent.putExtra("type", type);

        }else{
            modifyCSVInfos("not finished", String.valueOf(total_Score), true, false);

            intent = new Intent(getApplicationContext(), FSMCActivity.class);
            intent.putExtra("langue", langue);
            intent.putExtra("patientID", idPatient);
            intent.putExtra("caseID", caseID);
            intent.putExtra("date", date);
            intent.putExtra("diagnosis", diagnosis);
            intent.putExtra("type", type);

        }
        startActivity(intent);
        finish();
    }

    private void finishQuestionnaire() {
        binding.btnSkipQuestionnaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyCSVInfos("done", "0", false, true);
                Intent intent = new Intent(getApplicationContext(), OptionnalQuestionnairesActivity.class);
                intent.putExtra("langue", langue);
                intent.putExtra("patientID", idPatient);
                intent.putExtra("caseID", caseID);
                intent.putExtra("date", date);
                intent.putExtra("diagnosis", diagnosis);
                intent.putExtra("type", type);

                startActivity(intent);
                finish();

            }
        });
    }

    private void modifyCSVInfos(String done, String  score, boolean skip, boolean skip_questionnaire){

        String csvFilePath;
        if(type.equals("first")){
            csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/first/infos.csv";
        }else{
            csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/second/infos.csv";
        }
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            int qolColumnIndex = 70;

            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[qolColumnIndex] = done;
            row[qolColumnIndex+1] = score;

            row[qolColumnIndex + 2] = String.valueOf(numberQuestion + 1);

            if(skip)
                row[qolColumnIndex+3] = String.valueOf(skipped_question + 1);

            if(skip_questionnaire){
                row[qolColumnIndex] = done;
                row[qolColumnIndex+1] = score;
                row[qolColumnIndex + 2] = "0";
                row[qolColumnIndex + 3] = "0";
            }


            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            writer.writeAll(csvEntries);
            writer.close();


            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }


    }


    private void write_csv(String rating){
        if(!exist_file){
            writeCSVClass.createAndWriteCSV_fatigue(csv_path+"/"+idPatient+"_FSMC.csv", idPatient,caseID, date, String.valueOf(numberQuestion), rating);
        }else{
            writeCSVClass.writeDataCSV_fatigue(csv_path+"/"+idPatient+"_FSMC.csv", String.valueOf(numberQuestion), rating);
        }
    }
}