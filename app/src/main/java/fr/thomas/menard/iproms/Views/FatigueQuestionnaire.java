package fr.thomas.menard.iproms.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

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
import fr.thomas.menard.iproms.databinding.ActivityFatigueQuestionnaireBinding;

public class FatigueQuestionnaire extends AppCompatActivity {

    ActivityFatigueQuestionnaireBinding binding;

    String rating;
    String langue, type;

    int numberQuestion;
    int total_Score = 0;

    private WriteCSV writeCSVClass;

    boolean exist_file = false;
    String idPatient, caseID, date, question_num_string, diagnosis, lastQuestion;

    int pourcentage = 0;

    int skipped_question = 0;

    String csv_path;

    Context context;
    Resources resources;

    boolean touched = false, redo_questionnaire=false, restard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFatigueQuestionnaireBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        reinit_questionnaire();
        retrieveInfos();
        displayQuestion();
        listenSeekbar();
        listenBtnConfirm();
        listenBtnSkip();
        finishQuestionnaire();
        translateText();

    }

    private void init() {
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        idPatient = intent.getStringExtra("patientID");
        caseID = intent.getStringExtra("caseID");
        total_Score = intent.getIntExtra("totalScore", 0);
        langue = intent.getStringExtra("langue");
        diagnosis = intent.getStringExtra("diagnosis");
        lastQuestion = intent.getStringExtra("lastQuestion");
        redo_questionnaire = intent.getBooleanExtra("redo_questionnaire", false);
        type = intent.getStringExtra("type");


        writeCSVClass = WriteCSV.getInstance(this);

        if(type.equals("first")){
            csv_path = getExternalFilesDir(null).getAbsolutePath() + "/" + idPatient + "/first/";

        }else{
            csv_path = getExternalFilesDir(null).getAbsolutePath() + "/" + idPatient + "/second/";

        }
        exist_file = writeCSVClass.checkFileName(idPatient + "_FSS.csv", csv_path);



    }

    private void translateText(){
        context = LocaleHelper.setLocale(getApplicationContext(), langue);
        resources = context.getResources();

        binding.ratingTextLangue.setText(R.string.your_rating);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    private void retrieveInfos(){
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
            int qolColumnIndex = 3;

            List<String[]> csvEntries = reader.readAll();
            String[] firstRow = csvEntries.get(1);

            total_Score = Integer.parseInt(firstRow[qolColumnIndex+1]);
            numberQuestion = Integer.parseInt((firstRow[qolColumnIndex+2]));
            skipped_question = Integer.parseInt(firstRow[qolColumnIndex+3]);


            if(numberQuestion==0)
                numberQuestion = 1;


            pourcentage = 100 * (numberQuestion) / 9;
            binding.txtPoucentageDone.setText(String.valueOf(pourcentage));
            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void reinit_questionnaire(){
        if(redo_questionnaire){
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

                int qolColumnIndex = 3;


                List<String[]> csvEntries = reader.readAll();
                String[] row = csvEntries.get(1);
                row[qolColumnIndex] = "null";
                row[qolColumnIndex+1] = "0";
                row[qolColumnIndex + 2] = "0";
                row[qolColumnIndex + 3] = "0";


                CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
                writer.writeAll(csvEntries);
                writer.close();


                reader.close();

            } catch (IOException | CsvException e) {
                Log.d("TEST", "infos " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private void finishQuestionnaire(){
        binding.btnSkipQuestionnaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyCSVInfos("done", "0", false, true);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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

    private void listenBtnConfirm(){
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_csv(rating);
                total_Score = total_Score + Integer.parseInt(rating);

                if(numberQuestion==9){
                    modifyCSVInfos("done", String.valueOf(total_Score), false, false);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("langue", langue);
                    intent.putExtra("patientID", idPatient);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("date", date);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("type", type);
                    startActivity(intent);
                    finish();
                }else {
                    modifyCSVInfos("not finished", String.valueOf(total_Score), false, false);
                    Intent intent = new Intent(getApplicationContext(), FatigueQuestionnaire.class);
                    intent.putExtra("patientID", idPatient);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("date", date);
                    intent.putExtra("langue", langue);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("totalScore", total_Score);
                    intent.putExtra("type", type);
                    startActivity(intent);
                    finish();
                }
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
        if(numberQuestion==9){
            modifyCSVInfos("done", String.valueOf(total_Score), true, false);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("langue", langue);
            intent.putExtra("patientID", idPatient);
            intent.putExtra("caseID", caseID);
            intent.putExtra("date", date);
            intent.putExtra("diagnosis", diagnosis);
            intent.putExtra("type", type);

            startActivity(intent);
            finish();
        }else {
            modifyCSVInfos("not finished", String.valueOf(total_Score), true, false);
            Intent intent = new Intent(getApplicationContext(), FatigueQuestionnaire.class);
            intent.putExtra("patientID", idPatient);
            intent.putExtra("caseID", caseID);
            intent.putExtra("date", date);
            intent.putExtra("langue", langue);
            intent.putExtra("diagnosis", diagnosis);
            intent.putExtra("totalScore", total_Score);
            intent.putExtra("type", type);
            startActivity(intent);
            finish();
        }
    }

    private void displayQuestion(){
        if(numberQuestion==1){
            question_num_string = "Question 1";
            binding.txtQuestion.setText(R.string.question1_fatigue);
        } else if (numberQuestion ==2) {
            question_num_string = "Question 2";
            binding.txtQuestion.setText(R.string.question2_fatigue);
        }else if (numberQuestion ==3) {
            question_num_string = "Question 3";
            binding.txtQuestion.setText(R.string.question3_fatigue);
        }else if (numberQuestion ==4) {
            question_num_string = "Question 4";
            binding.txtQuestion.setText(R.string.question4_fatigue);
        }else if (numberQuestion ==5) {
            question_num_string = "Question 5";
            binding.txtQuestion.setText(R.string.question5_fatigue);
        }else if (numberQuestion ==6) {
            question_num_string = "Question 6";
            binding.txtQuestion.setText(R.string.question6_fatigue);
        }else if (numberQuestion ==7) {
            question_num_string = "Question 7";
            binding.txtQuestion.setText(R.string.question7_fatigue);
        }else if (numberQuestion ==8) {
            question_num_string = "Question 8";
            binding.txtQuestion.setText(R.string.question8_fatigue);
        }else if (numberQuestion ==9) {
            question_num_string = "Question 9";
            binding.txtQuestion.setText(R.string.question9_fatigue);
        }
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

            int qolColumnIndex = 3;


            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[qolColumnIndex] = done;
            row[qolColumnIndex+1] = score;
            if(!skip_questionnaire)
                row[qolColumnIndex + 2] = String.valueOf(numberQuestion +1);

            if(skip && !skip_questionnaire)
                row[qolColumnIndex+3] = String.valueOf(skipped_question + 1);

            if(skip_questionnaire){
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

    private void listenSeekbar(){
        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                rating = String.valueOf(progress + 1);
                binding.txtRating.setText(rating);
                binding.btnConfirm.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                binding.btnConfirm.setVisibility(View.VISIBLE);
                if(!touched){
                    rating = String.valueOf(4);
                    binding.txtRating.setText(rating);
                    touched = true;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void write_csv(String rating){
        if(!exist_file){
            writeCSVClass.createAndWriteCSV_fatigue(csv_path+"/"+idPatient+"_FSS.csv", idPatient,caseID, date, String.valueOf(numberQuestion), rating);
        }else{
            writeCSVClass.writeDataCSV_fatigue(csv_path+"/"+idPatient+"_FSS.csv",  String.valueOf(numberQuestion), rating);
        }
    }
}