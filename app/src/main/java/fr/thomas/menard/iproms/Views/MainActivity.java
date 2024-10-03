package fr.thomas.menard.iproms.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
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
import fr.thomas.menard.iproms.Utils.tScore;
import fr.thomas.menard.iproms.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    String patientID, date, caseID, langue, diagnosis, questionnaire, who_fill;
    Resources resources;
    Context context;


    String questionAnsFatigue, questionAnsDep, questionAnsQol, questionAnsBDI, questionAnsPROMIS;
    String avg_score_fatigue, avg_score_depression, avg_score_anxiety, avg_score_qol, avg_score_PROMIS;

    String lastQuestionFatigue, lastQuestionDep, skipped_question_anx, skippedQuestionQOL, skipped_question_bdi, skipped_question_promis;

    String fatigue, depression, qol, bdi, promis;
    String qol1, qol2, qol3, qol4, qol5, qol6, qol7, qol8, qol9, qol10;
    String skippedQuestionQOL1, skippedQuestionQOL2, skippedQuestionQOL3,skippedQuestionQOL4,skippedQuestionQOL5,skippedQuestionQOL6,skippedQuestionQOL7,skippedQuestionQOL8,skippedQuestionQOL9,skippedQuestionQOL10;

    String questionAnsQOL1, questionAnsQOL2,questionAnsQOL3,questionAnsQOL4,questionAnsQOL5,questionAnsQOL6,questionAnsQOL7,questionAnsQOL8,questionAnsQOL9,questionAnsQOL10;
    String scoreQOL1, scoreQOL2,scoreQOL3,scoreQOL4,scoreQOL5,scoreQOL6,scoreQOL7,scoreQOL8,scoreQOL9,scoreQOL10, score_bdi;

    boolean redo_questionnaire;
    boolean restart_fatigue =false, restart_dep = false, restart_promis, restart_qol1 = false, restart_qol2= false, restart_qol3= false, restart_qol4= false,
            restart_qol5= false, restart_qol6= false, restart_qol7= false, restart_qol8= false, restart_qol9= false,restart_qol10= false;


    File folderSRC;
    private static final String host = "172.20.11.10";
    private static final int port = 22;
    private static final String user = "lli_admin";
    private static final String password = "0YVjglmuevOh";
    private static String PATH_SERVER;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        init();
        retrieveInfos();
        checkQuestionnaireDone();
        skippedQuestion();
        displayText();
        checkFatigueI();
        checkDepression();
        checkBDI();
        checkPromis();
        listenBtnStart();
        listenRadioGroup();
        listenBtnResult();
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


    }


    private void retrieveInfos(){
        String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/"+patientID+"/infos.csv";

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
            int qolColumnIndex = 21;

            List<String[]> csvEntries = reader.readAll();
            String[] firstRow = csvEntries.get(1);


            fatigue = firstRow[fatigueColumnIndex];
            depression = firstRow[depressionColumnIndex];
            bdi = firstRow[bdiIndex];
            promis = firstRow[promisColumnIndex];
            qol = firstRow[qolColumnIndex];

            avg_score_fatigue = (firstRow[fatigueColumnIndex+1]);
            avg_score_depression = firstRow[depressionColumnIndex+1];
            avg_score_anxiety = firstRow[depressionColumnIndex + 2];
            score_bdi = firstRow[bdiIndex+1];
            avg_score_PROMIS = firstRow[promisColumnIndex+1];
            avg_score_qol = (firstRow[qolColumnIndex+1]);


            questionAnsFatigue = (firstRow[fatigueColumnIndex+2]);
            questionAnsDep = (firstRow[depressionColumnIndex+3]);
            questionAnsBDI = firstRow[bdiIndex+2];
            questionAnsPROMIS = firstRow[promisColumnIndex+2];
            questionAnsQol = (firstRow[qolColumnIndex+2]);


            lastQuestionFatigue = firstRow[fatigueColumnIndex + 3];
            lastQuestionDep = firstRow[depressionColumnIndex + 4];
            skipped_question_anx = firstRow[depressionColumnIndex + 5];
            skipped_question_bdi = firstRow[bdiIndex+3];
            skipped_question_promis = firstRow[promisColumnIndex+3];
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

    private void checkQuestionnaireDone() {
        if (fatigue.equals("done") && depression.equals("done") && bdi.equals("done") && promis.equals("done")) {
            uploadData("infos.csv");

            binding.btnConfirm.setVisibility(View.GONE);
            binding.btnResult.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void skippedQuestion(){
        /*
        if(!skippedQuestionQOL.equals("null") && !questionAnsQol.equals("null")) {
            int skipped_qol = Integer.parseInt(skippedQuestionQOL) - Integer.parseInt(questionAnsQol);
            binding.txtQuestionsSkippedQOL.setText("(" + skipped_qol + "  skipped)");
        }
        */
        if(!lastQuestionDep.equals("null") && !questionAnsDep.equals("null")) {
            binding.txtQuestionsSkippedDep.setText("(" + lastQuestionDep + "  skipped)");
            binding.txtQuestionsSkippedAnx.setText("(" + skipped_question_anx + "  skipped)");
        }
        if(!lastQuestionFatigue.equals("null") && !questionAnsFatigue.equals("null")) {
            binding.txtQuestionsSkippedFatigue.setText("(" + lastQuestionFatigue + "  skipped)");
        }
    }


    @SuppressLint("SetTextI18n")
    private void displayText(){
        binding.txtPatientID.setText(patientID);
        binding.txtPatientID.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        binding.txtDiagnosis.setText(diagnosis);
        binding.txtDiagnosis.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

    }

    @SuppressLint("SetTextI18n")
    private void checkBDI(){
        if(!bdi.equals("null")){
            binding.txtQuestionBDI.setText("Question answered : "+ questionAnsBDI +"/ 22  - ("+skipped_question_bdi+" skipped)" );

            if(bdi.equals("done")){
                binding.imgBDIDone.setVisibility(View.VISIBLE);
                if(score_bdi.equals("0")){
                    binding.txtQuestionnaireSkippedBdi.setVisibility(View.VISIBLE);
                    binding.txtQuestionBDI.setVisibility(View.GONE);
                }else{
                    binding.rdBtnBDI.setClickable(false);
                    uploadData("result_bdi.csv");
                    binding.txtQuestionBDI.setVisibility(View.VISIBLE);
                    binding.txtRawValueBDI.setText("Total score : " + score_bdi  +"/ 63");
                    binding.txtRawValueBDI.setVisibility(View.VISIBLE);
                }

            }
        }
    }


    private void checkFatigueI(){
        if(!fatigue.equals("null")){
            restart_fatigue = true;
            binding.nbrQuestionAnsweredFatigue.setText(String.valueOf(Integer.parseInt(questionAnsFatigue) - 1));
            binding.imgFatigueDone.setImageResource(R.drawable.started);
            binding.imgFatigueDone.setVisibility(View.VISIBLE);
            binding.txtQuestionsSkippedFatigue.setVisibility(View.INVISIBLE);
            if(fatigue.equals("done")){
                binding.imgFatigueDone.setImageResource(R.drawable.questionnaire_done);
                binding.txtQuestionsSkippedFatigue.setVisibility(View.VISIBLE);
                if(!avg_score_fatigue.equals("0")){
                    binding.imgFatigueDone.setVisibility(View.VISIBLE);
                    if(Integer.parseInt(lastQuestionFatigue) < 5){
                        double mean_fatigue = (double) Integer.parseInt(avg_score_fatigue) / (Integer.parseInt(questionAnsFatigue) - Integer.parseInt(lastQuestionFatigue) - 1);
                        long final_average_fatigue = Math.round(mean_fatigue);
                        binding.rdBtnFatigue.setClickable(false);
                        binding.linearAvgScoreFatigue.setVisibility(View.VISIBLE);
                        binding.scoreAvgFatigue.setText(String.valueOf(final_average_fatigue));
                    }else{
                        binding.linearAvgScoreFatigue.setVisibility(View.VISIBLE);
                        binding.nbrQuestionAnsweredFatigue.setText(String.valueOf(Integer.parseInt(questionAnsFatigue) - 1));


                    }
                }else{
                    binding.linearFatigue.setVisibility(View.GONE);
                    binding.txtQuestionnaireSkipped.setVisibility(View.VISIBLE);
                    binding.imgFatigueDone.setImageResource(R.drawable.questionnaire_done);
                }


            }
        }

    }

    private void checkPromis(){
        if(!promis.equals("null")){
            binding.txtQuestionPROMIS.setText("Question answered : "+ (Integer.parseInt(questionAnsPROMIS) -1) +"/ 10  - ("+skipped_question_promis+" skipped)" );

            if(promis.equals("done")){
                binding.imgPromisDone.setVisibility(View.VISIBLE);
                if(avg_score_PROMIS.equals("0")){
                    binding.txtQuestionnaireSkippedPROMIS.setVisibility(View.VISIBLE);
                    binding.txtQuestionPROMIS.setVisibility(View.GONE);
                }else{
                    binding.rdBtnPROMIS.setClickable(false);
                    uploadData("result_bdi.csv");
                    binding.txtQuestionPROMIS.setVisibility(View.VISIBLE);
                    binding.txtRawValuePROMIS.setText("Total score : " + avg_score_PROMIS  +"/ 50");
                    binding.txtRawValuePROMIS.setVisibility(View.VISIBLE);
                }

            }


        }
    }



    @SuppressLint("SetTextI18n")
    private void checkDepression() {
        if(!depression.equals("null")){
            restart_dep = true;
            binding.nbrQuestionAnsweredDep.setText(String.valueOf(Integer.parseInt(questionAnsDep) - 1));
            binding.imgDepressionDone.setImageResource(R.drawable.started);
            binding.imgDepressionDone.setVisibility(View.VISIBLE);
            binding.txtQuestionsSkippedDepAnx.setVisibility(View.INVISIBLE);
            if (depression.equals("done")) {
                binding.imgDepressionDone.setVisibility(View.VISIBLE);
                binding.imgDepressionDone.setImageResource(R.drawable.questionnaire_done);
                binding.txtQuestionsSkippedDepAnx.setVisibility(View.INVISIBLE);
                if(!avg_score_depression.equals("0") && !avg_score_anxiety.equals("0")){
                    binding.linearAvgScoreDep.setVisibility(View.VISIBLE);
                    binding.linearAvgScoreAnx.setVisibility(View.VISIBLE);
                    binding.txtQuestionsSkippedAnx.setVisibility(View.VISIBLE);
                    binding.txtQuestionsSkippedDep.setVisibility(View.VISIBLE);
                    binding.scoreAvgDep.setText(String.valueOf(avg_score_depression));
                    binding.scoreAvgAnx.setText(avg_score_anxiety);
                    binding.rdBtnDA.setClickable(Integer.parseInt(lastQuestionDep) >= 4 || Integer.parseInt(skipped_question_anx) >= 4);
                }else{
                    binding.linearDep.setVisibility(View.GONE);
                    binding.txtQuestionnaireSkippedDep.setVisibility(View.VISIBLE);
                    binding.imgDepressionDone.setImageResource(R.drawable.questionnaire_done);

                }


            }
        }

    }






    private void uploadData(String file){
        SFTP sftp = new SFTP();
        folderSRC = new File(getExternalFilesDir(null).getAbsolutePath() + "/"+patientID);
        PATH_SERVER = "data/raw_data/" + patientID + "/iPROMS/" + date + "/";
        String file_to_send = folderSRC + "/" + file;

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

    private void listenRadioGroup(){
        binding.radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rdBtnFatigue) {
                    questionnaire = "fatigue";

                    if(Integer.parseInt(lastQuestionFatigue)>3)
                        redo_questionnaire=true;

                }else if (checkedId == R.id.rdBtnDA) {
                    questionnaire = "depression";
                    if(Integer.parseInt(lastQuestionDep)>3 || Integer.parseInt(skipped_question_anx) > 3)
                        redo_questionnaire=true;
                }else if (checkedId == R.id.rdBtnPROMIS) {
                    questionnaire = "promis";
                    if(Integer.parseInt(skipped_question_promis)>3)
                        redo_questionnaire=true;
                }
                else if (checkedId == R.id.rdBtnBDI) {
                    questionnaire = "bdi";
                }


            }
        });

        binding.radioGroup2Optionnal.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rdBtnWEIMuS) {
                    questionnaire = "WEIMuS";
                } else if (checkedId == R.id.rdBtnESS) {
                    questionnaire = "ESS";
                } else {
                    questionnaire = "";
                }
            }
        });
    }

    private void startActivity(String questionnaire){
        if(questionnaire.equals("fatigue")){
            Intent intent = new Intent(getApplicationContext(), FatigueQuestionnaire.class);
            intent.putExtra("date", date);
            intent.putExtra("patientID", patientID);
            intent.putExtra("caseID", caseID);
            intent.putExtra("langue", langue);
            intent.putExtra("diagnosis", diagnosis);
            intent.putExtra("lastQuestion", lastQuestionFatigue);
            intent.putExtra("totalScore", Integer.parseInt(avg_score_fatigue));
            intent.putExtra("redo_questionnaire", redo_questionnaire);
            intent.putExtra("restard", restart_fatigue);
            intent.putExtra("who_fill", who_fill);
            startActivity(intent);


        }
        else if (questionnaire.equals("depression")) {
            Intent intent = new Intent(getApplicationContext(), DepressionAnxietyActivity.class);
            intent.putExtra("date", date);
            intent.putExtra("patientID", patientID);
            intent.putExtra("caseID", caseID);
            intent.putExtra("langue", langue);
            intent.putExtra("diagnosis", diagnosis);
            intent.putExtra("lastQuestion", lastQuestionDep);
            intent.putExtra("totalScore", Integer.parseInt(avg_score_depression));
            intent.putExtra("redo_questionnaire", redo_questionnaire);
            intent.putExtra("restard", restart_dep);

            startActivity(intent);

        }
        else if (questionnaire.equals("promis")) {
            Intent intent = new Intent(getApplicationContext(), PromisActivity.class);
            intent.putExtra("date", date);
            intent.putExtra("patientID", patientID);
            intent.putExtra("caseID", caseID);
            intent.putExtra("langue", langue);
            intent.putExtra("diagnosis", diagnosis);
            intent.putExtra("lastQuestion", lastQuestionDep);
            intent.putExtra("totalScore", Integer.parseInt(avg_score_depression));
            intent.putExtra("redo_questionnaire", redo_questionnaire);
            intent.putExtra("restard", restart_dep);

            startActivity(intent);

        }
        else if (questionnaire.equals("bdi")) {
            Intent intent = new Intent(getApplicationContext(), BDI_Activity.class);
            intent.putExtra("date", date);
            intent.putExtra("patientID", patientID);
            intent.putExtra("caseID", caseID);
            intent.putExtra("langue", langue);
            intent.putExtra("diagnosis", diagnosis);
            intent.putExtra("lastQuestion", lastQuestionDep);
            intent.putExtra("totalScore", Integer.parseInt(avg_score_depression));
            intent.putExtra("redo_questionnaire", redo_questionnaire);
            intent.putExtra("restard", restart_dep);

            startActivity(intent);

        }
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

        }else if (questionnaire.equals("qol6")) {
            Intent intent = new Intent(getApplicationContext(), QualityofLifeActivity.class);
            intent.putExtra("date", date);
            intent.putExtra("patientID", patientID);
            intent.putExtra("caseID", caseID);
            intent.putExtra("diagnosis", diagnosis);
            intent.putExtra("langue", langue);
            intent.putExtra("lastQuestion", skippedQuestionQOL);
            intent.putExtra("redo_questionnaire", redo_questionnaire);
            intent.putExtra("num_question", 41);
            intent.putExtra("qol", "qol6");
            intent.putExtra("restard", restart_qol6);
            startActivity(intent);

        }else if (questionnaire.equals("qol7")) {
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
        else{
            Toast.makeText(context, "Please select a questionnaire", Toast.LENGTH_SHORT).show();
        }
    }

    private void listenBtnStart(){
        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openPopup(questionnaire);
                startActivity(questionnaire);

            }
        });
    }

    private void listenBtnResult(){
        binding.btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SummaryActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("patientID", patientID);
                intent.putExtra("caseID", caseID);
                intent.putExtra("diagnosis", diagnosis);
                intent.putExtra("langue", langue);
                startActivity(intent);

            }
        });
    }

}