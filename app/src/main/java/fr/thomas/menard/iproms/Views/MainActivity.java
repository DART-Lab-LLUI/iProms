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

import fr.thomas.menard.iproms.App.MyApplication;
import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.DataTransfer;
import fr.thomas.menard.iproms.Utils.DebugLogger;
import fr.thomas.menard.iproms.Utils.FileManager;
import fr.thomas.menard.iproms.Utils.LocaleHelper;
import fr.thomas.menard.iproms.Utils.ReadCSV;
import fr.thomas.menard.iproms.databinding.ActivityMainBinding;


public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private Context context;
    private String questionnaire;
    private boolean redo_questionnaire;
    private boolean restart_fatigue = false;
    private boolean restart_dep = false;
    private boolean restart_promis = false;
    private boolean restart_bdi = false;

    private int safeParse(String safe) {
        if (safe == null || safe.isEmpty()) return 0;
        try {
            return Integer.parseInt(safe);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void init() {
        // load all CSV backed fields into InfoFile.*
        ReadCSV.retrieveInfos(this);

        // apply localization, then refresh UI
        LocaleHelper.setLocale(this, MyApplication.language.getLanguage());
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

    // show N skipped if both answered and skpped counts non empty
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

    // header info
    @SuppressLint("SetTextI18n")
    private void displayText(){
        binding.txtPatientID.setText(patientInfo.getPatientId());
        binding.txtPatientID.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        binding.txtDiagnosis.setText(patientInfo.getDiagnosis());
        binding.txtDiagnosis.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
    }

    // fatigue button / text
    private void checkFatigueI(){
        if (!fatigue.isEmpty()) {
            restart_fatigue = true;
            binding.nbrQuestionAnsweredFatigue.setText(questionAnsFatigue);
            binding.imgFatigueDone.setImageResource(R.drawable.started);
            binding.imgFatigueDone.setVisibility(View.VISIBLE);
            binding.txtQuestionsSkippedFatigue.setVisibility(View.INVISIBLE);

            if("done".equals(fatigue)){
                binding.imgFatigueDone.setVisibility(View.VISIBLE);
                binding.txtQuestionsSkippedFatigue.setVisibility(View.VISIBLE);

                if(!avg_score_fatigue.equals("0")){
                    // binding.imgFatigueDone.setVisibility(View.VISIBLE);

                    // show average score
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

    // depression button / text
    private void checkDepression() {
        if(!depression.isEmpty()){
            restart_dep = true;
            binding.nbrQuestionAnsweredDep.setText(questionAnsDep);
            binding.imgDepressionDone.setImageResource(R.drawable.started);
            binding.imgDepressionDone.setVisibility(View.VISIBLE);
            binding.txtQuestionsSkippedDepAnx.setVisibility(View.INVISIBLE);

            if ("done".equals(depression)) {
                binding.imgDepressionDone.setVisibility(View.VISIBLE);
                binding.txtQuestionsSkippedDepAnx.setVisibility(View.INVISIBLE);

                if(!avg_score_depression.equals("0") && !avg_score_anxiety.equals("0") || questionAnsDep.equals("15")){
                    binding.linearAvgScoreDep.setVisibility(View.VISIBLE);
                    binding.linearAvgScoreAnx.setVisibility(View.VISIBLE);
                    binding.txtQuestionsSkippedAnx.setVisibility(View.VISIBLE);
                    binding.txtQuestionsSkippedDep.setVisibility(View.VISIBLE);
                    binding.scoreAvgDep.setText(avg_score_depression);
                    binding.scoreAvgAnx.setText(avg_score_anxiety);
                    binding.rdBtnDA.setClickable(Integer.parseInt(lastQuestionDep) >= 4 || Integer.parseInt(skipped_question_anx) >= 4);
                }else{
                    binding.linearDep.setVisibility(View.GONE);
                    binding.txtQuestionnaireSkippedDep.setVisibility(View.VISIBLE);
                    // binding.imgDepressionDone.setImageResource(R.drawable.questionnaire_done);
                }
            }
        }
    }

    // bdi button / text
    private void checkBDI(){
        if(!bdi.isEmpty()){
            restart_bdi = true;
            binding.txtQuestionBDI.setText("Question answered : "+ questionAnsBDI +"/ 21  - ("+skipped_question_bdi+" skipped)" );

            if("done".equals(bdi)){
                binding.imgBDIDone.setVisibility(View.VISIBLE);

                if(score_bdi.equals("0") && !questionAnsBDI.equals("21")) {
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

    // PROMIS button / text
    private void checkPromis(){
        if(!promis.isEmpty()){
            restart_promis = true;
            binding.txtQuestionPROMIS.setText("Question answered : " + questionAnsPROMIS + "/ 10  - (" + skipped_question_promis + " skipped)" );

            if("done".equals(promis)) {
                binding.imgPromisDone.setVisibility(View.VISIBLE);

                if(avg_score_PROMIS_mental.equals("0")){
                    binding.txtQuestionPROMIS.setVisibility(View.GONE);
                }else{
                    binding.rdBtnPROMIS.setClickable(false);
                    binding.txtRawValueMentalPROMIS.setText("Total score : " + avg_score_PROMIS_mental  +"/ 20");
                    binding.txtRawValuePhysicalPROMIS.setText("Total score : " + avg_score_PROMIS_physical  +"/ 20");
                    binding.txtRawValuePhysicalPROMIS.setVisibility(View.VISIBLE);
                    binding.txtRawValueMentalPROMIS.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void listenRadioGroup(){
        binding.radioGroup2.setOnCheckedChangeListener((group, checkedId) -> {
            redo_questionnaire = false;
            if (checkedId == R.id.rdBtnFatigue) {
                questionnaire = "fatigue";
                if(safeParse(lastQuestionFatigue)>3)
                    redo_questionnaire=true;
            }else if (checkedId == R.id.rdBtnDA) {
                questionnaire = "depression";
                if(safeParse(lastQuestionDep)>3 || safeParse(skipped_question_anx) > 3)
                    redo_questionnaire=true;
            }else if (checkedId == R.id.rdBtnPROMIS) {
                questionnaire = "promis";
                if(safeParse(skipped_question_promis)>3)
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

    private void listenBtnStart(){
        binding.btnConfirm.setOnClickListener(v -> {
            if(questionnaire == null) {
                Toast.makeText(this, "Please select a questionnaire", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(questionnaire);
            }
        });
    }

    private void listenBtnResult(){
        binding.btnResult.setOnClickListener(v -> {
            navigateToNextActivityWithoutFinish(SummaryActivity.class);
        });
    }

    private void startActivity(String questionnaire){
        switch (questionnaire) {
            case  "fatigue":
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
                Toast.makeText(this, "Please select a questionnaire", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean getRestartFlag(String questionnaire){
        switch (questionnaire) {
            case "fatigue": return restart_fatigue;
            case "depression": return restart_dep;
            case "promis": return restart_promis;
            case "bdi": return restart_bdi;
            default: return false;
        }
    }

    private void checkQuestionnaireDone() {
        if ("done".equals(fatigue) && "done".equals(depression) && "done".equals(bdi) && "done".equals(promis)) {
            uploadData(FileManager.getInfoFile(this));
            binding.btnConfirm.setVisibility(View.GONE);
            binding.btnResult.setVisibility(View.VISIBLE);
        }
    }

    private void uploadData(File file){
        new DataTransfer(this).uploadFile(file);
    }

    // pasts redo and restart into next activity
    @Override
    public void prepareIntent(Intent intent) {

        intent.putExtra("redo_questionnaire", redo_questionnaire);
        intent.putExtra("restart", getRestartFlag(questionnaire));
    }
}