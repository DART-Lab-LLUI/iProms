package fr.thomas.menard.iproms.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.LocaleHelper;
import fr.thomas.menard.iproms.Utils.SFTP;
import fr.thomas.menard.iproms.Utils.WriteCSV;
import fr.thomas.menard.iproms.Utils.tScore;
import fr.thomas.menard.iproms.databinding.ActivitySummaryBinding;

public class SummaryActivity extends AppCompatActivity {

    ActivitySummaryBinding binding;
    String patientID, date, caseID, langue, diagnosis, questionnaire, questionnaire_sleep, type;
    Resources resources;
    Context context;

    int total_score_sleep;
    String questionAnsFatigue, questionAnsDep, questionAnsQol, questionAnsPROMIS;
    String avg_score_fatigue, score_depression, score_anxiety, avg_score_qol, tscore_promis_physical, tscore_promis_mental, score_promis_physical, score_promis_mental;

    String lastQuestionFatigue, skippedQuestionDepAnx, skipped_question_anx, skippedQuestionQOL, skipped_question_promis;

    String fatigue, depression, qol, promis;
    String qol1, qol2, qol3, qol4, qol5, qol6, qol7, qol8, qol9, qol10, sleep, fsmc, bdi;
    String skippedQuestionQOL1, skippedQuestionQOL2, skippedQuestionQOL3,skippedQuestionQOL4,skippedQuestionQOL5,skippedQuestionQOL6,skippedQuestionQOL7,skippedQuestionQOL8,skippedQuestionQOL9,skippedQuestionQOL10,skipped_question_sleep, skipped_question_fsmc, skipped_question_bdi;
    String questionAnsQOL1, questionAnsQOL2,questionAnsQOL3,questionAnsQOL4,questionAnsQOL5,questionAnsQOL6,questionAnsQOL7,questionAnsQOL8,questionAnsQOL9,questionAnsQOL10,questionAnsSleep, questionAnsBDI, questionAnsFSMC;
    String scoreQOL1, scoreQOL2,scoreQOL3,scoreQOL4,scoreQOL5,scoreQOL6,scoreQOL7,scoreQOL8,scoreQOL9,scoreQOL10, score_sleep, score_fsmc, score_bdi;

    double[][] tscorePromisPhysical = tScore.getScoreTable_promis_physical();
    double[][] tscorePromisMental = tScore.getScoreTable_promis_mental();

    double[][] tScore_qol1 = tScore.getScoreTable_emotional_behavioral();
    double[][] tScore_qol2 = tScore.getScoreTable_sleep();

    double[][] tScore_qol3 = tScore.getScoreTable_ability_in_social_roles();

    double [][] tScore_qol4 = tScore.getScoreTable_satisfaction_social_roles();

    double [][] tScore_qol5 = tScore.getScoreTable_cognitive_function();

    double [][] tScore_qol7 = tScore.getScoreTable_positve_affect();

    double [][] tScore_qol8 = tScore.getScoreTable_stigma();
    double [][] tScore_qol9 = tScore.getScoreTable_upper_function();
    double [][] tScore_qol10 = tScore.getScoreTable_lower_function();
    File folderSRC;
    String txt_interpretations;
    private static final String host = "172.20.11.10";
    private static final int port = 22;
    private static final String user = "lli_admin";
    private static final String password = "0YVjglmuevOh";
    private static String PATH_SERVER;

    private WriteCSV writeCSV;
    String csv_path;
    boolean exist_file = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySummaryBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());


        init();
        retrieveInfos();
        promisTscore();
        //convertTscore();
        initTab();
        createResultCSV();
        uploadResultCSV();
        checkScoreforOthersQuestionnaires();
        listnenBtn();
    }


    private void init(){
        Intent intent = getIntent();
        langue = intent.getStringExtra("langue");
        patientID = intent.getStringExtra("patientID");
        date = intent.getStringExtra("date");
        caseID = intent.getStringExtra("caseID");
        diagnosis = intent.getStringExtra("diagnosis");
        type = intent.getStringExtra("type");


        questionnaire = "";

        context = LocaleHelper.setLocale(getApplicationContext(), langue);
        resources = context.getResources();

        writeCSV = WriteCSV.getInstance(this);

        if(type.equals("first")){
            csv_path = getExternalFilesDir(null).getAbsolutePath() + "/" + patientID + "/first/";

        }else{
            csv_path = getExternalFilesDir(null).getAbsolutePath() + "/" + patientID + "/second/";

        }

    }

    private void initTab(){
        double mean_fatigue = (double) Integer.parseInt(avg_score_fatigue) / (Integer.parseInt(questionAnsFatigue) - Integer.parseInt(lastQuestionFatigue) - 1);
        String mean_sfatigue = String.valueOf(Math.round(mean_fatigue));
        binding.cellScoreFatiue.setText(mean_sfatigue+ " / 7");
        displayInterpretations("fatigue");
        binding.cellDepressionScore.setText(score_depression + " / 21");
        displayInterpretations("depression");
        binding.cellAnxietyScore.setText(score_anxiety+ " / 21");
        displayInterpretations("anxiety");
        binding.cellBDIScore.setText(score_bdi+" / 63");
        displayInterpretations("bdi");
        binding.cellPromisPhysicalScore.setText(score_promis_physical + " / 20");
        displayInterpretations("promis");
        binding.cellPromisMentalScore.setText(score_promis_mental + " / 20");
        displayInterpretations("promis");





    }

    private void listnenBtn(){
        binding.btnOptionnalQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OptionnalQuestionnairesActivity.class);
                intent.putExtra("langue", langue);
                intent.putExtra("patientID", patientID);
                intent.putExtra("date", date);
                intent.putExtra("caseID", caseID);
                intent.putExtra("diagnosis", diagnosis);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
    }

    private void promisTscore(){
        if(!score_promis_physical.equals("0") && Integer.parseInt(skipped_question_promis) < 4){
            if (Integer.parseInt(skipped_question_promis) == 10) {
                tscore_promis_physical = "0";

            }else {
                Log.d("TEST", "tes " + tscorePromisPhysical[10][1]);
                tscore_promis_physical = String.valueOf(tscorePromisPhysical[Integer.parseInt(score_promis_physical) - 4 ][1]);

            }

        }else{
            tscore_promis_physical = "No t-score";
        }

        if(!score_promis_mental.equals("0") && Integer.parseInt(skipped_question_promis) < 4){
            if (Integer.parseInt(skipped_question_promis) == 10) {
                tscore_promis_mental = "0";

            }else {
                tscore_promis_mental = String.valueOf(tscorePromisMental[Integer.parseInt(score_promis_mental ) - 4 ][1]);

            }

        }else{
            tscore_promis_mental = "No t-score";
        }
    }
    private void convertTscore(){
        if(!scoreQOL10.equals("0") && Integer.parseInt(skippedQuestionQOL10) < 4){
            if (Integer.parseInt(skippedQuestionQOL10) == 0) {
                scoreQOL10 = String.valueOf(tScore_qol10[Integer.parseInt(scoreQOL10) - 8][1]);
            } else if (Integer.parseInt(skippedQuestionQOL10) == 8) {
                scoreQOL10 = "0";
            }else {
                int score_round = (Integer.parseInt(scoreQOL10) * 8) / (8-Integer.parseInt(skippedQuestionQOL10));
                scoreQOL10 = String.valueOf(tScore_qol10[score_round - 8][1]);
            }

        }else{
            scoreQOL10 = "No t-score";
        }

        if(!scoreQOL9.equals("0") && Integer.parseInt(skippedQuestionQOL9) < 4){
            if (Integer.parseInt(skippedQuestionQOL9) == 0) {
                scoreQOL9 = String.valueOf(tScore_qol9[Integer.parseInt(scoreQOL9) - 8][1]);
            } else if (Integer.parseInt(skippedQuestionQOL9) == 8) {
                scoreQOL9 = "0";
            }else
            {
                int score_round = (Integer.parseInt(scoreQOL9) * 8) / (8-Integer.parseInt(skippedQuestionQOL9));
                scoreQOL9 = String.valueOf(tScore_qol9[score_round - 8][1]);
            }
        }else{
            scoreQOL9 = "No t-score";
        }


        if(!scoreQOL8.equals("0") && Integer.parseInt(skippedQuestionQOL8) < 4){
            if (Integer.parseInt(skippedQuestionQOL8) == 0) {
                scoreQOL8 = String.valueOf(tScore_qol8[Integer.parseInt(scoreQOL8) - 8][1]);
            } else if (Integer.parseInt(skippedQuestionQOL8) == 8) {
                scoreQOL8 = "0";
            }else
            {
                int score_round = (Integer.parseInt(scoreQOL8) * 8) / (8-Integer.parseInt(skippedQuestionQOL8));
                scoreQOL8 = String.valueOf(tScore_qol8[score_round - 8][1]);
            }
        }else{
            scoreQOL8 = "No t-score";
        }

        if(!scoreQOL7.equals("0") && Integer.parseInt(skippedQuestionQOL7) < 5){
            if (Integer.parseInt(skippedQuestionQOL7) == 0) {
                scoreQOL7 = String.valueOf(tScore_qol7[Integer.parseInt(scoreQOL7) - 9][1]);
            } else if (Integer.parseInt(skippedQuestionQOL7) == 8) {
                scoreQOL7 = "0";
            }else
            {
                int score_round = (Integer.parseInt(scoreQOL7) * 9) / (9-Integer.parseInt(skippedQuestionQOL7));
                scoreQOL7 = String.valueOf(tScore_qol7[score_round - 9][1]);
            }
        }else{
            scoreQOL7 = "No t-score";
        }


        if(!scoreQOL5.equals("0") && Integer.parseInt(skippedQuestionQOL5) < 4){
            if (Integer.parseInt(skippedQuestionQOL5) == 0) {
                scoreQOL5 = String.valueOf(tScore_qol5[Integer.parseInt(scoreQOL5) - 8][1]);
            } else if (Integer.parseInt(skippedQuestionQOL5) == 8) {
                scoreQOL5 = "0";
            }else
            {
                int score_round = (Integer.parseInt(scoreQOL5) * 8) / (8-Integer.parseInt(skippedQuestionQOL5));
                scoreQOL5 = String.valueOf(tScore_qol5[score_round - 8][1]);
            }
        }else{
            scoreQOL5 = "No t-score";
        }

        if(!scoreQOL4.equals("0") && Integer.parseInt(skippedQuestionQOL4) < 4){
            if (Integer.parseInt(skippedQuestionQOL4) == 0) {
                scoreQOL4 = String.valueOf(tScore_qol4[Integer.parseInt(scoreQOL4) - 8][1]);
            }else if (Integer.parseInt(skippedQuestionQOL4) == 8) {
                scoreQOL4 = "0";
            } else
            {
                int score_round = (Integer.parseInt(scoreQOL4) * 8) / (8-Integer.parseInt(skippedQuestionQOL4));
                scoreQOL4 = String.valueOf(tScore_qol4[score_round - 8][1]);
            }
        }else{
            scoreQOL4 = "No t-score";
        }


        if(!scoreQOL3.equals("0") && Integer.parseInt(skippedQuestionQOL3) < 4){
            if (Integer.parseInt(skippedQuestionQOL3) == 0) {
                scoreQOL3 = String.valueOf(tScore_qol3[Integer.parseInt(scoreQOL3) - 8][1]);
            } else if (Integer.parseInt(skippedQuestionQOL3) == 8) {
                scoreQOL3 = "0";
            } else
            {
                int score_round = (Integer.parseInt(scoreQOL3) * 8) / (8-Integer.parseInt(skippedQuestionQOL3));
                scoreQOL3 = String.valueOf(tScore_qol3[score_round - 8][1]);
            }
        }else{
            scoreQOL3 = "No t-score";
        }


        if(!scoreQOL2.equals("0") && Integer.parseInt(skippedQuestionQOL2) < 4){
            if (Integer.parseInt(skippedQuestionQOL2) == 0) {
                scoreQOL2 = String.valueOf(tScore_qol2[Integer.parseInt(scoreQOL2) - 8][1]);
            } else if (Integer.parseInt(skippedQuestionQOL2) == 8) {
                scoreQOL2 = "0";
            } else
            {
                int score_round = (Integer.parseInt(scoreQOL2) * 8) / (8-Integer.parseInt(skippedQuestionQOL2));
                scoreQOL2 = String.valueOf(tScore_qol2[score_round - 8][1]);
            }
        }else{
            scoreQOL2 = "No t-score";
        }

        if(!scoreQOL1.equals("0") && Integer.parseInt(skippedQuestionQOL1) < 4){
            if (Integer.parseInt(skippedQuestionQOL1) == 0) {
                scoreQOL1 = String.valueOf(tScore_qol1[Integer.parseInt(scoreQOL1) - 8][1]);
            }else if (Integer.parseInt(skippedQuestionQOL1) == 8) {
                scoreQOL1 = "0";
            } else
            {
                int score_round = (Integer.parseInt(scoreQOL1) * 8) / (8-Integer.parseInt(skippedQuestionQOL1));
                scoreQOL1 = String.valueOf(tScore_qol1[score_round - 8][1]);
            }
        }else{
            scoreQOL1 = "No t-score";
        }

    }

    private void retrieveInfos(){
        String csvFilePath;
        if(type.equals("first")){
            csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+patientID+"/first/infos.csv";
        }else{
            csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+patientID+"/second/infos.csv";
        }

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
            int promisColumnIndex = 17;
            int qolColumnIndex = 22;

            List<String[]> csvEntries = reader.readAll();
            String[] firstRow = csvEntries.get(1);


            fatigue = firstRow[fatigueColumnIndex];
            depression = firstRow[depressionColumnIndex];
            bdi = firstRow[bdiIndex];
            promis = firstRow[promisColumnIndex];
            qol = firstRow[qolColumnIndex];

            avg_score_fatigue = (firstRow[fatigueColumnIndex+1]);
            score_depression = firstRow[depressionColumnIndex+1];
            score_anxiety = firstRow[depressionColumnIndex + 2];
            score_bdi = firstRow[bdiIndex+1];
            score_promis_physical = firstRow[promisColumnIndex+1];
            score_promis_mental = firstRow[promisColumnIndex+2];

            avg_score_qol = (firstRow[qolColumnIndex+1]);


            questionAnsFatigue = (firstRow[fatigueColumnIndex+2]);
            questionAnsDep = (firstRow[depressionColumnIndex+3]);
            questionAnsBDI = firstRow[bdiIndex+2];
            questionAnsPROMIS = firstRow[promisColumnIndex+3];
            questionAnsQol = (firstRow[qolColumnIndex+2]);


            lastQuestionFatigue = firstRow[fatigueColumnIndex + 3];
            skippedQuestionDepAnx = firstRow[depressionColumnIndex + 4];
            skipped_question_anx = firstRow[depressionColumnIndex + 5];
            skipped_question_bdi = firstRow[bdiIndex+3];
            skipped_question_promis = firstRow[promisColumnIndex+4];
            skippedQuestionQOL = firstRow[qolColumnIndex+3];

            qol1 = firstRow[qolColumnIndex + 4];
            scoreQOL1 = firstRow[qolColumnIndex + 5];
            questionAnsQOL1 = firstRow[qolColumnIndex + 6];
            skippedQuestionQOL1 = firstRow[qolColumnIndex + 7];

            qol2 = firstRow[qolColumnIndex + 8];
            scoreQOL2 = firstRow[qolColumnIndex + 9];
            questionAnsQOL2 = firstRow[qolColumnIndex + 10];
            skippedQuestionQOL2 = firstRow[qolColumnIndex + 11];

            qol3 = firstRow[qolColumnIndex + 12];
            scoreQOL3 = firstRow[qolColumnIndex + 13];
            questionAnsQOL3 = firstRow[qolColumnIndex + 14];
            skippedQuestionQOL3 = firstRow[qolColumnIndex + 15];

            qol4 = firstRow[qolColumnIndex + 16];
            scoreQOL4 = firstRow[qolColumnIndex + 17];
            questionAnsQOL4 = firstRow[qolColumnIndex + 18];
            skippedQuestionQOL4 = firstRow[qolColumnIndex + 19];

            qol5 = firstRow[qolColumnIndex + 20];
            scoreQOL5 = firstRow[qolColumnIndex + 21];
            questionAnsQOL5 = firstRow[qolColumnIndex + 22];
            skippedQuestionQOL5 = firstRow[qolColumnIndex + 23];

            qol6 = firstRow[qolColumnIndex + 24];
            scoreQOL6 = firstRow[qolColumnIndex + 25];
            questionAnsQOL6 = firstRow[qolColumnIndex + 26];
            skippedQuestionQOL6 = firstRow[qolColumnIndex + 27];

            qol7 = firstRow[qolColumnIndex + 28];
            scoreQOL7 = firstRow[qolColumnIndex + 29];
            questionAnsQOL7 = firstRow[qolColumnIndex + 30];
            skippedQuestionQOL7 = firstRow[qolColumnIndex + 31];

            qol8 = firstRow[qolColumnIndex + 32];
            scoreQOL8 = firstRow[qolColumnIndex + 33];
            questionAnsQOL8 = firstRow[qolColumnIndex + 34];
            skippedQuestionQOL8 = firstRow[qolColumnIndex + 35];

            qol9 = firstRow[qolColumnIndex + 36];
            scoreQOL9 = firstRow[qolColumnIndex + 37];
            questionAnsQOL9 = firstRow[qolColumnIndex + 38];
            skippedQuestionQOL9 = firstRow[qolColumnIndex + 39];

            qol10 = firstRow[qolColumnIndex + 40];
            scoreQOL10 = firstRow[qolColumnIndex + 41];
            questionAnsQOL10 = firstRow[qolColumnIndex + 42];
            skippedQuestionQOL10 = firstRow[qolColumnIndex + 43];

            /*
            if(Integer.parseInt(questionAnsQol)<0)
                questionAnsQol = "0";

             */

            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createResultCSV(){
        if(!exist_file){
            double mean_fatigue = (double) Integer.parseInt(avg_score_fatigue) / (Integer.parseInt(questionAnsFatigue) - Integer.parseInt(lastQuestionFatigue) - 1);

            writeCSV.createAndWriteResult(csv_path+"/"+patientID+"_result.csv", patientID, caseID, date,
                    String.valueOf(mean_fatigue), score_depression, score_anxiety, score_bdi, score_promis_physical, score_promis_mental, scoreQOL1, scoreQOL2, scoreQOL3, scoreQOL4,
                    scoreQOL5, scoreQOL7, scoreQOL8, scoreQOL9, scoreQOL10);
        }
    }

    private void uploadResultCSV(){
        uploadData("result.csv");
        binding.linearUpload.setVisibility(View.VISIBLE);
    }



    @SuppressLint("ResourceAsColor")
    private String displayInterpretations(String categorie){

        if(categorie.equals("fatigue")){
            if(avg_score_fatigue.equals("0")){
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellResultFatigue.setColorSections(upperlimit, colors);
                binding.cellResultFatigue.setUserScore(2.75f);
                binding.cellResultFatigue.setUserText(txt_interpretations);
            }else{
                double mean_fatigue = (double) Integer.parseInt(avg_score_fatigue) / (Integer.parseInt(questionAnsFatigue) - Integer.parseInt(lastQuestionFatigue) - 1);
                if(mean_fatigue < 4){
                    txt_interpretations = "Within normal limits";
                }else{
                    txt_interpretations = "Impaired";
                }

                float[] upperLimits_fatigue = {(float) (4.0f/1.3), (float) (7.0f/1.3)};
                int[] colors_fatigue = {android.graphics.Color.GREEN, Color.RED};
                binding.cellResultFatigue.setColorSections(upperLimits_fatigue, colors_fatigue);
                binding.cellResultFatigue.setUserText("Impaired");
                binding.cellResultFatigue.setUserScore((float) ((float) mean_fatigue/1.3));
            }


        }

        if(categorie.equals("depression")){
            if(Integer.parseInt(score_depression)==0 && !questionAnsDep.equals("15")){
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellDepressionResult.setColorSections(upperlimit, colors);
                binding.cellDepressionResult.setUserScore(2.75f);
                binding.cellDepressionResult.setUserText(txt_interpretations);
            }else{
                if(Integer.parseInt(score_depression)<7){
                    txt_interpretations = "Within normal limits";

                } else if (Integer.parseInt(score_depression)<11) {
                    txt_interpretations = "Bordeling case";

                }
                else{
                    txt_interpretations = "Impaired";

                }

                float[] upperLimits_dep = {(float) (7.0f/4), (float) (11.0f/4), (float) (21.0f/4)};
                int[] colors_dep = {android.graphics.Color.GREEN, Color.YELLOW, Color.RED};
                binding.cellDepressionResult.setColorSections(upperLimits_dep, colors_dep);
                binding.cellDepressionResult.setUserScore((float) ((float) Integer.parseInt(score_depression)/4));
                binding.cellDepressionResult.setUserText(txt_interpretations);
            }

        }


        if(categorie.equals("anxiety")){
            if(score_anxiety.equals("0") && !questionAnsDep.equals("15")){
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellAnxietyResult.setColorSections(upperlimit, colors);
                binding.cellAnxietyResult.setUserScore(2.75f);
                binding.cellAnxietyResult.setUserText(txt_interpretations);
            }else{
                if(Integer.parseInt(score_anxiety)<7) {
                    txt_interpretations = "Within normal limits";

                }else if (Integer.parseInt(score_anxiety)<11) {
                    txt_interpretations = "Bordeling case";

                }else{
                    txt_interpretations = "Impaired";

                }
                float[] upperLimits_dep = {(float) (7.0f/4), (float) (11.0f/4), (float) (21.0f/4)};
                int[] colors_dep = {android.graphics.Color.GREEN, Color.YELLOW, Color.RED};
                binding.cellAnxietyResult.setColorSections(upperLimits_dep, colors_dep);
                binding.cellAnxietyResult.setUserScore((float) ((float) Integer.parseInt(score_anxiety)/4));
                binding.cellAnxietyResult.setUserText(txt_interpretations);
            }

        }

        if(categorie.equals("bdi")){
            String txt_interpretations;
            if(score_bdi.equals("0") && !questionAnsBDI.equals("22")){
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellBDIResult.setColorSections(upperlimit, colors);
                binding.cellBDIResult.setUserScore(2.75f);
                binding.cellBDIResult.setUserText(txt_interpretations);
            }else{
                if(Integer.parseInt(score_bdi)<14){
                    txt_interpretations = "unauffällig";
                } else if (Integer.parseInt(score_bdi)<20) {
                    txt_interpretations = "milde depressive";
                } else if (Integer.parseInt(score_bdi)<29) {
                    txt_interpretations = "moderate depressive";
                }else {
                    txt_interpretations = "schwere depressive";
                }

                int[] colors = {android.graphics.Color.GREEN, Color.YELLOW,Color.rgb(255,165,0), Color.RED};

                float min = 0.0f;
                float greenEnd = 14.0f;
                float yellowEnd = 20.0f;
                float orangeEnd = 29.0f;
                float redEnd = 63.0f;

                float[] upperlimit = {binding.cellBDIResult.rescaleValue(greenEnd,min,redEnd,  40.0f),
                        binding.cellBDIResult.rescaleValue(yellowEnd, min,redEnd, 40.0f),
                        binding.cellBDIResult.rescaleValue(orangeEnd, min,redEnd, 40.0f),
                        binding.cellBDIResult.rescaleValue(redEnd, min,redEnd, 40.0f)};

                float user_score = Float.parseFloat(score_bdi);
                float user_score_rescale = binding.cellBDIResult.rescaleValue(user_score,min,redEnd, 40.0f);

                binding.cellBDIResult.setColorSections(upperlimit, colors);
                binding.cellBDIResult.setUserScore(user_score_rescale);
                binding.cellBDIResult.setUserText(txt_interpretations);
            }

        }



        if(categorie.equals("promis")) {
            if (tscore_promis_physical.equals("No t-score") || Integer.parseInt(skipped_question_promis)>3) {
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellPromisPhysicalResult.setColorSections(upperlimit, colors);
                binding.cellPromisPhysicalResult.setUserScore(2.75f);
                binding.cellPromisPhysicalResult.setUserText(txt_interpretations);

            } else {
                if (Double.parseDouble(tscore_promis_physical) > 70) {
                    txt_interpretations = "Very high";
                } else if (Double.parseDouble(tscore_promis_physical) < 71 && Double.parseDouble(tscore_promis_physical) > 60) {
                    txt_interpretations = "High";

                } else if (Double.parseDouble(tscore_promis_physical) < 61 && Double.parseDouble(tscore_promis_physical) > 40) {
                    txt_interpretations = "Average";

                } else if (Double.parseDouble(tscore_promis_physical) < 41 && Double.parseDouble(tscore_promis_physical) > 30) {
                    txt_interpretations = "Low";
                }
                else if (Double.parseDouble(tscore_promis_physical) < 31) {
                    txt_interpretations = "Very low";

                }
                int[] colors = { Color.RED, Color.YELLOW, android.graphics.Color.GREEN};

                float min = 20.2f;
                float greenEnd = 40.0f;
                float yellowEnd = 60.0f;
                float redEnd = 80.0f;

                float[] upperlimit = {binding.cellPromisMentalResult.rescaleValue(greenEnd,min, redEnd, 70.0f),
                        binding.cellPromisMentalResult.rescaleValue(yellowEnd, min, redEnd,70.0f),
                        binding.cellPromisMentalResult.rescaleValue(redEnd, min, redEnd,70.0f)};

                float user_score = Float.parseFloat(tscore_promis_physical);
                float user_score_rescale = binding.cellPromisPhysicalResult.rescaleValue(user_score, min, redEnd,70.0f);

                binding.cellPromisPhysicalResult.setColorSections(upperlimit, colors);
                binding.cellPromisPhysicalResult.setUserScore(user_score_rescale);
                binding.cellPromisPhysicalResult.setUserText(txt_interpretations);

            }

            if (tscore_promis_mental.equals("No t-score") || Integer.parseInt(skipped_question_promis)>3) {
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellPromisMentalResult.setColorSections(upperlimit, colors);
                binding.cellPromisMentalResult.setUserScore(2.75f);
                binding.cellPromisMentalResult.setUserText(txt_interpretations);

            } else {
                if (Double.parseDouble(tscore_promis_mental) > 70) {
                    txt_interpretations = "Very high";
                } else if (Double.parseDouble(tscore_promis_mental) < 71 && Double.parseDouble(tscore_promis_mental) > 60) {
                    txt_interpretations = "High";

                } else if (Double.parseDouble(tscore_promis_mental) < 61 && Double.parseDouble(tscore_promis_mental) > 40) {
                    txt_interpretations = "Average";

                } else if (Double.parseDouble(tscore_promis_mental) < 41 && Double.parseDouble(tscore_promis_mental) > 30) {
                    txt_interpretations = "Low";
                }
                else if (Double.parseDouble(tscore_promis_mental) < 31) {
                    txt_interpretations = "Very low";

                }
                int[] colors = { Color.RED, Color.YELLOW, android.graphics.Color.GREEN};

                float min = 20.2f;
                float greenEnd = 40.0f;
                float yellowEnd = 60.0f;
                float redEnd = 80.0f;

                float[] upperlimit = {binding.cellPromisMentalResult.rescaleValue(greenEnd,min, redEnd, 70.0f),
                        binding.cellPromisMentalResult.rescaleValue(yellowEnd, min, redEnd,70.0f),
                        binding.cellPromisMentalResult.rescaleValue(redEnd, min, redEnd,70.0f)};

                float user_score = Float.parseFloat(tscore_promis_mental);
                float user_score_rescale = binding.cellPromisMentalResult.rescaleValue(user_score, min, redEnd,70.0f);

                binding.cellPromisMentalResult.setColorSections(upperlimit, colors);
                binding.cellPromisMentalResult.setUserScore(user_score_rescale);
                binding.cellPromisMentalResult.setUserText(txt_interpretations);

            }
        }
/*
        if(categorie.equals("qol2")){
            if(scoreQOL2.equals("No t-score") || Integer.parseInt(skippedQuestionQOL2)>3){
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellqol2Result.setColorSections(upperlimit, colors);
                binding.cellqol2Result.setUserScore(2.75f);
                binding.cellqol2Result.setUserText(txt_interpretations);
            }else{
                Log.d("TEST", "here diff 0" + scoreQOL2);
                if(Double.parseDouble(scoreQOL2)>70) {
                    txt_interpretations = "Severely increased";
                }
                else if (Double.parseDouble(scoreQOL2)<71 && Double.parseDouble(scoreQOL2) >60) {
                    txt_interpretations =  "Moderately increased";

                }
                else if (Double.parseDouble(scoreQOL2)<61 && Double.parseDouble(scoreQOL2) >54) {
                    txt_interpretations =  "Mildly increased";

                }
                else if (Double.parseDouble(scoreQOL2)<55) {
                    txt_interpretations = "Within normal limits";

                }

                int[] colors = {android.graphics.Color.GREEN, Color.YELLOW, Color.rgb(255,165,0), Color.RED};

                float min = 32.0f;
                float greenEnd = 55.0f;
                float yellowEnd = 61.0f;
                float orangeEnd = 71.0f;
                float redEnd = 84.2f;

                float[] upperlimit = {binding.cellqol2Result.rescaleValue(greenEnd,min,redEnd,  40.0f),
                        binding.cellqol2Result.rescaleValue(yellowEnd, min,redEnd, 40.0f),
                        binding.cellqol2Result.rescaleValue(orangeEnd,min,redEnd,  40.0f),
                        binding.cellqol2Result.rescaleValue(redEnd, min,redEnd, 40.0f)};

                float user_score = Float.parseFloat(scoreQOL2);
                float user_score_rescale = binding.cellqol2Result.rescaleValue(user_score,min,redEnd, 40.0f);

                binding.cellqol2Result.setColorSections(upperlimit, colors);
                binding.cellqol2Result.setUserScore(user_score_rescale);
                binding.cellqol2Result.setUserText(txt_interpretations);


            }

        }

        if(categorie.equals("qol8") ){
            if(scoreQOL8.equals("No t-score") || Integer.parseInt(skippedQuestionQOL8)>3){
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellqol8Result.setColorSections(upperlimit, colors);
                binding.cellqol8Result.setUserScore(2.75f);
                binding.cellqol8Result.setUserText(txt_interpretations);
            }else{

                    if(Double.parseDouble(scoreQOL8)>70){
                        txt_interpretations = "Severely increased";
                    }

                    else if (Double.parseDouble(scoreQOL8)<71 && Double.parseDouble(scoreQOL8) >60) {
                        txt_interpretations =  "Moderately increased";

                    }
                    else if (Double.parseDouble(scoreQOL8)<61 && Double.parseDouble(scoreQOL8) >54) {
                        txt_interpretations =  "Mildly increased";

                    }
                    else if (Double.parseDouble(scoreQOL8)<55) {
                        txt_interpretations = "Within normal limits";

                    }
                    int[] colors = {android.graphics.Color.GREEN, Color.YELLOW, Color.rgb(255,165,0), Color.RED};

                    float min = 39.2f;
                    float greenEnd = 55.0f;
                    float yellowEnd = 61.0f;
                    float orangeEnd = 71.0f;
                    float redEnd = 81.5f;

                    float[] upperlimit = {binding.cellqol8Result.rescaleValue(greenEnd,min,redEnd,  40.0f),
                            binding.cellqol8Result.rescaleValue(yellowEnd, min,redEnd, 40.0f),
                            binding.cellqol8Result.rescaleValue(orangeEnd,min,redEnd,  40.0f),
                            binding.cellqol8Result.rescaleValue(redEnd, min,redEnd, 40.0f)};

                    float user_score = Float.parseFloat(scoreQOL8);
                    float user_score_rescale = binding.cellqol8Result.rescaleValue(user_score,min,redEnd, 40.0f);

                    binding.cellqol8Result.setColorSections(upperlimit, colors);
                    binding.cellqol8Result.setUserScore(user_score_rescale);
                    binding.cellqol8Result.setUserText(txt_interpretations);

                }
            }

        if(categorie.equals("qol3")){
            if(scoreQOL3.equals("No t-score") || Integer.parseInt(skippedQuestionQOL3)>3){
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellqol3Result.setColorSections(upperlimit, colors);
                binding.cellqol3Result.setUserScore(2.75f);
                binding.cellqol3Result.setUserText(txt_interpretations);            }else{

                if(Double.parseDouble(scoreQOL3)>45) {
                    txt_interpretations = "Within normal limits";
                }
                else if (Double.parseDouble(scoreQOL3)<46 && Double.parseDouble(scoreQOL3) >39) {
                    txt_interpretations = "Mild impairment";

                }
                else if (Double.parseDouble(scoreQOL3)<40 && Double.parseDouble(scoreQOL3) >29) {
                    txt_interpretations = "Moderate impairment";
                }
                else if (Double.parseDouble(scoreQOL3)<30) {
                    txt_interpretations = "Severe impairment";

                }
                int[] colors = {android.graphics.Color.RED,  Color.rgb(255,165,0),Color.YELLOW, Color.GREEN};

                float min = 24.1f;
                float greenEnd = 30.0f;
                float yellowEnd = 40.0f;
                float orangeEnd = 45.0f;
                float redEnd = 60.2f;

                float[] upperlimit = {binding.cellqol3Result.rescaleValue(greenEnd,min,redEnd,  70.0f),
                        binding.cellqol3Result.rescaleValue(yellowEnd, min,redEnd, 70.0f),
                        binding.cellqol3Result.rescaleValue(orangeEnd,min,redEnd,  70.0f),
                        binding.cellqol3Result.rescaleValue(redEnd, min,redEnd, 70.0f)};

                float user_score = Float.parseFloat(scoreQOL3);
                float user_score_rescale = binding.cellqol3Result.rescaleValue(user_score,min,redEnd, 70.0f);

                binding.cellqol3Result.setColorSections(upperlimit, colors);
                binding.cellqol3Result.setUserScore(user_score_rescale);
                binding.cellqol3Result.setUserText(txt_interpretations);

            }
        }

        if(categorie.equals("qol4") ){
            if(scoreQOL4.equals("No t-score") || Integer.parseInt(skippedQuestionQOL4)>3){
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellqol4Result.setColorSections(upperlimit, colors);
                binding.cellqol4Result.setUserScore(2.75f);
                binding.cellqol4Result.setUserText(txt_interpretations);
            }else{

                if(Double.parseDouble(scoreQOL4)>45) {
                    txt_interpretations = "Within normal limits";

                }
                else if (Double.parseDouble(scoreQOL4)<46 && Double.parseDouble(scoreQOL4) >39) {
                    txt_interpretations = "Mild impairment";

                }
                else if (Double.parseDouble(scoreQOL4)<40 && Double.parseDouble(scoreQOL4) >29) {
                    txt_interpretations = "Moderate impairment";

                }
                else if (Double.parseDouble(scoreQOL4)<30) {
                    txt_interpretations = "Severe impairment";

                }
                int[] colors = {android.graphics.Color.RED,  Color.rgb(255,165,0),Color.YELLOW, Color.GREEN};

                float min = 28.4f;
                float greenEnd = 30.0f;
                float yellowEnd = 40.0f;
                float orangeEnd = 45.0f;
                float redEnd = 60.5f;

                float[] upperlimit = {binding.cellqol4Result.rescaleValue(greenEnd,min,redEnd,  70.0f),
                        binding.cellqol4Result.rescaleValue(yellowEnd, min,redEnd, 70.0f),
                        binding.cellqol4Result.rescaleValue(orangeEnd,min,redEnd,  70.0f),
                        binding.cellqol4Result.rescaleValue(redEnd, min,redEnd, 70.0f)};

                float user_score = Float.parseFloat(scoreQOL4);
                float user_score_rescale = binding.cellqol4Result.rescaleValue(user_score,min,redEnd, 70.0f);

                binding.cellqol4Result.setColorSections(upperlimit, colors);
                binding.cellqol4Result.setUserScore(user_score_rescale);
                binding.cellqol4Result.setUserText(txt_interpretations);
            }
        }

        if(categorie.equals("qol5") ){
            if(scoreQOL5.equals("No t-score") || Integer.parseInt(skippedQuestionQOL5)>3){
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellqol5Result.setColorSections(upperlimit, colors);
                binding.cellqol5Result.setUserScore(2.75f);
                binding.cellqol5Result.setUserText(txt_interpretations);
            }else{

                if(Double.parseDouble(scoreQOL5)>45) {
                    txt_interpretations = "Within normal limits";

                }
                else if (Double.parseDouble(scoreQOL5)<46 && Double.parseDouble(scoreQOL5) >39) {
                    txt_interpretations = "Mild impairment";

                }
                else if (Double.parseDouble(scoreQOL5)<40 && Double.parseDouble(scoreQOL5) >29) {
                    txt_interpretations = "Moderate impairment";

                }
                else if (Double.parseDouble(scoreQOL5)<30) {
                    txt_interpretations = "Severe impairment";

                }
                int[] colors = {android.graphics.Color.RED,  Color.rgb(255,165,0),Color.YELLOW, Color.GREEN};

                float min = 17.3f;
                float greenEnd = 30.0f;
                float yellowEnd = 40.0f;
                float orangeEnd = 45.0f;
                float redEnd = 64.2f;

                float[] upperlimit = {binding.cellqol5Result.rescaleValue(greenEnd,min,redEnd,  70.0f),
                        binding.cellqol5Result.rescaleValue(yellowEnd, min,redEnd, 70.0f),
                        binding.cellqol5Result.rescaleValue(orangeEnd,min,redEnd,  70.0f),
                        binding.cellqol5Result.rescaleValue(redEnd, min,redEnd, 70.0f)};

                float user_score = Float.parseFloat(scoreQOL5);
                float user_score_rescale = binding.cellqol5Result.rescaleValue(user_score,min,redEnd, 70.0f);

                binding.cellqol5Result.setColorSections(upperlimit, colors);
                binding.cellqol5Result.setUserScore(user_score_rescale);
                binding.cellqol5Result.setUserText(txt_interpretations);
            }

        }

        if(categorie.equals("qol7") ){
            if(scoreQOL7.equals("No t-score") || Integer.parseInt(skippedQuestionQOL7)>4){
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellqol7Result.setColorSections(upperlimit, colors);
                binding.cellqol7Result.setUserScore(2.75f);
                binding.cellqol7Result.setUserText(txt_interpretations);
            }else{

                if(Double.parseDouble(scoreQOL7)>45) {
                    txt_interpretations = "Within normal limits";
                }
                else if (Double.parseDouble(scoreQOL7)<46 && Double.parseDouble(scoreQOL7) >39) {
                    txt_interpretations = "Mild impairment";

                }
                else if (Double.parseDouble(scoreQOL7)<40 && Double.parseDouble(scoreQOL7) >29) {
                    txt_interpretations = "Moderate impairment";

                }
                else if (Double.parseDouble(scoreQOL7)<30) {
                    txt_interpretations = "Severe impairment";

                }
                int[] colors = {android.graphics.Color.RED,  Color.rgb(255,165,0),Color.YELLOW, Color.GREEN};

                float min = 26.3f;
                float greenEnd = 30.0f;
                float yellowEnd = 40.0f;
                float orangeEnd = 45.0f;
                float redEnd = 68.0f;

                float[] upperlimit = {binding.cellqol7Result.rescaleValue(greenEnd,min,redEnd,  70.0f),
                        binding.cellqol7Result.rescaleValue(yellowEnd, min,redEnd, 70.0f),
                        binding.cellqol7Result.rescaleValue(orangeEnd,min,redEnd,  70.0f),
                        binding.cellqol7Result.rescaleValue(redEnd, min,redEnd, 70.0f)};

                float user_score = Float.parseFloat(scoreQOL7);
                float user_score_rescale = binding.cellqol7Result.rescaleValue(user_score,min,redEnd, 70.0f);

                binding.cellqol7Result.setColorSections(upperlimit, colors);
                binding.cellqol7Result.setUserScore(user_score_rescale);
                binding.cellqol7Result.setUserText(txt_interpretations);
            }
        }

        if(categorie.equals("qol9")){
            if(scoreQOL9.equals("No t-score") || Integer.parseInt(skippedQuestionQOL9)>3){
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellqol9Result.setColorSections(upperlimit, colors);
                binding.cellqol9Result.setUserScore(2.75f);
                binding.cellqol9Result.setUserText(txt_interpretations);
            }else{
                if(Double.parseDouble(scoreQOL9)>45) {
                    txt_interpretations = "Within normal limits";

                }
                else if (Double.parseDouble(scoreQOL9)<46 && Double.parseDouble(scoreQOL9) >39) {
                    txt_interpretations = "Mild impairment";

                }
                else if (Double.parseDouble(scoreQOL9)<40 && Double.parseDouble(scoreQOL9) >29) {
                    txt_interpretations = "Moderate impairment";

                }
                else if (Double.parseDouble(scoreQOL9)<30) {
                    txt_interpretations = "Severe impairment";

                }
                int[] colors = {android.graphics.Color.RED,  Color.rgb(255,165,0),Color.YELLOW, Color.GREEN};

                float min = 12.8f;
                float greenEnd = 30.0f;
                float yellowEnd = 40.0f;
                float orangeEnd = 45.0f;
                float redEnd = 53.8f;

                float[] upperlimit = {binding.cellqol9Result.rescaleValue(greenEnd,min,redEnd,  70.0f),
                        binding.cellqol9Result.rescaleValue(yellowEnd, min,redEnd, 70.0f),
                        binding.cellqol9Result.rescaleValue(orangeEnd,min,redEnd,  70.0f),
                        binding.cellqol9Result.rescaleValue(redEnd, min,redEnd, 70.0f)};

                float user_score = Float.parseFloat(scoreQOL9);
                float user_score_rescale = binding.cellqol9Result.rescaleValue(user_score,min,redEnd, 70.0f);

                binding.cellqol9Result.setColorSections(upperlimit, colors);
                binding.cellqol9Result.setUserScore(user_score_rescale);
                binding.cellqol9Result.setUserText(txt_interpretations);
            }
        }

        if(categorie.equals("qol10")){
            if(scoreQOL10.equals("No t-score") || Integer.parseInt(skippedQuestionQOL10)>3){
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellqol10Result.setColorSections(upperlimit, colors);
                binding.cellqol10Result.setUserScore(2.75f);
                binding.cellqol10Result.setUserText(txt_interpretations);
            }else{

                if(Double.parseDouble(scoreQOL10)>45) {
                    txt_interpretations = "Within normal limits";

                }
                else if (Double.parseDouble(scoreQOL10)<46 && Double.parseDouble(scoreQOL10) >39) {
                    txt_interpretations = "Mild impairment";

                }
                else if (Double.parseDouble(scoreQOL10)<40 && (Double.parseDouble(scoreQOL10) >29)) {
                    txt_interpretations = "Moderate impairment";

                }
                else if (Double.parseDouble(scoreQOL10)<30) {
                    txt_interpretations = "Severe impairment";

                }
                int[] colors = {android.graphics.Color.RED,  Color.rgb(255,165,0),Color.YELLOW, Color.GREEN};

                float min = 16.5f;
                float greenEnd = 30.0f;
                float yellowEnd = 40.0f;
                float orangeEnd = 45.0f;
                float redEnd = 58.6f;

                float[] upperlimit = {binding.cellqol10Result.rescaleValue(greenEnd,min,redEnd,  70.0f),
                        binding.cellqol10Result.rescaleValue(yellowEnd, min,redEnd, 70.0f),
                        binding.cellqol10Result.rescaleValue(orangeEnd,min,redEnd,  70.0f),
                        binding.cellqol10Result.rescaleValue(redEnd, min,redEnd, 70.0f)};

                float user_score = Float.parseFloat(scoreQOL10);
                float user_score_rescale = binding.cellqol10Result.rescaleValue(user_score,min,redEnd, 70.0f);

                binding.cellqol10Result.setColorSections(upperlimit, colors);
                binding.cellqol10Result.setUserScore(user_score_rescale);
                binding.cellqol10Result.setUserText(txt_interpretations);
            }
            */




        return txt_interpretations;
    }


    private void checkScoreforOthersQuestionnaires() {
        if ((Integer.parseInt(avg_score_fatigue) / 9 > 3) || (Integer.parseInt(score_depression) > 7) || (Integer.parseInt(score_anxiety) > 7)) {
            binding.txtDescriptionOthers.setVisibility(View.VISIBLE);
        } else {
            binding.txtSummaryDescription.setVisibility(View.VISIBLE);
        }
    }

    private void uploadData(String file){
        SFTP sftp = new SFTP();
        if(type.equals("first")){
            folderSRC = new File(getExternalFilesDir(null).getAbsolutePath() + "/"+patientID + "/first");

        }else{
            folderSRC = new File(getExternalFilesDir(null).getAbsolutePath() + "/"+patientID +"/second");
        }
        PATH_SERVER = "data/raw_data/" + patientID + "/iPROMS/" + date + "/";
        String file_to_send = folderSRC + "/" + patientID +"_"+file;

        Log.d("TEST", "file to send" + file_to_send);

        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean co = sftp.connect(host, user, password, port,file_to_send,
                        PATH_SERVER);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!co) {
                            Toast.makeText(context, "Upload failed, please check your Wifi connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();

    }
}