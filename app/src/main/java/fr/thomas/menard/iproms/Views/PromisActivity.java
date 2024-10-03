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
import fr.thomas.menard.iproms.databinding.ActivityPromisBinding;

public class PromisActivity extends AppCompatActivity {

    ActivityPromisBinding binding;

    String rating;
    String langue;

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
        binding = ActivityPromisBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        translateText();
        retrieveInfos();
        displayQuestion();
        displayLegend();
        reinit_questionnaire();
        listenBtnConfirm();
        listenSeekbar();
        listenBtnSkip();
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
        lastQuestion = intent.getStringExtra("lastQuestion");
        redo_questionnaire = intent.getBooleanExtra("redo_questionnaire", false);


        writeCSVClass = WriteCSV.getInstance(this);

        csv_path = getExternalFilesDir(null).getAbsolutePath() + "/" + idPatient + "/";
        exist_file = writeCSVClass.checkFileName(idPatient + "_PROMIS.csv", csv_path);

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
        String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/infos.csv";

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            // Read the header to get column indices
            int qolColumnIndex = 17;

            List<String[]> csvEntries = reader.readAll();
            String[] firstRow = csvEntries.get(1);

            total_Score = Integer.parseInt(firstRow[qolColumnIndex+1]);
            numberQuestion = Integer.parseInt((firstRow[qolColumnIndex+2]));
            skipped_question = Integer.parseInt(firstRow[qolColumnIndex+3]);


            if(numberQuestion==0)
                numberQuestion = 1;


            pourcentage = 100 * (numberQuestion) / 10;
            binding.txtPoucentageDone.setText(String.valueOf(pourcentage));
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

                int qolColumnIndex = 17;


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

                if(numberQuestion==10){
                    modifyCSVInfos("done", String.valueOf(total_Score), false, false);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("langue", langue);
                    intent.putExtra("patientID", idPatient);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("date", date);
                    intent.putExtra("diagnosis", diagnosis);
                    startActivity(intent);
                    finish();
                }else {
                    modifyCSVInfos("not finished", String.valueOf(total_Score), false, false);
                    Intent intent = new Intent(getApplicationContext(), PromisActivity.class);
                    intent.putExtra("patientID", idPatient);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("date", date);
                    intent.putExtra("langue", langue);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("totalScore", total_Score);
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

            startActivity(intent);
            finish();
        }else {
            modifyCSVInfos("not finished", String.valueOf(total_Score), true, false);
            Intent intent = new Intent(getApplicationContext(), PromisActivity.class);
            intent.putExtra("patientID", idPatient);
            intent.putExtra("caseID", caseID);
            intent.putExtra("date", date);
            intent.putExtra("langue", langue);
            intent.putExtra("diagnosis", diagnosis);
            intent.putExtra("totalScore", total_Score);
            startActivity(intent);
            finish();
        }
    }

    private void displayQuestion(){
        if(numberQuestion==1){
            question_num_string = "Question 1";
            binding.txtQuestion.setText(R.string.promis_1);
        } else if (numberQuestion ==2) {
            question_num_string = "Question 2";
            binding.txtQuestion.setText(R.string.promis_2);
        }else if (numberQuestion ==3) {
            question_num_string = "Question 3";
            binding.txtQuestion.setText(R.string.promis_3);
        }else if (numberQuestion ==4) {
            question_num_string = "Question 4";
            binding.txtQuestion.setText(R.string.promis_4);
        }else if (numberQuestion ==5) {
            question_num_string = "Question 5";
            binding.txtQuestion.setText(R.string.promis_5);
        }else if (numberQuestion ==6) {
            question_num_string = "Question 6";
            binding.txtQuestion.setText(R.string.promis_6);
        }else if (numberQuestion ==7) {
            question_num_string = "Question 7";
            binding.txtQuestion.setText(R.string.promis_7);
        }else if (numberQuestion ==8) {
            question_num_string = "Question 8";
            binding.txtQuestion.setText(R.string.promis_8);
        }else if (numberQuestion ==9) {
            question_num_string = "Question 9";
            binding.txtQuestion.setText(R.string.promis_8);
        }else if (numberQuestion ==10) {
            question_num_string = "Question 10";
            binding.txtQuestion.setText(R.string.promis_10);
        }
    }

    private void displayLegend(){
        if(numberQuestion==1 || numberQuestion==2 || numberQuestion==3 || numberQuestion==4 || numberQuestion==5 || numberQuestion==9){
            binding.txt0.setText(R.string.promis_legend_1to59_1);
            binding.txt1.setText(R.string.promis_legend_1to59_2);
            binding.txt2.setText(R.string.promis_legend_1to59_3);
            binding.txt3.setText(R.string.promis_legend_1to59_4);
            binding.txt6.setText(R.string.promis_legend_1to59_5);
        } else if (numberQuestion==6 || numberQuestion==7 || numberQuestion==8 || numberQuestion==10) {
            binding.txt0.setText(R.string.promis_legend_6to10_1);
            binding.txt1.setText(R.string.promis_legend_6to10_2);
            binding.txt2.setText(R.string.promis_legend_6to10_3);
            binding.txt3.setText(R.string.promis_legend_6to10_4);
            binding.txt6.setText(R.string.promis_legend_6to10_5);
        }
    }

    private void modifyCSVInfos(String done, String  score, boolean skip, boolean skip_questionnaire){

        String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/infos.csv";

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            int qolColumnIndex = 17;


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
                    rating = String.valueOf(3);
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
            writeCSVClass.createAndWriteCSV_fatigue(csv_path+"/"+idPatient+"_PROMIS.csv", idPatient,caseID, date, String.valueOf(numberQuestion), rating);
        }else{
            writeCSVClass.writeDataCSV_fatigue(csv_path+"/"+idPatient+"_PROMIS.csv",  String.valueOf(numberQuestion), rating);
        }
    }
}