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
import fr.thomas.menard.iproms.databinding.ActivityQualityofLifeBinding;

public class QualityofLifeActivity extends AppCompatActivity {

    ActivityQualityofLifeBinding binding;

    Context context;
    Resources resources;
    String langue, rating;
    int numberQuestion, total_Score, total_score_qol, pourcentage = 0, numberQuestion_qol;
    int skipped_question, skipped_question_qol;
    String question_num_string, csv_path, date, idPatient, caseID, diagnosis, lastQuestion;

    int questionAns = 0, question_general_Ans;

    boolean exist_file = false, touched = false, restard = false, redo_questionnaire = false;

    String qol;

    private WriteCSV writeCSVClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQualityofLifeBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        reinit_questionnaire(qol);
        retrieveGeneralInfos();
        retrieveInfos(qol);
        displayText();
        finishQuestionnaire();
        listenSeekbar();
        listenBtnConfirm();
        listenBtnSkip();
    }

    private void init(){
        Intent intent = getIntent();
        date = intent.getStringExtra("date");
        idPatient = intent.getStringExtra("patientID");
        caseID = intent.getStringExtra("caseID");
        total_Score = intent.getIntExtra("totalScore", 0);
        langue = intent.getStringExtra("langue");
        diagnosis = intent.getStringExtra("diagnosis");
        questionAns = intent.getIntExtra("questionAnswered", 0);
        lastQuestion = intent.getStringExtra("lastQuestion");
        qol = intent.getStringExtra("qol");
        restard = intent.getBooleanExtra("restard", false);
        redo_questionnaire = intent.getBooleanExtra("redo_questionnaire",false);


        context = LocaleHelper.setLocale(getApplicationContext(), langue);
        resources = context.getResources();

        writeCSVClass = WriteCSV.getInstance(this);
        csv_path = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/";
        exist_file = writeCSVClass.checkFileName(idPatient+"_QOL.csv", csv_path);


        context = LocaleHelper.setLocale(getApplicationContext(), langue);
        resources = context.getResources();
        binding.txtIntro.setText(R.string.txt_intro_depression);



    }

    private void reinit_questionnaire(String qol){
        if(redo_questionnaire){
            String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/infos.csv";

            try {
                CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

                // Create a CSVReader with FileReader and custom CSVParser
                CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                        .withCSVParser(csvParser)
                        .build();

                int qolColumnIndex = 21;

                switch (qol) {
                    case "qol1":
                        qolColumnIndex += 4;
                        break;
                    case "qol2":
                        qolColumnIndex += 8;
                        break;
                    case "qol3":
                        qolColumnIndex += 12;
                        break;
                    case "qol4":
                        qolColumnIndex += 16;
                        break;
                    case "qol5":
                        qolColumnIndex += 20;
                        break;
                    case "qol6":
                        qolColumnIndex += 24;
                        break;
                    case "qol7":
                        qolColumnIndex += 28;
                        break;
                    case "qol8":
                        qolColumnIndex += 32;
                        break;
                    case "qol9":
                        qolColumnIndex += 36;
                        break;
                    case "qol10":
                        qolColumnIndex += 40;
                        break;
                }
                List<String[]> csvEntries = reader.readAll();
                String[] row = csvEntries.get(1);
                row[qolColumnIndex] = "null";
                row[qolColumnIndex+1] = "0";
                row[qolColumnIndex + 2] = "0";
                row[qolColumnIndex + 3] = "0";

                if(qol.equals("qol7"))
                    row[15] = String.valueOf(Integer.parseInt(row[15]) - 9);
                else{
                    row[15] = String.valueOf(Integer.parseInt(row[15]) - 8);
                }
                if(Integer.parseInt(row[15])<0){
                    row[15] = "0";
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
    }

    private void finishQuestionnaire(){
        binding.btnSkipQuestionnaire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyCSVInfos("done", "0", qol, true, true);
                Intent intent = new Intent(getApplicationContext(), OptionnalQuestionnairesActivity.class);
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
            write_csv("exit");
            Intent intent = new Intent(getApplicationContext(), OptionnalQuestionnairesActivity.class);
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

    private void retrieveGeneralInfos(){
        String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/infos.csv";

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
            int qolColumnIndex = 21;

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> csvEntries = reader.readAll();
            String[] firstRow = csvEntries.get(1);

            question_general_Ans = Integer.parseInt((firstRow[qolColumnIndex+2]));

            total_Score = Integer.parseInt(firstRow[qolColumnIndex+1]);
            skipped_question = Integer.parseInt(firstRow[qolColumnIndex+3]);

            if(question_general_Ans == 0)
                question_general_Ans = 1;
            numberQuestion_qol = question_general_Ans;

            pourcentage = 100 * numberQuestion_qol/ 73;
            binding.txtPoucentageDoneQOL.setText(String.valueOf(pourcentage));

            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void retrieveInfos(String qol){
        String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/infos.csv";
        int qolColumnIndex = 21;

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            switch (qol) {
                case "qol1":
                    qolColumnIndex += 4;
                    break;
                case "qol2":
                    qolColumnIndex += 8;
                    break;
                case "qol3":
                    qolColumnIndex += 12;
                    break;
                case "qol4":
                    qolColumnIndex += 16;
                    break;
                case "qol5":
                    qolColumnIndex += 20;
                    break;
                case "qol7":
                    qolColumnIndex += 28;
                    break;
                case "qol8":
                    qolColumnIndex += 32;
                    break;
                case "qol9":
                    qolColumnIndex += 36;
                    break;
                case "qol10":
                    qolColumnIndex += 40;
                    break;
            }


            List<String[]> csvEntries = reader.readAll();
            String[] firstRow = csvEntries.get(1);

            total_score_qol = Integer.parseInt(firstRow[qolColumnIndex+1]);
            numberQuestion = Integer.parseInt((firstRow[qolColumnIndex+2]));
            skipped_question_qol = Integer.parseInt(firstRow[qolColumnIndex+3]);

            if(numberQuestion==0)
                numberQuestion = 1;


            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayText(){
        Integer questionID = getResources().getIdentifier("question_"+qol+"_" + numberQuestion, "string", getPackageName());
        binding.txtQuestion.setText(getString(questionID));
        if(qol.equals("qol1")){
            binding.txtDomain.setText(R.string.emotional_behavioral_dyscontrol);
            binding.txtContext.setText(R.string.in_the_past_7_days);
        } else if (qol.equals("qol2")) {
            binding.txtDomain.setText(R.string.sleep_disturbance);
            binding.txtContext.setText(R.string.in_the_past_7_days);
        }else if (qol.equals("qol3")) {
            binding.txtDomain.setText(R.string.ability_in_social_roles);
            binding.txtContext.setText(R.string.in_the_past_7_days);
        }else if (qol.equals("qol4")) {
            binding.txtDomain.setText(R.string.satisfaction_with_social_act);
            binding.txtContext.setText(R.string.in_the_past_7_days);
            binding.txtinfo0.setText(R.string.satisfaction_info_0);
            binding.txtinfo1.setText(R.string.satisfaction_info_1);
            binding.txtinfo2.setText(R.string.satisfaction_info_2);
            binding.txtinfo3.setText(R.string.satisfaction_info_3);
            binding.txtinfo4.setText(R.string.satisfaction_info_4);
        }else if (qol.equals("qol5")) {
            binding.txtDomain.setText(R.string.applied_cognition_general_concerns);
            if(numberQuestion<3){
                binding.txtContext.setText(R.string.in_the_past_7_days);
            }else{
                binding.txtContext.setText(R.string.how_difficult);
                binding.txtinfo0.setText(R.string.applied_exec_function_info_1);
                binding.txtinfo1.setText(R.string.applied_exec_function_info_2);
                binding.txtinfo2.setText(R.string.applied_exec_function_info_3);
                binding.txtinfo3.setText(R.string.applied_exec_function_info_4);
                binding.txtinfo4.setText(R.string.applied_exec_function_info_5);
            }

        }else if (qol.equals("qol7")) {
            binding.txtDomain.setText(R.string.positiv_effect_well_being);
            binding.txtContext.setText(R.string.lately);
        }else if (qol.equals("qol8")) {
            binding.txtDomain.setText(R.string.applied_cognition_executive_function);
            binding.txtContext.setText(R.string.lately);
        }else if (qol.equals("qol9")) {
            binding.txtDomain.setText(R.string.upper_function);
            binding.txtContext.setText("");
            binding.txtinfo0.setText(R.string.upper_info_1);
            binding.txtinfo1.setText(R.string.uppper_info_2);
            binding.txtinfo2.setText(R.string.uppper_info_3);
            binding.txtinfo3.setText(R.string.uppper_info_4);
            binding.txtinfo4.setText(R.string.uppper_info_5);
        }else if (qol.equals("qol10")) {
            binding.txtDomain.setText(R.string.lower_function);
            binding.txtContext.setText("");
            binding.txtinfo0.setText(R.string.upper_info_1);
            binding.txtinfo1.setText(R.string.uppper_info_2);
            binding.txtinfo2.setText(R.string.uppper_info_3);
            binding.txtinfo3.setText(R.string.uppper_info_4);
            binding.txtinfo4.setText(R.string.uppper_info_5);
        }
    }


    private void listenBtnConfirm(){
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberQuestion_qol += 1;
                write_csv(rating);
                total_score_qol = total_score_qol + Integer.parseInt(rating);
                total_Score = total_Score + Integer.parseInt(rating);
                questionAns = questionAns + 1;


                modifyCSVGeneralQOL("not finished", String.valueOf(total_Score), String.valueOf(question_general_Ans +1), false);
                Log.d("TEST", "log" + numberQuestion_qol + "_" + qol);
                //81 question
                Intent intent;
                if(qol.equals("qol7")){
                    if(numberQuestion==9){

                        modifyCSVInfos("done", String.valueOf(total_score_qol), qol, false, false);

                        intent = new Intent(getApplicationContext(), OptionnalQuestionnairesActivity.class);
                        intent.putExtra("langue", langue);
                        intent.putExtra("patientID", idPatient);
                        intent.putExtra("caseID", caseID);
                        intent.putExtra("date", date);
                        intent.putExtra("diagnosis", diagnosis);
                    }
                    else {
                        modifyCSVInfos("not finished", String.valueOf(total_score_qol), qol, false, false);
                        intent = new Intent(getApplicationContext(), QualityofLifeActivity.class);
                        intent.putExtra("num_question", numberQuestion);
                        intent.putExtra("patientID", idPatient);
                        intent.putExtra("caseID", caseID);
                        intent.putExtra("date", date);
                        intent.putExtra("langue", langue);
                        intent.putExtra("diagnosis", diagnosis);
                        intent.putExtra("totalScore", total_score_qol);
                        intent.putExtra("questionAnswered", questionAns);
                        intent.putExtra("qol", qol);
                    }
                }else{
                    if(numberQuestion==8){

                        modifyCSVInfos("done", String.valueOf(total_score_qol), qol, false, false);

                        intent = new Intent(getApplicationContext(), OptionnalQuestionnairesActivity.class);
                        intent.putExtra("langue", langue);
                        intent.putExtra("patientID", idPatient);
                        intent.putExtra("caseID", caseID);
                        intent.putExtra("date", date);
                        intent.putExtra("diagnosis", diagnosis);
                    }
                    else {
                        modifyCSVInfos("not finished", String.valueOf(total_score_qol), qol, false, false);
                        intent = new Intent(getApplicationContext(), QualityofLifeActivity.class);
                        intent.putExtra("num_question", numberQuestion);
                        intent.putExtra("patientID", idPatient);
                        intent.putExtra("caseID", caseID);
                        intent.putExtra("date", date);
                        intent.putExtra("langue", langue);
                        intent.putExtra("diagnosis", diagnosis);
                        intent.putExtra("totalScore", total_score_qol);
                        intent.putExtra("questionAnswered", questionAns);
                        intent.putExtra("qol", qol);
                    }
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
        numberQuestion_qol += 1;
        Intent intent;
        modifyCSVGeneralQOL("not finished", String.valueOf(total_Score), String.valueOf(question_general_Ans + 1), true);
        if(qol.equals("qol7")){
            if(numberQuestion==9){

                modifyCSVInfos("done", String.valueOf(total_score_qol), qol, true, false);

                intent = new Intent(getApplicationContext(), OptionnalQuestionnairesActivity.class);
                intent.putExtra("langue", langue);
                intent.putExtra("patientID", idPatient);
                intent.putExtra("caseID", caseID);
                intent.putExtra("date", date);
                intent.putExtra("diagnosis", diagnosis);
            }
            else {
                modifyCSVInfos("not finished", String.valueOf(total_score_qol), qol, true, false);
                intent = new Intent(getApplicationContext(), QualityofLifeActivity.class);
                intent.putExtra("num_question", numberQuestion);
                intent.putExtra("patientID", idPatient);
                intent.putExtra("caseID", caseID);
                intent.putExtra("date", date);
                intent.putExtra("langue", langue);
                intent.putExtra("diagnosis", diagnosis);
                intent.putExtra("totalScore", total_score_qol);
                intent.putExtra("questionAnswered", questionAns);
                intent.putExtra("qol", qol);
            }
        }else{
            if(numberQuestion==8){

                modifyCSVInfos("done", String.valueOf(total_score_qol), qol, true, false);

                intent = new Intent(getApplicationContext(), OptionnalQuestionnairesActivity.class);
                intent.putExtra("langue", langue);
                intent.putExtra("patientID", idPatient);
                intent.putExtra("caseID", caseID);
                intent.putExtra("date", date);
                intent.putExtra("diagnosis", diagnosis);
            }
            else {
                modifyCSVInfos("not finished", String.valueOf(total_score_qol), qol, true, false);
                intent = new Intent(getApplicationContext(), QualityofLifeActivity.class);
                intent.putExtra("num_question", numberQuestion);
                intent.putExtra("patientID", idPatient);
                intent.putExtra("caseID", caseID);
                intent.putExtra("date", date);
                intent.putExtra("langue", langue);
                intent.putExtra("diagnosis", diagnosis);
                intent.putExtra("totalScore", total_score_qol);
                intent.putExtra("questionAnswered", questionAns);
                intent.putExtra("qol", qol);
            }
        }
        startActivity(intent);
        finish();
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


    private void modifyCSVInfos(String done, String  score, String qol, boolean skip, boolean skip_questionnaire){

        String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/infos.csv";

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();
            int qolColumnIndex = 17;

            switch (qol) {
                case "qol1":
                    qolColumnIndex += 4;
                    break;
                case "qol2":
                    qolColumnIndex += 8;
                    break;
                case "qol3":
                    qolColumnIndex += 12;
                    break;
                case "qol4":
                    qolColumnIndex += 16;
                    break;
                case "qol5":
                    qolColumnIndex += 20;
                    break;
                case "qol6":
                    qolColumnIndex += 24;
                    break;
                case "qol7":
                    qolColumnIndex += 28;
                    break;
                case "qol8":
                    qolColumnIndex += 32;
                    break;
                case "qol9":
                    qolColumnIndex += 36;
                    break;
                case "qol10":
                    qolColumnIndex += 40;
                    break;
            }
            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[qolColumnIndex] = done;
            row[qolColumnIndex+1] = score;

            if(!skip_questionnaire) {
                row[qolColumnIndex + 2] = String.valueOf(numberQuestion + 1);
            }

            if(skip && !skip_questionnaire)
                row[qolColumnIndex+3] = String.valueOf(skipped_question_qol + 1);


            if(skip_questionnaire){
                row[qolColumnIndex + 2] = "0";
                row[qolColumnIndex+3] = "0";
            }
            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            writer.writeAll(csvEntries);
            writer.close();

            Log.d("TEST", "row " + row[qolColumnIndex]+ row[qolColumnIndex+1]);


            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }


    }

    private void modifyCSVGeneralQOL(String done, String  score, String answered, boolean skip){

        String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+idPatient+"/infos.csv";
        int qolColumnIndex = 21;

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[qolColumnIndex] = done;
            row[qolColumnIndex+1] = score;
            row[qolColumnIndex + 2] = answered;
            if(skip)
                row[qolColumnIndex+3] = String.valueOf(skipped_question + 1);


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
        String full_qol;
        switch (qol) {
            case "qol1":
                full_qol = "Emotional and Behavioral Dyscontrol";
                break;
            case "qol2":
                full_qol ="Sleep Disturbance";
                break;
            case "qol3":
                full_qol = "Ability to Participate in Social Roles and Activities";
                break;
            case "qol4":
                full_qol = "Satisfaction with Social Roles and Activities";
                break;
            case "qol5":
                full_qol = "Cognitive function";
                break;
            case "qol7":
                full_qol = "Positive Affect and Well-Being";
                break;
            case "qol8":
                full_qol = "Stigma";
                break;
            case "qol9":
                full_qol = "Upper Extremity Function";
                break;
            case "qol10":
                full_qol = "Lower Extremity Function";
                break;
            default:
                full_qol = "NONE";
                break;
        }

        if(!exist_file){
            writeCSVClass.createAndWriteCSV_QOL(csv_path+"/"+idPatient+"_QOL.csv", idPatient,caseID, date, full_qol, String.valueOf(numberQuestion_qol + 1), rating);
        }else{
            writeCSVClass.writeDataCSV_QOL(csv_path+"/"+idPatient+"_QOL.csv", full_qol, String.valueOf(numberQuestion_qol + 1), rating);
        }
    }


}