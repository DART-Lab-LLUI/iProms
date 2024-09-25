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
import fr.thomas.menard.iproms.databinding.ActivityDepressionAnxietyBinding;

public class DepressionAnxietyActivity extends AppCompatActivity {


    ActivityDepressionAnxietyBinding binding;
    Context context;
    Resources resources;
    String langue, rating;
    int numberQuestion, total_Score, skipped_question, pourcentage = 0;
    String csv_path, date, idPatient, caseID, diagnosis, lastQuestion, categorie;

    int questionAns = 0;

    boolean exist_file = false;

    private WriteCSV writeCSVClass;

    boolean touched = false, redo_questionnaire = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDepressionAnxietyBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        reinit_questionnaire();
        getQuestion();
        getCategorie();
        retrieveGeneralInfos(categorie);
        displayText();
        finishQuestionnaire();
        listenSeekbar();
        listenBtnConfirm();
        listenBtnSkip();
    }

    private void init(){
        Intent intent = getIntent();
        langue = intent.getStringExtra("langue");
        date = intent.getStringExtra("date");
        idPatient = intent.getStringExtra("patientID");
        caseID = intent.getStringExtra("caseID");
        diagnosis = intent.getStringExtra("diagnosis");
        total_Score= intent.getIntExtra("totalScore", 0);
        questionAns = intent.getIntExtra("questionAnswered", 0);
        lastQuestion = intent.getStringExtra("lastQuestion");
        redo_questionnaire = intent.getBooleanExtra("redo_questionnaire",false);


        context = LocaleHelper.setLocale(getApplicationContext(), langue);
        resources = context.getResources();

        writeCSVClass = WriteCSV.getInstance(this);

        csv_path = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/";
        exist_file = writeCSVClass.checkFileName(idPatient+"_HADS.csv", csv_path);

        context = LocaleHelper.setLocale(getApplicationContext(), langue);
        resources = context.getResources();
        binding.txtIntro.setText(R.string.txt_intro_depression);

    }

    private void getCategorie(){
        if(numberQuestion%2==0)
            categorie = "depression";
        else
            categorie = "anxiety";
    }

    private void getQuestion(){
        String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/infos.csv";

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            // Read the header to get column indices
            int depressionColumnIndex = 7;

            List<String[]> csvEntries = reader.readAll();
            String[] firstRow = csvEntries.get(1);
            numberQuestion = Integer.parseInt(firstRow[depressionColumnIndex+3]);
            if(numberQuestion==0)
                numberQuestion = 1;


            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    private void finishQuestionnaire(){
        binding.btnSkipQuestionnaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyCSVInfos("done", "0", "skip", true, true);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("langue", langue);
                intent.putExtra("patientID", idPatient);
                intent.putExtra("caseID", caseID);
                intent.putExtra("date", date);
                intent.putExtra("diagnosis", diagnosis);
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
            write_csv("exit");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("langue", langue);
            intent.putExtra("patientID", idPatient);
            intent.putExtra("caseID", caseID);
            intent.putExtra("date", date);
            intent.putExtra("diagnosis", diagnosis);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_skip) {
            skip();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void retrieveGeneralInfos(String categorie){
        String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/infos.csv";

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            // Read the header to get column indices
            int depressionColumnIndex = 7;

            List<String[]> csvEntries = reader.readAll();
            String[] firstRow = csvEntries.get(1);

            if(categorie.equals("depression"))
                total_Score = Integer.parseInt(firstRow[depressionColumnIndex+1]);
            else
                total_Score = Integer.parseInt(firstRow[depressionColumnIndex+2]);

            skipped_question = Integer.parseInt(firstRow[depressionColumnIndex+4]);

            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void reinit_questionnaire(){
        if(redo_questionnaire){
            String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/infos.csv";

            try {
                CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

                // Create a CSVReader with FileReader and custom CSVParser
                CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                        .withCSVParser(csvParser)
                        .build();

                int qolColumnIndex = 7;


                List<String[]> csvEntries = reader.readAll();
                String[] row = csvEntries.get(1);
                row[qolColumnIndex] = "null";
                row[qolColumnIndex+1] = "0";
                row[qolColumnIndex + 2] = "0";
                row[qolColumnIndex + 3] = "0";
                row[qolColumnIndex + 4] = "0";
                row[qolColumnIndex + 5] = "0";


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

    private void displayText() {

        Integer questionID = getResources().getIdentifier("question_HADS_" + numberQuestion, "string", getPackageName());
        Integer info0 = getResources().getIdentifier("question_HADS_" + numberQuestion +"_0", "string", getPackageName());
        Integer info1 = getResources().getIdentifier("question_HADS_" + numberQuestion +"_1", "string", getPackageName());
        Integer info2 = getResources().getIdentifier("question_HADS_" + numberQuestion +"_2", "string", getPackageName());
        Integer info3 = getResources().getIdentifier("question_HADS_" + numberQuestion +"_3", "string", getPackageName());

        binding.txtQuestion.setText(getString(questionID));
        binding.txtinfo0.setText(getString(info0));
        binding.txtinfo1.setText(getString(info1));
        binding.txtinfo2.setText(getString(info2));
        binding.txtinfo3.setText(getString(info3));

        pourcentage = 100 * numberQuestion / 14;
        binding.txtPoucentageDoneDep.setText(String.valueOf(pourcentage));

        Log.d("TEST", "number question" + numberQuestion + pourcentage);

    }

    private void listenBtnConfirm(){
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                write_csv(rating);
                total_Score += Integer.parseInt(rating);

                Intent intent;
                if(numberQuestion==14){
                    modifyCSVInfos("done", String.valueOf(total_Score), categorie, false, false);
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("langue", langue);
                    intent.putExtra("patientID", idPatient);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("date", date);
                    intent.putExtra("diagnosis", diagnosis);
                    startActivity(intent);
                    finish();
                }else {
                    modifyCSVInfos("not finished", String.valueOf(total_Score), categorie, false, false);
                    intent = new Intent(getApplicationContext(), DepressionAnxietyActivity.class);
                    intent.putExtra("num_question", numberQuestion + 1);
                    intent.putExtra("patientID", idPatient);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("date", date);
                    intent.putExtra("langue", langue);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("totalScore", total_Score);
                    intent.putExtra("questionAnswered", questionAns + 1);
                    startActivity(intent);
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
        Intent intent;

        if(numberQuestion==14){
            modifyCSVInfos("done", String.valueOf(total_Score), categorie, true, false);

            intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("langue", langue);
            intent.putExtra("patientID", idPatient);
            intent.putExtra("caseID", caseID);
            intent.putExtra("date", date);
            intent.putExtra("diagnosis", diagnosis);

            startActivity(intent);
            finish();
        }else {
            modifyCSVInfos("not finished", String.valueOf(total_Score), categorie, true, false);
            intent = new Intent(getApplicationContext(), DepressionAnxietyActivity.class);
            intent.putExtra("num_question", numberQuestion + 1);
            intent.putExtra("patientID", idPatient);
            intent.putExtra("caseID", caseID);
            intent.putExtra("date", date);
            intent.putExtra("langue", langue);
            intent.putExtra("diagnosis", diagnosis);
            intent.putExtra("totalScore", total_Score);
            intent.putExtra("questionAnswered", questionAns);
            startActivity(intent);
        }
    }


    private void modifyCSVInfos(String done, String  score, String category, boolean skip, boolean skip_questionnaire){

        String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/infos.csv";

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            // Read the header to get column indices
            int depressionColumnIndex = 7;

            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[depressionColumnIndex] = done;
            if(category.equals("depression"))
                row[depressionColumnIndex+1] = score;
            else if (category.equals("skip")) {
                row[depressionColumnIndex+1] = "0";
                row[depressionColumnIndex+2] = "0";
                row[depressionColumnIndex + 3] = "0";
                row[depressionColumnIndex+4] = "0";
                row[depressionColumnIndex+5] = "0";

            }else {
                row[depressionColumnIndex + 2] = score;
            }

            if(!skip_questionnaire)
                row[depressionColumnIndex + 3] = String.valueOf(numberQuestion + 1);

            if(skip && !skip_questionnaire){
                if(category.equals("depression"))
                    row[depressionColumnIndex+4] = String.valueOf(skipped_question + 1);
                else
                    row[depressionColumnIndex+5] = String.valueOf(skipped_question + 1);


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

    private void write_csv(String rating){
        if(!exist_file){
            writeCSVClass.createAndWriteCSV_fatigue(csv_path+"/"+idPatient+"_HADS.csv", idPatient,caseID, date, String.valueOf(numberQuestion), rating);
        }else{
            writeCSVClass.writeDataCSV_fatigue(csv_path+"/"+idPatient+"_HADS.csv", String.valueOf(numberQuestion), rating);
        }
    }

}