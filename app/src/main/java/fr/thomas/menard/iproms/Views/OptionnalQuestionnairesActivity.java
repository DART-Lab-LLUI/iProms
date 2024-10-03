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
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.LocaleHelper;
import fr.thomas.menard.iproms.Utils.SFTP;
import fr.thomas.menard.iproms.Utils.WriteCSV;
import fr.thomas.menard.iproms.Utils.tScore;
import fr.thomas.menard.iproms.databinding.ActivityOptionnalQuestionnairesBinding;

public class OptionnalQuestionnairesActivity extends AppCompatActivity {


    ActivityOptionnalQuestionnairesBinding binding;

    String patientID, date, caseID, langue, diagnosis, questionnaire, questionnaire_sleep;
    Resources resources;
    Context context;
    String txt_interpretations;


    private static final String host = "172.20.11.10";
    private static final int port = 22;
    private static final String user = "lli_admin";
    private static final String password = "0YVjglmuevOh";
    private static String PATH_SERVER;

    private WriteCSV writeCSV;
    String csv_path;
    boolean exist_file_sleep = false;
    boolean exist_file_fsmc = false;
    boolean exist_file_bdi = false;

    String questionAnsFatigue, questionAnsDep, questionAnsQol;
    String avg_score_fatigue, score_depression, score_anxiety, avg_score_qol;

    String lastQuestionFatigue, skippedQuestionDepAnx, skipped_question_anx, skippedQuestionQOL;

    String sleep, fsmc, bdi;
    String skipped_question_sleep, skipped_question_fsmc, skipped_question_bdi;

    String questionAnsSleep, questionAnsBDI, questionAnsFSMC;
    String score_sleep, score_fsmc, score_bdi;
    File folderSRC;
    boolean redo_questionnaire;
    String qol, qol1, qol2, qol3, qol4, qol5, qol7, qol8, qol9, qol10;
    String skippedQuestionQOL1, skippedQuestionQOL2, skippedQuestionQOL3,skippedQuestionQOL4,skippedQuestionQOL5,skippedQuestionQOL7,skippedQuestionQOL8,skippedQuestionQOL9,skippedQuestionQOL10;

    String questionAnsQOL1, questionAnsQOL2,questionAnsQOL3,questionAnsQOL4,questionAnsQOL5,questionAnsQOL7,questionAnsQOL8,questionAnsQOL9,questionAnsQOL10;
    String scoreQOL1, scoreQOL2,scoreQOL3,scoreQOL4,scoreQOL5,scoreQOL7,scoreQOL8,scoreQOL9,scoreQOL10;

    boolean  restart_qol1 = false, restart_qol2= false, restart_qol3= false, restart_qol4= false,
            restart_qol5= false, restart_qol7= false, restart_qol8= false, restart_qol9= false,restart_qol10= false;

    double[][] tScore_qol1 = tScore.getScoreTable_emotional_behavioral();
    double[][] tScore_qol2 = tScore.getScoreTable_sleep();

    double[][] tScore_qol3 = tScore.getScoreTable_ability_in_social_roles();

    double [][] tScore_qol4 = tScore.getScoreTable_satisfaction_social_roles();

    double [][] tScore_qol5 = tScore.getScoreTable_cognitive_function();

    double [][] tScore_qol7 = tScore.getScoreTable_positve_affect();

    double [][] tScore_qol8 = tScore.getScoreTable_stigma();
    double [][] tScore_qol9 = tScore.getScoreTable_upper_function();
    double [][] tScore_qol10 = tScore.getScoreTable_lower_function();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOptionnalQuestionnairesBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        retrieveInfos();
        displayFSMC();
        displaysleep();
        /*
        checkQoL();
        checkQoL1();
        checkQoL2();
        checkQoL3();
        checkQoL4();
        checkQoL5();
        checkQoL7();
        checkQoL8();
        checkQoL9();
        checkQoL10();
        checkSkipped();

         */
        checkQuestionnaireDone();


        listenRadioGroup();
        listenBtnConfirm();


    }

    private void init(){
        Intent intent = getIntent();
        langue = intent.getStringExtra("langue");
        patientID = intent.getStringExtra("patientID");
        date = intent.getStringExtra("date");
        caseID = intent.getStringExtra("caseID");
        diagnosis = intent.getStringExtra("diagnosis");


        questionnaire = "";

        context = LocaleHelper.setLocale(getApplicationContext(), langue);
        resources = context.getResources();

        writeCSV = WriteCSV.getInstance(this);

        csv_path = getExternalFilesDir(null).getAbsolutePath() + "/"+patientID+"/";
        exist_file_sleep = writeCSV.checkFileName(patientID + "_result_sleep.csv", csv_path);
        exist_file_fsmc = writeCSV.checkFileName(patientID + "_result_fsmc.csv", csv_path);
        exist_file_bdi = writeCSV.checkFileName(patientID + "_result_bdi.csv", csv_path);

    }

    /*
    private void checkSkipped(){
        if(Integer.parseInt(skippedQuestionQOL1)>4) {
            binding.rdBtnQol1.setClickable(true);
        }if(Integer.parseInt(skippedQuestionQOL2)>4) {
            binding.rdBtnQol2.setClickable(true);
        }if(Integer.parseInt(skippedQuestionQOL3)>4) {
            binding.rdBtnQol3.setClickable(true);
        }if(Integer.parseInt(skippedQuestionQOL4)>4) {
            binding.rdBtnQol4.setClickable(true);
        }if(Integer.parseInt(skippedQuestionQOL5)>4) {
            binding.rdBtnQol5.setClickable(true);
        }if(Integer.parseInt(skippedQuestionQOL7)>5) {
            binding.rdBtnQol7.setClickable(true);
        }if(Integer.parseInt(skippedQuestionQOL8)>4) {
            binding.rdBtnQol8.setClickable(true);
        }if(Integer.parseInt(skippedQuestionQOL9)>4) {
            binding.rdBtnQol9.setClickable(true);
        }if(Integer.parseInt(skippedQuestionQOL10)>4) {
            binding.rdBtnQol10.setClickable(true);
        }
    }


     */
    private void checkQuestionnaireDone(){
        if (fsmc.equals("done") && sleep.equals("done") /*&& qol1.equals("done") && qol2.equals("done") && qol3.equals("done") && qol4.equals("done") && qol5.equals("done") && qol7.equals("done") && qol8.equals("done") && qol9.equals("done") && qol10.equals("done")*/) {
            uploadData("infos.csv");
        /*
            binding.rdBtnQol1.setVisibility(View.GONE);
            binding.rdBtnQol2.setVisibility(View.GONE);
            binding.rdBtnQol3.setVisibility(View.GONE);
            binding.rdBtnQol4.setVisibility(View.GONE);
            binding.rdBtnQol5.setVisibility(View.GONE);
            binding.rdBtnQol7.setVisibility(View.GONE);
            binding.rdBtnQol8.setVisibility(View.GONE);
            binding.rdBtnQol9.setVisibility(View.GONE);
            binding.rdBtnQol10.setVisibility(View.GONE);


            binding.txtQuestionQol1.setVisibility(View.GONE);
            binding.txtQuestionQol2.setVisibility(View.GONE);
            binding.txtQuestionQol3.setVisibility(View.GONE);
            binding.txtQuestionQol4.setVisibility(View.GONE);
            binding.txtQuestionQol5.setVisibility(View.GONE);
            binding.txtQuestionQol7.setVisibility(View.GONE);
            binding.txtQuestionQol8.setVisibility(View.GONE);
            binding.txtQuestionQol9.setVisibility(View.GONE);
            binding.txtQuestionQol10.setVisibility(View.GONE);

            binding.txtRawValueQol1.setVisibility(View.GONE);
            binding.txtRawValueQol2.setVisibility(View.GONE);
            binding.txtRawValueQol3.setVisibility(View.GONE);
            binding.txtRawValueQol4.setVisibility(View.GONE);
            binding.txtRawValueQol5.setVisibility(View.GONE);
            binding.txtRawValueQol7.setVisibility(View.GONE);
            binding.txtRawValueQol8.setVisibility(View.GONE);
            binding.txtRawValueQol9.setVisibility(View.GONE);
            binding.txtRawValueQol10.setVisibility(View.GONE);

            binding.imgDoneQol1.setVisibility(View.GONE);
            binding.imgDoneQol2.setVisibility(View.GONE);
            binding.imgDoneQol3.setVisibility(View.GONE);
            binding.imgDoneQol4.setVisibility(View.GONE);
            binding.imgDoneQol5.setVisibility(View.GONE);
            binding.imgDoneQol7.setVisibility(View.GONE);
            binding.imgDoneQol8.setVisibility(View.GONE);
            binding.imgDoneQol9.setVisibility(View.GONE);
            binding.imgDoneQol10.setVisibility(View.GONE);


         */

            binding.btnConfirm.setVisibility(View.GONE);
            initTab();
        }
    }

    private void retrieveInfos() {
        String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/" + patientID + "/infos.csv";

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
            int sleepIndex = 61;
            int FSMCIndex = 65;

            List<String[]> csvEntries = reader.readAll();
            String[] firstRow = csvEntries.get(1);


            qol = firstRow[qolColumnIndex];

            avg_score_fatigue = (firstRow[fatigueColumnIndex+1]);
            avg_score_qol = (firstRow[qolColumnIndex+1]);


            questionAnsFatigue = (firstRow[fatigueColumnIndex+2]);
            questionAnsDep = (firstRow[depressionColumnIndex+3]);
            questionAnsQol = (firstRow[qolColumnIndex+2]);


            lastQuestionFatigue = firstRow[fatigueColumnIndex + 3];
            skipped_question_anx = firstRow[depressionColumnIndex + 5];
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

            sleep = firstRow[sleepIndex];
            score_sleep = firstRow[sleepIndex + 1];
            questionAnsSleep = firstRow[sleepIndex + 2];
            skipped_question_sleep = firstRow[sleepIndex + 3];

            fsmc = firstRow[FSMCIndex];
            score_fsmc = firstRow[FSMCIndex + 1];
            questionAnsFSMC = firstRow[FSMCIndex + 2];
            skipped_question_fsmc = firstRow[FSMCIndex + 3];

            bdi = firstRow[bdiIndex];
            score_bdi = firstRow[bdiIndex + 1];
            questionAnsBDI = firstRow[bdiIndex + 2];
            skipped_question_bdi = firstRow[bdiIndex + 3];


            reader.close();

            Log.d("TEST", "QOL" + bdi + questionAnsBDI);

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void displayFSMC(){
        if(!diagnosis.equals("Stroke")){
            if(!fsmc.equals("null")){
                binding.txtQuestionFatigueFSMC.setText("Question answered : "+ (Integer.parseInt(questionAnsFSMC) -1) +"/ 20 - ("+skipped_question_fsmc+" skipped)" );

                if(fsmc.equals("done")){
                    binding.imgDoneFsmc.setVisibility(View.VISIBLE);
                    if(score_fsmc.equals("0")){
                        binding.txtQuestionFatigueFSMC.setVisibility(View.GONE);
                        binding.txtQuestionnaireSkippedFsmc.setVisibility(View.VISIBLE);
                    }else{
                        binding.rdBtnFSMC.setClickable(false);
                        createResultFSMCCSV();
                        uploadData("result_fsmc.csv");
                        binding.txtQuestionFatigueFSMC.setVisibility(View.VISIBLE);
                        binding.txtRawValueFsmc.setText("Total score : " + score_fsmc  +"/ 80");
                        binding.txtRawValueFsmc.setVisibility(View.VISIBLE);
                    }

                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void displaysleep(){
        if(!sleep.equals("null")){
            binding.txtQuestionFatigueSleep.setText("Question answered : "+ (Integer.parseInt(questionAnsSleep) - 1) +"/ 8  - ("+skipped_question_sleep+" skipped)" );

            if(sleep.equals("done")){
                binding.imgDoneSleep.setVisibility(View.VISIBLE);
                if(score_sleep.equals("0")){
                    binding.txtQuestionFatigueSleep.setVisibility(View.GONE);
                    binding.txtQuestionnaireSkipped.setVisibility(View.VISIBLE);
                }else{
                    binding.rdBtnESSFatigue.setClickable(false);
                    createResultSleepCSV();
                    uploadData("result_sleep.csv");
                    binding.txtQuestionFatigueSleep.setVisibility(View.VISIBLE);
                    binding.txtRawValueSleep.setText("Total score : " + score_sleep  +"/ 24");
                    binding.txtRawValueSleep.setVisibility(View.VISIBLE);
                }

            }
        }
    }

    private void createResultSleepCSV(){
        if(!exist_file_sleep){
            writeCSV.createAndWriteSleepResult(csv_path+"/"+patientID+"_result_sleep.csv", patientID, caseID, date,score_sleep);
        }
    }

    private void createResultFSMCCSV(){
        if(!exist_file_fsmc){
            writeCSV.createAndWriteFSMCResult(csv_path+"/"+patientID+"_result_fsmc.csv", patientID, caseID, date,score_fsmc);
        }
    }




    private void initTab(){

        binding.tableLayout.setVisibility(View.VISIBLE);
        binding.btnConfirm.setVisibility(View.GONE);

        binding.cellScoreSleep.setText(score_sleep);
        displayResult("sleep");
        binding.cellFSMCScore.setText(score_fsmc);
        displayResult("fsmc");


        /*

        binding.cellqol1Score.setText(scoreQOL1);
        binding.cellqol2Score.setText(scoreQOL2);
        binding.cellqol3Score.setText(scoreQOL3);
        binding.cellqol4Score.setText(scoreQOL4);
        binding.cellqol5Score.setText(scoreQOL5);
        binding.cellqol7Score.setText(scoreQOL7);
        binding.cellqol8Score.setText(scoreQOL8);
        binding.cellqol9Score.setText(scoreQOL9);
        binding.cellqol10Score.setText(scoreQOL10);







        displayInterpretations("qol1");
        displayInterpretations("qol2");
        displayInterpretations("qol3");
        displayInterpretations("qol4");
        displayInterpretations("qol5");
        displayInterpretations("qol7");
        displayInterpretations("qol8");
        displayInterpretations("qol9");
        displayInterpretations("qol10");




         */

    }

    /*
    private String displayInterpretations(String categorie){

        if(categorie.equals("qol1")) {
            if (scoreQOL1.equals("No t-score") || Integer.parseInt(skippedQuestionQOL1)>3) {
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellqol1Result.setColorSections(upperlimit, colors);
                binding.cellqol1Result.setUserScore(2.75f);
                binding.cellqol1Result.setUserText(txt_interpretations);

            } else {
                if (Double.parseDouble(scoreQOL1) > 70) {
                    txt_interpretations = "Severely increased";
                } else if (Double.parseDouble(scoreQOL1) < 71 && Double.parseDouble(scoreQOL1) > 60) {
                    txt_interpretations = "Moderately increased";

                } else if (Double.parseDouble(scoreQOL1) < 61 && Double.parseDouble(scoreQOL1) > 54) {
                    txt_interpretations = "Mildly increased";

                } else if (Double.parseDouble(scoreQOL1) < 55) {
                    txt_interpretations = "Within normal limits";

                }
                int[] colors = {android.graphics.Color.GREEN, Color.YELLOW, Color.rgb(255,165,0), Color.RED};

                float min = 32.2f;
                float greenEnd = 55.0f;
                float yellowEnd = 61.0f;
                float orangeEnd = 71.0f;
                float redEnd = 82.0f;

                float[] upperlimit = {binding.cellqol1Result.rescaleValue(greenEnd,min, redEnd, 70.0f),
                        binding.cellqol1Result.rescaleValue(yellowEnd, min, redEnd,70.0f),
                        binding.cellqol1Result.rescaleValue(orangeEnd, min, redEnd,70.0f),
                        binding.cellqol1Result.rescaleValue(redEnd, min, redEnd,70.0f)};

                float user_score = Float.parseFloat(scoreQOL1);
                float user_score_rescale = binding.cellqol1Result.rescaleValue(user_score, min, redEnd,70.0f);

                binding.cellqol1Result.setColorSections(upperlimit, colors);
                binding.cellqol1Result.setUserScore(user_score_rescale);
                binding.cellqol1Result.setUserText(txt_interpretations);

            }
        }

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

        if(categorie.equals("qol10")) {
            if (scoreQOL10.equals("No t-score") || Integer.parseInt(skippedQuestionQOL10) > 3) {
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128, 128, 128)};
                float[] upperlimit = {5.5f};
                binding.cellqol10Result.setColorSections(upperlimit, colors);
                binding.cellqol10Result.setUserScore(2.75f);
                binding.cellqol10Result.setUserText(txt_interpretations);
            } else {

                if (Double.parseDouble(scoreQOL10) > 45) {
                    txt_interpretations = "Within normal limits";

                } else if (Double.parseDouble(scoreQOL10) < 46 && Double.parseDouble(scoreQOL10) > 39) {
                    txt_interpretations = "Mild impairment";

                } else if (Double.parseDouble(scoreQOL10) < 40 && (Double.parseDouble(scoreQOL10) > 29)) {
                    txt_interpretations = "Moderate impairment";

                } else if (Double.parseDouble(scoreQOL10) < 30) {
                    txt_interpretations = "Severe impairment";

                }
                int[] colors = {android.graphics.Color.RED, Color.rgb(255, 165, 0), Color.YELLOW, Color.GREEN};

                float min = 16.5f;
                float greenEnd = 30.0f;
                float yellowEnd = 40.0f;
                float orangeEnd = 45.0f;
                float redEnd = 58.6f;

                float[] upperlimit = {binding.cellqol10Result.rescaleValue(greenEnd, min, redEnd, 70.0f),
                        binding.cellqol10Result.rescaleValue(yellowEnd, min, redEnd, 70.0f),
                        binding.cellqol10Result.rescaleValue(orangeEnd, min, redEnd, 70.0f),
                        binding.cellqol10Result.rescaleValue(redEnd, min, redEnd, 70.0f)};

                float user_score = Float.parseFloat(scoreQOL10);
                float user_score_rescale = binding.cellqol10Result.rescaleValue(user_score, min, redEnd, 70.0f);

                binding.cellqol10Result.setColorSections(upperlimit, colors);
                binding.cellqol10Result.setUserScore(user_score_rescale);
                binding.cellqol10Result.setUserText(txt_interpretations);
            }
        }

        return txt_interpretations;
    }


    private void checkQoL() {
        if (!qol.equals("null")) {
            binding.linearQol.setVisibility(View.GONE);
            binding.imgQoLDone.setImageResource(R.drawable.started);
            binding.imgQoLDone.setVisibility(View.VISIBLE);
            if(qol1.equals("done") && qol2.equals("done") && qol3.equals("done") && qol4.equals("done")
                    &&qol5.equals("done") &&qol7.equals("done") &&qol8.equals("done") &&qol9.equals("done") &&qol10.equals("done")){

                //uploadData(patientID+"_QOL.csv");
                if(Integer.parseInt(skippedQuestionQOL1)<4 && Integer.parseInt(skippedQuestionQOL2)<4 && Integer.parseInt(skippedQuestionQOL3)<4
                        && Integer.parseInt(skippedQuestionQOL4)<4 && Integer.parseInt(skippedQuestionQOL5)<4 && Integer.parseInt(skippedQuestionQOL7)<5
                        && Integer.parseInt(skippedQuestionQOL8)<4 && Integer.parseInt(skippedQuestionQOL9)<4 && Integer.parseInt(skippedQuestionQOL10)<4){
                    modifyDoneQOL();
                    qol="done";
                    binding.imgQoLDone.setImageResource(R.drawable.questionnaire_done);
                }
                else{
                    binding.imgQoLDone.setImageResource(R.drawable.skipped);
                }

            }
        }


    }
    private void modifyDoneQOL(){

        String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+patientID+"/infos.csv";

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            int qolColumnIndex = 21;

            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[qolColumnIndex] = "done";
            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            writer.writeAll(csvEntries);
            writer.close();


            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }


    }
    private void modifyQolDone(String qol){

        String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+patientID+"/infos.csv";

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
            row[qolColumnIndex] = "done";

            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            writer.writeAll(csvEntries);
            writer.close();


            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }


    }

    private void checkFinishQol(){
        if(Integer.parseInt(questionAnsQOL1) == 8 && Integer.parseInt(skippedQuestionQOL1) <4){
            modifyQolDone("qol1");
        }if(Integer.parseInt(questionAnsQOL2) == 8 && Integer.parseInt(skippedQuestionQOL2) <4){
            modifyQolDone("qol2");
        }if(Integer.parseInt(questionAnsQOL3) == 8 && Integer.parseInt(skippedQuestionQOL3) <4){
            modifyQolDone("qol3");
        }if(Integer.parseInt(questionAnsQOL4) == 8 && Integer.parseInt(skippedQuestionQOL4) <4){
            modifyQolDone("qol4");
        }if(Integer.parseInt(questionAnsQOL5) == 8 && Integer.parseInt(skippedQuestionQOL5) <4){
            modifyQolDone("qol5");
        }if(Integer.parseInt(questionAnsQOL7) == 9 && Integer.parseInt(skippedQuestionQOL7) <5){
            modifyQolDone("qol7");
        }if(Integer.parseInt(questionAnsQOL8) == 8 && Integer.parseInt(skippedQuestionQOL8) <4){
            modifyQolDone("qol8");
        }if(Integer.parseInt(questionAnsQOL9) == 8 && Integer.parseInt(skippedQuestionQOL9) <4){
            modifyQolDone("qol9");
        }if(Integer.parseInt(questionAnsQOL10) == 8 && Integer.parseInt(skippedQuestionQOL10) <4){
            modifyQolDone("qol10");
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkQoL1() {
        if (!qol1.equals("null")) {
            restart_qol1 = true;
            binding.txtQuestionQol1.setText("Questions answered " +(Integer.parseInt(questionAnsQOL1) - 1) + " / 8");
            if(qol1.equals("done")){
                binding.txtRawValueQol1.setVisibility(View.VISIBLE);
                binding.txtQuestionQol1.setVisibility(View.GONE);
                binding.imgDoneQol1.setVisibility(View.VISIBLE);
                if(!scoreQOL1.equals("0")){
                    if(Integer.parseInt(skippedQuestionQOL1)<4){
                        binding.rdBtnQol1.setClickable(false);
                        if (Integer.parseInt(skippedQuestionQOL1) == 0) {
                            double score = tScore_qol1[Integer.parseInt(scoreQOL1) - 8 ][1];
                            binding.txtRawValueQol1.setText("T-Score : " + score);
                        }else{
                            int score_round = (Integer.parseInt(scoreQOL1) * 8) / (8-Integer.parseInt(skippedQuestionQOL1));
                            double score_prorated = tScore_qol1[score_round - 8 ][1];
                            binding.txtRawValueQol1.setText("T-Score : " + score_prorated+ " - (Questions skipped : " + skippedQuestionQOL1+")");
                        }
                    }else{
                        binding.txtRawValueQol1.setText("Questions skipped : " + skippedQuestionQOL1);
                        binding.imgDoneQol1.setImageResource(R.drawable.questionnaire_done);
                    }
                }else{
                    binding.txtRawValueQol1.setText("Questionnaire skipped");
                    binding.imgDoneQol1.setImageResource(R.drawable.questionnaire_done);
                }



            }
        }


    }
    @SuppressLint("SetTextI18n")
    private void checkQoL2() {
        if (!qol2.equals("null")) {
            restart_qol2 = true;
            binding.txtQuestionQol2.setText("Questions answered " +questionAnsQOL2 + " / 8");

            if(qol2.equals("done")){
                binding.txtRawValueQol2.setVisibility(View.VISIBLE);
                binding.txtQuestionQol2.setVisibility(View.GONE);
                binding.imgDoneQol2.setVisibility(View.VISIBLE);
                if(!scoreQOL2.equals("0")){
                    if(Integer.parseInt(skippedQuestionQOL2)<4){
                        binding.rdBtnQol2.setClickable(false);
                        if (Integer.parseInt(skippedQuestionQOL2) == 0) {
                            double score = tScore_qol2[Integer.parseInt(scoreQOL2) - 8 ][1];
                            binding.txtRawValueQol2.setText("T-Score : " + score);
                        }else{
                            int score_round = (Integer.parseInt(scoreQOL2) * 8) / (8-Integer.parseInt(skippedQuestionQOL2));
                            double score_prorated = tScore_qol2[score_round - 8 ][1];
                            binding.txtRawValueQol2.setText("T-Score : " + score_prorated+ " - (Questions skipped : " + skippedQuestionQOL2+")");
                        }
                    }else{
                        binding.txtRawValueQol2.setText("Questions skipped : " + skippedQuestionQOL2);
                        binding.imgDoneQol2.setImageResource(R.drawable.questionnaire_done);}
                }else{
                    binding.txtRawValueQol2.setText("Questionnaire skipped");
                    binding.imgDoneQol2.setImageResource(R.drawable.questionnaire_done);
                }

            }
        }


    }
    @SuppressLint("SetTextI18n")
    private void checkQoL3() {
        if (!qol3.equals("null")) {
            restart_qol3 = true;
            binding.txtQuestionQol3.setText("Questions answered " +questionAnsQOL3 + " / 8");

            if(qol3.equals("done")){
                binding.imgDoneQol3.setVisibility(View.VISIBLE);
                binding.txtRawValueQol3.setVisibility(View.VISIBLE);
                binding.txtQuestionQol3.setVisibility(View.GONE);
                if(!scoreQOL3.equals("0")){
                    if(Integer.parseInt(skippedQuestionQOL3)<4){
                        binding.rdBtnQol3.setClickable(false);

                        if (Integer.parseInt(skippedQuestionQOL3) == 0) {
                            double score = tScore_qol3[Integer.parseInt(scoreQOL3) - 8 ][1];
                            binding.txtRawValueQol3.setText("T-Score : " + score);
                        }else{
                            int score_round = (Integer.parseInt(scoreQOL3) * 8) / (8-Integer.parseInt(skippedQuestionQOL3));
                            double score_prorated = tScore_qol3[score_round - 8 ][1];
                            binding.txtRawValueQol3.setText("T-Score : " + score_prorated+ " - (Questions skipped : " + skippedQuestionQOL3+")");
                        }
                    }else{
                        binding.txtRawValueQol3.setText("Questions skipped : " + skippedQuestionQOL3);
                        binding.imgDoneQol3.setImageResource(R.drawable.questionnaire_done);
                    }
                }else{
                    binding.txtRawValueQol3.setText("Questionnaire skipped");
                    binding.imgDoneQol3.setImageResource(R.drawable.questionnaire_done);
                }

            }
        }


    }
    @SuppressLint("SetTextI18n")
    private void checkQoL4() {
        if (!qol4.equals("null")) {
            restart_qol4 = true;
            binding.txtQuestionQol4.setText("Questions answered " +questionAnsQOL4 + " / 8");

            if(qol4.equals("done")){
                binding.imgDoneQol4.setVisibility(View.VISIBLE);
                binding.txtRawValueQol4.setVisibility(View.VISIBLE);
                binding.txtQuestionQol4.setVisibility(View.GONE);
                if(!scoreQOL4.equals("0")){
                    if(Integer.parseInt(skippedQuestionQOL4)<4){
                        binding.rdBtnQol4.setClickable(false);

                        if (Integer.parseInt(skippedQuestionQOL4) == 0) {
                            double score = tScore_qol4[Integer.parseInt(scoreQOL4) - 8 ][1];
                            binding.txtRawValueQol4.setText("T-Score : " + score);
                        }else{
                            int score_round = (Integer.parseInt(scoreQOL4) * 8) / (8-Integer.parseInt(skippedQuestionQOL4));
                            double score_prorated = tScore_qol4[score_round - 8 ][1];
                            binding.txtRawValueQol4.setText("T-Score : " + score_prorated + " - (Questions skipped : " + skippedQuestionQOL4+")");
                        }
                    }else{
                        binding.txtRawValueQol4.setText("Questions skipped : " + skippedQuestionQOL4);
                        binding.imgDoneQol4.setImageResource(R.drawable.questionnaire_done);
                    }
                }else{
                    binding.txtRawValueQol4.setText("Questionnaire skipped");
                    binding.imgDoneQol4.setImageResource(R.drawable.questionnaire_done);
                }

            }
        }


    }
    @SuppressLint("SetTextI18n")
    private void checkQoL5() {
        if (!qol5.equals("null")) {
            restart_qol5 = true;
            binding.txtQuestionQol5.setText("Questions answered " +questionAnsQOL5 + " / 8");

            if(qol5.equals("done")){
                binding.imgDoneQol5.setVisibility(View.VISIBLE);
                binding.txtRawValueQol5.setVisibility(View.VISIBLE);
                binding.txtQuestionQol5.setVisibility(View.GONE);
                if(!scoreQOL5.equals("0")){
                    if(Integer.parseInt(skippedQuestionQOL5)<4){
                        binding.rdBtnQol5.setClickable(false);

                        if (Integer.parseInt(skippedQuestionQOL5) == 0) {
                            double score = tScore_qol5[Integer.parseInt(scoreQOL5) - 8 ][1];
                            binding.txtRawValueQol5.setText("T-Score : " + score);
                        }else{
                            int score_round = (Integer.parseInt(scoreQOL5) * 8) / (8-Integer.parseInt(skippedQuestionQOL5));
                            double score_prorated = tScore_qol5[score_round - 8 ][1];
                            binding.txtRawValueQol5.setText("T-Score : " + score_prorated + " - (Questions skipped : " + skippedQuestionQOL5+")");
                        }
                    }else{
                        binding.txtRawValueQol5.setText("Questions skipped : " + skippedQuestionQOL5);
                        binding.imgDoneQol5.setImageResource(R.drawable.questionnaire_done);
                    }
                }else{
                    binding.txtRawValueQol5.setText("Questionnaire skipped");
                    binding.imgDoneQol5.setImageResource(R.drawable.questionnaire_done);
                }


            }
        }


    }
    @SuppressLint("SetTextI18n")
    private void checkQoL7() {
        if (!qol7.equals("null")) {
            restart_qol7 = true;
            binding.txtQuestionQol7.setText("Questions answered " +questionAnsQOL7 + " / 9");

            if(qol7.equals("done")){
                binding.imgDoneQol7.setVisibility(View.VISIBLE);
                binding.txtQuestionQol7.setVisibility(View.GONE);
                binding.txtRawValueQol7.setVisibility(View.VISIBLE);
                if(!scoreQOL7.equals("0")){
                    if(Integer.parseInt(skippedQuestionQOL7)<5){
                        binding.rdBtnQol7.setClickable(false);


                        if (Integer.parseInt(skippedQuestionQOL7) == 0) {
                            double score = tScore_qol7[Integer.parseInt(scoreQOL7) - 9 ][1];
                            binding.txtRawValueQol7.setText("T-Score : " + score);
                        }else{
                            int score_round = (Integer.parseInt(scoreQOL7) * 9) / (9-Integer.parseInt(skippedQuestionQOL7));
                            double score_prorated = tScore_qol7[score_round - 9 ][1];
                            binding.txtRawValueQol7.setText("T-Score : " + score_prorated + " - (Questions skipped : " + skippedQuestionQOL7+")");
                        }
                    }else{
                        binding.txtRawValueQol7.setText("Questions skipped : " + skippedQuestionQOL7);
                        binding.imgDoneQol7.setImageResource(R.drawable.questionnaire_done);
                    }
                }else{
                    binding.txtRawValueQol7.setText("Questionnaire skipped");
                    binding.imgDoneQol7.setImageResource(R.drawable.questionnaire_done);
                }


            }
        }
    }
    @SuppressLint("SetTextI18n")
    private void checkQoL8() {
        if (!qol8.equals("null")) {
            restart_qol8 = true;
            binding.txtQuestionQol8.setText("Questions answered : " +questionAnsQOL8 + " / 8");

            if(qol8.equals("done")){
                binding.imgDoneQol8.setVisibility(View.VISIBLE);
                binding.txtRawValueQol8.setVisibility(View.VISIBLE);
                binding.txtQuestionQol8.setVisibility(View.GONE);
                if (!scoreQOL8.equals("0")) {
                    if(Integer.parseInt(skippedQuestionQOL8)<4){
                        binding.rdBtnQol8.setClickable(false);


                        if (Integer.parseInt(skippedQuestionQOL8) == 0) {
                            double score = tScore_qol8[Integer.parseInt(scoreQOL8) - 8 ][1];
                            binding.txtRawValueQol8.setText("T-Score : " + score);
                        }else{
                            int score_round = (Integer.parseInt(scoreQOL8) * 8) / (8-Integer.parseInt(skippedQuestionQOL8));
                            double score_prorated = tScore_qol8[score_round - 8 ][1];
                            binding.txtRawValueQol8.setText("T-Score : " + score_prorated + " - (Questions skipped : " + skippedQuestionQOL8+")");
                        }
                    }else{
                        binding.txtRawValueQol8.setText("Questions skipped : " + skippedQuestionQOL8);
                        binding.imgDoneQol8.setImageResource(R.drawable.questionnaire_done);
                    }
                }else{
                    binding.txtRawValueQol8.setText("Questionnaire skipped");
                    binding.imgDoneQol8.setImageResource(R.drawable.questionnaire_done);
                }

            }

        }


    }
    @SuppressLint("SetTextI18n")
    private void checkQoL9() {
        if (!qol9.equals("null")) {
            restart_qol9 = true;
            binding.txtQuestionQol9.setText("Questions answered " +questionAnsQOL9 + " / 8");

            if(qol9.equals("done")){
                binding.imgDoneQol9.setVisibility(View.VISIBLE);
                binding.txtRawValueQol9.setVisibility(View.VISIBLE);
                binding.txtQuestionQol9.setVisibility(View.GONE);
                if(!scoreQOL9.equals("0")){
                    if(Integer.parseInt(skippedQuestionQOL9)<4){
                        binding.rdBtnQol9.setClickable(false);

                        if (Integer.parseInt(skippedQuestionQOL9) == 0) {
                            double score = tScore_qol9[Integer.parseInt(scoreQOL9) - 8 ][1];
                            binding.txtRawValueQol9.setText("T-Score : " + score);
                        }else{
                            int score_round = (Integer.parseInt(scoreQOL9) * 8) / (8-Integer.parseInt(skippedQuestionQOL9));
                            double score_prorated = tScore_qol9[score_round - 8][1];
                            binding.txtRawValueQol9.setText("T-Score : " + score_prorated + " - (Questions skipped : " + skippedQuestionQOL9+")");
                        }

                    }else{

                        binding.txtRawValueQol9.setText("Questions skipped : " + skippedQuestionQOL9);
                        binding.imgDoneQol9.setImageResource(R.drawable.questionnaire_done);
                    }
                }else{
                    binding.txtRawValueQol9.setText("Questionnaire skipped");
                    binding.imgDoneQol9.setImageResource(R.drawable.questionnaire_done);
                }


            }
        }


    }
    @SuppressLint("SetTextI18n")
    private void checkQoL10() {
        if (!qol10.equals("null")) {
            restart_qol10 = true;
            binding.txtQuestionQol10.setText("Questions answered " +questionAnsQOL10 + " / 8");

            if(qol10.equals("done")){
                binding.imgDoneQol10.setVisibility(View.VISIBLE);
                binding.txtRawValueQol10.setVisibility(View.VISIBLE);
                binding.txtQuestionQol10.setVisibility(View.GONE);
                if(!scoreQOL10.equals("0")){
                    if(Integer.parseInt(skippedQuestionQOL10)<4){
                        binding.rdBtnQol10.setClickable(false);

                        if (Integer.parseInt(skippedQuestionQOL10) == 0) {
                            double score = tScore_qol10[Integer.parseInt(scoreQOL10) - 8][1];
                            binding.txtRawValueQol10.setText("T-Score : " + score);
                        }else{
                            int score_round = (Integer.parseInt(scoreQOL10) * 8) / (8-Integer.parseInt(skippedQuestionQOL10));
                            double score_prorated = tScore_qol10[score_round - 8][1];
                            binding.txtRawValueQol10.setText("T-Score : " + score_prorated + " - (Questions skipped : " + skippedQuestionQOL10+")");
                        }
                    }else{
                        binding.txtRawValueQol10.setText("Questions skipped : " + skippedQuestionQOL10);
                        binding.imgDoneQol10.setImageResource(R.drawable.questionnaire_done);
                    }
                }else{
                    binding.txtRawValueQol10.setText("Questionnaire skipped");
                    binding.imgDoneQol10.setImageResource(R.drawable.questionnaire_done);
                }




            }

        }


    }


     */

    private void displayResult(String categorie){
        String text_interpretations;
        if(categorie.equals("sleep")){
            if(score_sleep.equals("0")){
                text_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellFSMCResult.setColorSections(upperlimit, colors);
                binding.cellFSMCResult.setUserScore(2.75f);
                binding.cellFSMCResult.setUserText(text_interpretations);
            }else{
                if(Integer.parseInt(score_sleep)<4){
                    text_interpretations = "Good";
                } else if (Integer.parseInt(score_sleep)<10) {
                    text_interpretations = "Moderate";
                }else
                {
                    text_interpretations = "Severe";
                }
                int[] colors = {android.graphics.Color.GREEN, Color.YELLOW, Color.RED};

                float min = 0.0f;
                float greenEnd = 4.0f;
                float yellowEnd = 10.0f;
                float redEnd = 24.0f;

                float[] upperlimit = {binding.cellResultSleep.rescaleValue(greenEnd,min,redEnd,  40.0f),
                        binding.cellResultSleep.rescaleValue(yellowEnd, min,redEnd, 40.0f),
                        binding.cellResultSleep.rescaleValue(redEnd, min,redEnd, 40.0f)};

                float user_score = Float.parseFloat(score_sleep);
                float user_score_rescale = binding.cellResultSleep.rescaleValue(user_score,min,redEnd, 40.0f);

                binding.cellResultSleep.setColorSections(upperlimit, colors);
                binding.cellResultSleep.setUserScore(user_score_rescale);
                binding.cellResultSleep.setUserText(text_interpretations);
            }
        }

        if(categorie.equals("fsmc")){
            if (score_fsmc.equals("0")) {
                String txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellFSMCResult.setColorSections(upperlimit, colors);
                binding.cellFSMCResult.setUserScore(2.75f);
                binding.cellFSMCResult.setUserText(txt_interpretations);

            }else{
                String txt_interpretations  ="";
                if(Integer.parseInt(score_fsmc)<43){
                    txt_interpretations = "Good";
                } else if (Integer.parseInt(score_fsmc)<53) {
                    txt_interpretations = "Leichte Fatigue";
                } else if (Integer.parseInt(score_fsmc)<63) {
                    txt_interpretations = "Mittelgradige  Fatigue";
                } else if (Integer.parseInt(score_fsmc)>62) {
                    txt_interpretations = "Schwere Fatigue";
                }
                int[] colors = {android.graphics.Color.GREEN, Color.YELLOW,Color.rgb(255,165,0), Color.RED};

                float min = 0.0f;
                float greenEnd = 43.0f;
                float yellowEnd = 53.0f;
                float orangeEnd = 63.0f;
                float redEnd = 80.0f;

                float[] upperlimit = {binding.cellFSMCResult.rescaleValue(greenEnd,min,redEnd,  40.0f),
                        binding.cellFSMCResult.rescaleValue(yellowEnd, min,redEnd, 40.0f),
                        binding.cellFSMCResult.rescaleValue(orangeEnd, min,redEnd, 40.0f),
                        binding.cellFSMCResult.rescaleValue(redEnd, min,redEnd, 40.0f)};

                float user_score = Float.parseFloat(score_fsmc);
                float user_score_rescale = binding.cellFSMCResult.rescaleValue(user_score,min,redEnd, 40.0f);

                binding.cellFSMCResult.setColorSections(upperlimit, colors);
                binding.cellFSMCResult.setUserScore(user_score_rescale);
                binding.cellFSMCResult.setUserText(txt_interpretations);
            }

        }
    }

    private void listenRadioGroup() {
        binding.radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rdBtnESSFatigue) {
                    questionnaire = "sleep";

                    if (Integer.parseInt(skipped_question_sleep) > 3)
                        redo_questionnaire = true;

                } else if (checkedId == R.id.rdBtnFSMC) {
                    questionnaire = "fsmc";
                    if (Integer.parseInt(skipped_question_fsmc) > 4)
                        redo_questionnaire = true;
                }
                /*
                else if (checkedId == R.id.rdBtnQol1) {
                    questionnaire = "qol1";
                    if (Integer.parseInt(skippedQuestionQOL1) > 3) {
                        redo_questionnaire = true;
                    }
                } else if (checkedId == R.id.rdBtnQol2) {
                    questionnaire = "qol2";
                    if (Integer.parseInt(skippedQuestionQOL2) > 3)
                        redo_questionnaire = true;
                } else if (checkedId == R.id.rdBtnQol3) {
                    questionnaire = "qol3";
                    if (Integer.parseInt(skippedQuestionQOL3) > 3)
                        redo_questionnaire = true;
                } else if (checkedId == R.id.rdBtnQol4) {
                    questionnaire = "qol4";
                    if (Integer.parseInt(skippedQuestionQOL4) > 3)
                        redo_questionnaire = true;
                } else if (checkedId == R.id.rdBtnQol5) {
                    questionnaire = "qol5";
                    if (Integer.parseInt(skippedQuestionQOL5) > 3)
                        redo_questionnaire = true;
                } else if (checkedId == R.id.rdBtnQol7) {
                    questionnaire = "qol7";
                    if (Integer.parseInt(skippedQuestionQOL7) > 4)
                        redo_questionnaire = true;
                } else if (checkedId == R.id.rdBtnQol8) {
                    questionnaire = "qol8";
                    if (Integer.parseInt(skippedQuestionQOL8) > 3)
                        redo_questionnaire = true;
                } else if (checkedId == R.id.rdBtnQol9) {
                    questionnaire = "qol9";
                    if (Integer.parseInt(skippedQuestionQOL9) > 3)
                        redo_questionnaire = true;
                } else if (checkedId == R.id.rdBtnQol10) {
                    questionnaire = "qol10";
                    if (Integer.parseInt(skippedQuestionQOL10) > 3)
                        redo_questionnaire = true;

                }

                 */
            }
        });
    }

    private void listenBtnConfirm(){
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questionnaire.equals("sleep") || questionnaire.equals("ess_depression") || questionnaire.equals("ess_anxiety")){
                    Intent intent = new Intent(getApplicationContext(), SleepActivity.class);
                    intent.putExtra("date", date);
                    intent.putExtra("patientID", patientID);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("langue", langue);
                    intent.putExtra("questionnaire", questionnaire);
                    startActivity(intent);

                } else if (questionnaire.equals("fsmc")) {
                    Intent intent = new Intent(getApplicationContext(), IntroductionFSMCActivity.class);
                    intent.putExtra("date", date);
                    intent.putExtra("patientID", patientID);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("langue", langue);
                    intent.putExtra("questionnaire", questionnaire);
                    startActivity(intent);
                }

                /*
                else if (questionnaire.equals("qol1")) {
                    Intent intent = new Intent(getApplicationContext(), QualityofLifeActivity.class);
                    intent.putExtra("date", date);
                    intent.putExtra("patientID", patientID);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("langue", langue);
                    intent.putExtra("lastQuestion", skippedQuestionQOL);
                    intent.putExtra("redo_questionnaire", redo_questionnaire);
                    intent.putExtra("num_question", 1);
                    intent.putExtra("qol", "qol1");
                    intent.putExtra("restard", restart_qol1);
                    startActivity(intent);

                }else if (questionnaire.equals("qol2")) {
                    Intent intent = new Intent(getApplicationContext(), QualityofLifeActivity.class);
                    intent.putExtra("date", date);
                    intent.putExtra("patientID", patientID);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("langue", langue);
                    intent.putExtra("lastQuestion", skippedQuestionQOL);
                    intent.putExtra("redo_questionnaire", redo_questionnaire);
                    intent.putExtra("num_question", 9);
                    intent.putExtra("qol", "qol2");
                    intent.putExtra("restard", restart_qol2);
                    startActivity(intent);

                }else if (questionnaire.equals("qol3")) {
                    Intent intent = new Intent(getApplicationContext(), QualityofLifeActivity.class);
                    intent.putExtra("date", date);
                    intent.putExtra("patientID", patientID);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("langue", langue);
                    intent.putExtra("lastQuestion", skippedQuestionQOL);
                    intent.putExtra("redo_questionnaire", redo_questionnaire);
                    intent.putExtra("num_question", 17);
                    intent.putExtra("qol", "qol3");
                    intent.putExtra("restard", restart_qol3);
                    startActivity(intent);

                }else if (questionnaire.equals("qol4")) {
                    Intent intent = new Intent(getApplicationContext(), QualityofLifeActivity.class);
                    intent.putExtra("date", date);
                    intent.putExtra("patientID", patientID);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("langue", langue);
                    intent.putExtra("lastQuestion", skippedQuestionQOL);
                    intent.putExtra("redo_questionnaire", redo_questionnaire);
                    intent.putExtra("num_question", 25);
                    intent.putExtra("qol", "qol4");
                    intent.putExtra("restard", restart_qol4);
                    startActivity(intent);

                }else if (questionnaire.equals("qol5")) {
                    Intent intent = new Intent(getApplicationContext(), QualityofLifeActivity.class);
                    intent.putExtra("date", date);
                    intent.putExtra("patientID", patientID);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("langue", langue);
                    intent.putExtra("lastQuestion", skippedQuestionQOL);
                    intent.putExtra("redo_questionnaire", redo_questionnaire);
                    intent.putExtra("num_question", 33);
                    intent.putExtra("qol", "qol5");
                    intent.putExtra("restard", restart_qol5);
                    startActivity(intent);

                } else if (questionnaire.equals("qol7")) {
                    Intent intent = new Intent(getApplicationContext(), QualityofLifeActivity.class);
                    intent.putExtra("date", date);
                    intent.putExtra("patientID", patientID);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("langue", langue);
                    intent.putExtra("lastQuestion", skippedQuestionQOL);
                    intent.putExtra("redo_questionnaire", redo_questionnaire);
                    intent.putExtra("num_question", 49);
                    intent.putExtra("qol", "qol7");
                    intent.putExtra("restard", restart_qol7);
                    startActivity(intent);

                }else if (questionnaire.equals("qol8")) {
                    Intent intent = new Intent(getApplicationContext(), QualityofLifeActivity.class);
                    intent.putExtra("date", date);
                    intent.putExtra("patientID", patientID);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("langue", langue);
                    intent.putExtra("lastQuestion", skippedQuestionQOL);
                    intent.putExtra("redo_questionnaire", redo_questionnaire);
                    intent.putExtra("num_question", 58);
                    intent.putExtra("qol", "qol8");
                    intent.putExtra("restard", restart_qol8);
                    startActivity(intent);

                }else if (questionnaire.equals("qol9")) {
                    Intent intent = new Intent(getApplicationContext(), QualityofLifeActivity.class);
                    intent.putExtra("date", date);
                    intent.putExtra("patientID", patientID);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("langue", langue);
                    intent.putExtra("lastQuestion", skippedQuestionQOL);
                    intent.putExtra("redo_questionnaire", redo_questionnaire);
                    intent.putExtra("num_question", 66);
                    intent.putExtra("qol", "qol9");
                    intent.putExtra("restard", restart_qol9);
                    startActivity(intent);

                }else if (questionnaire.equals("qol10")) {
                    Intent intent = new Intent(getApplicationContext(), QualityofLifeActivity.class);
                    intent.putExtra("date", date);
                    intent.putExtra("patientID", patientID);
                    intent.putExtra("caseID", caseID);
                    intent.putExtra("diagnosis", diagnosis);
                    intent.putExtra("langue", langue);
                    intent.putExtra("lastQuestion", skippedQuestionQOL);
                    intent.putExtra("redo_questionnaire", redo_questionnaire);
                    intent.putExtra("num_question", 74);
                    intent.putExtra("qol", "qol10");
                    intent.putExtra("restard", restart_qol10);
                    startActivity(intent);

                }

                 */
            }
        });

    }



    private void uploadData(String file){
        SFTP sftp = new SFTP();
        folderSRC = new File(getExternalFilesDir(null).getAbsolutePath() + "/"+patientID);
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