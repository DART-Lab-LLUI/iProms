package fr.thomas.menard.iproms.Views;

import static fr.thomas.menard.iproms.Model.InfoFile.*;

import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import fr.thomas.menard.iproms.Model.MyApplication;
import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.DataTransfer;
import fr.thomas.menard.iproms.Utils.FileManager;
import fr.thomas.menard.iproms.Utils.LocaleHelper;
import fr.thomas.menard.iproms.Utils.ReadCSV;
import fr.thomas.menard.iproms.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private String questionnaire;
    private Context context;

    private boolean redo_questionnaire;
    private boolean restart_fatigue =false;
    private boolean restart_dep = false;


    private void checkQuestionnaireDone() {
        if (fatigue.equals("done") && depression.equals("done") && bdi.equals("done") && promis.equals("done")) {
            uploadData(FileManager.getInfoFile(this));

            binding.btnConfirm.setVisibility(View.GONE);
            binding.btnResult.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void skippedQuestion(){
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
        binding.txtPatientID.setText(patientInfo.getPatientId());
        binding.txtPatientID.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        binding.txtDiagnosis.setText(patientInfo.getDiagnosis());
        binding.txtDiagnosis.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
    }

    @SuppressLint("SetTextI18n")
    private void checkBDI(){
        if(!bdi.equals("null")){
            binding.txtQuestionBDI.setText("Question answered : "+ questionAnsBDI +"/ 22  - ("+skipped_question_bdi+" skipped)" );

            if(bdi.equals("done")){
                binding.imgBDIDone.setVisibility(View.VISIBLE);
                if(score_bdi.equals("0") && !questionAnsBDI.equals("22")){
                    binding.txtQuestionnaireSkippedBdi.setVisibility(View.VISIBLE);
                    binding.txtQuestionBDI.setVisibility(View.GONE);
                }else{
                    binding.rdBtnBDI.setClickable(false);
                    uploadData(FileManager.getResultBDIFile(this));
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
                if(avg_score_PROMIS_mental.equals("0")){
                    binding.txtQuestionPROMIS.setVisibility(View.GONE);
                }else{
                    binding.rdBtnPROMIS.setClickable(false);
                    uploadData(FileManager.getResultBDIFile(this));
                    binding.txtQuestionPROMIS.setVisibility(View.VISIBLE);
                    binding.txtRawValueMentalPROMIS.setText("Total score : " + avg_score_PROMIS_mental  +"/ 20");
                    binding.txtRawValueMentalPROMIS.setVisibility(View.VISIBLE);
                    binding.txtRawValuePhysicalPROMIS.setText("Total score : " + avg_score_PROMIS_physical  +"/ 20");
                    binding.txtRawValuePhysicalPROMIS.setVisibility(View.VISIBLE);
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
                if(!avg_score_depression.equals("0") && !avg_score_anxiety.equals("0") || questionAnsDep.equals("15")){
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



    private void uploadData(File file){
//        DataTransfer dataTransfer = new DataTransfer(this);
//        dataTransfer.uploadFile(file);
    }

    private void listenRadioGroup(){
        binding.radioGroup2.setOnCheckedChangeListener((group, checkedId) -> {
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
        });

        binding.radioGroup2Optionnal.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rdBtnWEIMuS) {
                questionnaire = "WEIMuS";
            } else if (checkedId == R.id.rdBtnESS) {
                questionnaire = "ESS";
            } else {
                questionnaire = "";
            }
        });
    }

    private void startActivity(String questionnaire){
        switch (questionnaire) {
            case "fatigue":
                navigateToNextActivityWithoutFinish(FatigueQuestionnaire.class);
                break;
            case "depression":
                navigateToNextActivityWithoutFinish(DepressionAnxietyActivity.class);
                break;
            case "promis":
                navigateToNextActivityWithoutFinish(PromisActivity.class);
                break;
            case "bdi":
                navigateToNextActivityWithoutFinish(BDI_Activity.class);
                break;
            default:
                if (questionnaire.startsWith("qol")) {
                    navigateToNextActivityWithoutFinish(QualityofLifeActivity.class);
                } else {
                    Toast.makeText(context, "Please select a questionnaire", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void listenBtnStart(){
        binding.btnConfirm.setOnClickListener(v -> {
            startActivity(questionnaire);

        });
    }

    private void listenBtnResult(){
        binding.btnResult.setOnClickListener(v -> {
            navigateToNextActivity(SummaryActivity.class);
        });
    }

    @Override
    public void init() {
        ReadCSV.retrieveInfos(this);
        context = LocaleHelper.setLocale(this, MyApplication.language.getLanguage());
        checkQuestionnaireDone();
        skippedQuestion();
        displayText();
        checkFatigueI();
        checkDepression();
        checkBDI();
        checkPromis();
    }

    @Override
    public void listenBtn() {
        listenBtnStart();
        listenRadioGroup();
        listenBtnResult();
    }

    @Override
    public void setBinding() {
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    @Override
    public void prepareIntent(Intent intent) {

        if(questionnaire == null){
            return;
        }

        switch (questionnaire){
            case "fatigue":
                intent.putExtra("redo_questionnaire", redo_questionnaire);
                intent.putExtra("restard", restart_fatigue);
                break;
            case "depression":
            case "promis":
            case "bdi":
                intent.putExtra("redo_questionnaire", redo_questionnaire);
                intent.putExtra("restard", restart_dep);
                break;

            case "qol1": {
                intent.putExtra("redo_questionnaire", redo_questionnaire);
                intent.putExtra("num_question", 1);
                intent.putExtra("qol", "qol1");
                break;
            }
            case "qol2": {
                intent.putExtra("redo_questionnaire", redo_questionnaire);
                intent.putExtra("num_question", 9);
                intent.putExtra("qol", "qol2");
                break;
            }
            case "qol3": {
                intent.putExtra("redo_questionnaire", redo_questionnaire);
                intent.putExtra("num_question", 17);
                intent.putExtra("qol", "qol3");
                break;
            }
            case "qol4": {
                intent.putExtra("redo_questionnaire", redo_questionnaire);
                intent.putExtra("num_question", 25);
                intent.putExtra("qol", "qol4");
                break;
            }
            case "qol5": {
                intent.putExtra("redo_questionnaire", redo_questionnaire);
                intent.putExtra("num_question", 33);
                intent.putExtra("qol", "qol5");
                break;
            }
            case "qol6": {
                intent.putExtra("redo_questionnaire", redo_questionnaire);
                intent.putExtra("num_question", 41);
                intent.putExtra("qol", "qol6");
                break;
            }
            case "qol7": {
                intent.putExtra("redo_questionnaire", redo_questionnaire);
                intent.putExtra("num_question", 49);
                intent.putExtra("qol", "qol7");
                break;
            }
            case "qol8": {
                intent.putExtra("redo_questionnaire", redo_questionnaire);
                intent.putExtra("num_question", 58);
                intent.putExtra("qol", "qol8");
                break;
            }
            case "qol9": {
                intent.putExtra("redo_questionnaire", redo_questionnaire);
                intent.putExtra("num_question", 66);
                intent.putExtra("qol", "qol9");
                break;
            }
            case "qol10": {
                intent.putExtra("redo_questionnaire", redo_questionnaire);
                intent.putExtra("num_question", 74);
                intent.putExtra("qol", "qol10");
                break;
            }
            default:
                Toast.makeText(context, "Please select a questionnaire", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}