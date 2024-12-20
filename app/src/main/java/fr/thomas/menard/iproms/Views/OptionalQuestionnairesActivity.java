package fr.thomas.menard.iproms.Views;

import android.annotation.SuppressLint;
import static fr.thomas.menard.iproms.Model.InfoFile.*;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.io.File;
import fr.thomas.menard.iproms.Enum.Type;
import fr.thomas.menard.iproms.Model.MyApplication;
import fr.thomas.menard.iproms.Model.Patient;
import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.DataTransfer;
import fr.thomas.menard.iproms.Utils.FileManager;
import fr.thomas.menard.iproms.Utils.ReadCSV;
import fr.thomas.menard.iproms.Utils.WriteCSV;
import fr.thomas.menard.iproms.databinding.ActivityOptionnalQuestionnairesBinding;

public class OptionalQuestionnairesActivity extends BaseActivity {

    private ActivityOptionnalQuestionnairesBinding binding;
    private String questionnaire;
    private WriteCSV writeCSV;

    boolean redo_questionnaire;

    @Override
    public void init(){
        writeCSV = WriteCSV.getInstance(this);
    }

    @Override
    public void listenBtn() {
        ReadCSV.retrieveInfos(this);
        displayFSMC();
        displaysleep();
        checkQuestionnaireDone();

        listenRadioGroup();
        listenBtnConfirm();
    }

    @Override
    public void setBinding() {
        binding = ActivityOptionnalQuestionnairesBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    private void checkQuestionnaireDone(){
        if (fsmc.equals("done") && sleep.equals("done") /*&& qol1.equals("done") && qol2.equals("done") && qol3.equals("done") && qol4.equals("done") && qol5.equals("done") && qol7.equals("done") && qol8.equals("done") && qol9.equals("done") && qol10.equals("done")*/) {
            uploadData(FileManager.getInfoFile(this));

            binding.btnConfirm.setVisibility(View.GONE);
            initTab();
        }
    }



    @SuppressLint("SetTextI18n")
    private void displayFSMC(){
        if(!Patient.getPatient().getDiagnosis().equals("Stroke")){
            if(!fsmc.equals("null")){
                binding.txtQuestionFatigueFSMC.setText("Question answered : "+ (Integer.parseInt(questionAnsFCSM) -1) +"/ 20 - ("+skipped_question_fsmc+" skipped)" );

                if(fsmc.equals("done")){
                    binding.imgDoneFsmc.setVisibility(View.VISIBLE);
                    if(scoreFSMC.equals("0")){
                        binding.txtQuestionFatigueFSMC.setVisibility(View.GONE);
                        binding.txtQuestionnaireSkippedFsmc.setVisibility(View.VISIBLE);
                    }else{
                        binding.rdBtnFSMC.setClickable(false);
                        createResultFSMCCSV();
                        uploadData(FileManager.getFSMCResultFile(this));
                        binding.txtQuestionFatigueFSMC.setVisibility(View.VISIBLE);
                        binding.txtRawValueFsmc.setText("Total score : " + scoreFSMC  +"/ 80");
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
                    uploadData(FileManager.getSleepResultFile(this));
                    binding.txtQuestionFatigueSleep.setVisibility(View.VISIBLE);
                    binding.txtRawValueSleep.setText("Total score : " + score_sleep  +"/ 24");
                    binding.txtRawValueSleep.setVisibility(View.VISIBLE);
                }

            }
        }
    }

    private void createResultSleepCSV(){
        Patient patient = Patient.getPatient();
        String patientID = patient.getPatientId();
        String caseID = patient.getCaseId();
        String date = patient.getDate();

        if(!FileManager.isSleepResultFileExist(this)){
            writeCSV.createAndWriteSleepResult(FileManager.getSleepResultFile(this).getAbsolutePath(), patientID, caseID, date,score_sleep);
        }
    }

    private void createResultFSMCCSV(){
        Patient patient = Patient.getPatient();
        String patientID = patient.getPatientId();
        String caseID = patient.getCaseId();
        String date = patient.getDate();
        Type type = MyApplication.type;

        if(!FileManager.isFSMCResultFileExist(this)){
            writeCSV.createAndWriteFSMCResult(FileManager.getFSMCResultFile(this).getAbsolutePath(), patientID, caseID, date, scoreFSMC);
        }
    }




    private void initTab(){
        binding.tableLayout.setVisibility(View.VISIBLE);
        binding.btnConfirm.setVisibility(View.GONE);

        binding.cellScoreSleep.setText(score_sleep);
        displayResult("sleep");
        binding.cellFSMCScore.setText(scoreFSMC);
        displayResult("fsmc");
    }

    private void displayResult(String categorie){

        String text_interpretations;
        if(categorie.equals("sleep")){
            if(score_sleep.equals("0") && !questionAnsSleep.equals("9")){
                text_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellResultSleep.setColorSections(upperlimit, colors);
                binding.cellResultSleep.setUserScore(2.75f);
                binding.cellResultSleep.setUserText(text_interpretations);
                Log.d("TEST",  "HRER");
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
                Log.d("TEST", "HERE");

                float user_score = Float.parseFloat(score_sleep);
                float user_score_rescale = binding.cellResultSleep.rescaleValue(user_score,min,redEnd, 40.0f);

                binding.cellResultSleep.setColorSections(upperlimit, colors);
                binding.cellResultSleep.setUserScore(user_score_rescale);
                binding.cellResultSleep.setUserText(text_interpretations);
            }
        }

        if(categorie.equals("fsmc")){
            if (scoreFSMC.equals("0") && !questionAnsFCSM.equals("21")) {
                String txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellFSMCResult.setColorSections(upperlimit, colors);
                binding.cellFSMCResult.setUserScore(2.75f);
                binding.cellFSMCResult.setUserText(txt_interpretations);
                Log.d("TEST",  "HRER");
            }else{
                String txt_interpretations  ="";
                if(Integer.parseInt(scoreFSMC)<43){
                    txt_interpretations = "Good";
                } else if (Integer.parseInt(scoreFSMC)<53) {
                    txt_interpretations = "Leichte Fatigue";
                } else if (Integer.parseInt(scoreFSMC)<63) {
                    txt_interpretations = "Mittelgradige  Fatigue";
                } else if (Integer.parseInt(scoreFSMC)>62) {
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

                Log.d("TEST", "HERE");
                float user_score = Float.parseFloat(scoreFSMC);
                float user_score_rescale = binding.cellFSMCResult.rescaleValue(user_score,min,redEnd, 40.0f);

                binding.cellFSMCResult.setColorSections(upperlimit, colors);
                binding.cellFSMCResult.setUserScore(user_score_rescale);
                binding.cellFSMCResult.setUserText(txt_interpretations);
            }
        }
    }

    private void listenRadioGroup() {
        binding.radioGroup2.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rdBtnESSFatigue) {
                questionnaire = "sleep";

                if (Integer.parseInt(skipped_question_sleep) > 3)
                    redo_questionnaire = true;

            } else if (checkedId == R.id.rdBtnFSMC) {
                questionnaire = "fsmc";
                if (Integer.parseInt(skipped_question_fsmc) > 4)
                    redo_questionnaire = true;
            }
        });
    }

    private void listenBtnConfirm(){
        binding.btnConfirm.setOnClickListener(v -> {
            if(questionnaire.equals("sleep") || questionnaire.equals("ess_depression") || questionnaire.equals("ess_anxiety")){
                navigateToNextActivity(SleepActivity.class);
            } else if (questionnaire.equals("fsmc")) {
                navigateToNextActivity(IntroductionFSMCActivity.class);
            }
        });

    }

    private void uploadData(File file){
//        DataTransfer dataTransfer = new DataTransfer(this);
//        dataTransfer.uploadFile(file);
    }

    @Override
    public void prepareIntent(Intent intent) {
        super.prepareIntent(intent);
        intent.putExtra("questionnaire", questionnaire);
    }
}