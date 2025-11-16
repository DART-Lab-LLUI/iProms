package fr.thomas.menard.iproms.Views;

import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import fr.thomas.menard.iproms.App.MyApplication;
import fr.thomas.menard.iproms.Enum.QuestionnaireStatus;
import fr.thomas.menard.iproms.Enum.QuestionnaireType;
import fr.thomas.menard.iproms.FileWriter.AbstractQuestionnaire;
import fr.thomas.menard.iproms.FileWriter.BDIQuestionnaire;
import fr.thomas.menard.iproms.FileWriter.DepressionAnxietyQuestionnaire;
import fr.thomas.menard.iproms.FileWriter.FatigueQuestionnaire;
import fr.thomas.menard.iproms.FileWriter.PromisQuestionnaire;
import fr.thomas.menard.iproms.Model.Questionnaires;
import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.DataTransfer;
import fr.thomas.menard.iproms.Utils.LocaleHelper;
import fr.thomas.menard.iproms.databinding.ActivityMainBinding;


public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private QuestionnaireType questionnaire;
    private boolean redo_questionnaire;
    private Map<QuestionnaireType, AbstractQuestionnaire> questionnaireMap;
    private QuestionnaireType[] requiredQuestionnaires;

    @Override
    public void init() {
        LocaleHelper.setLocale(this, MyApplication.language.getLanguage());
        questionnaireMap = Questionnaires.getAll();
        requiredQuestionnaires = new QuestionnaireType[]{
                QuestionnaireType.FATIGUE,
                QuestionnaireType.DEPRESSIONANXIETY,
                QuestionnaireType.PROMIS,
                QuestionnaireType.BDI
        };

        checkQuestionnaireDone();
        displayText();
        checkFatigueI();
        checkDepression();
        checkBDI();
        checkPromis();
    }

    protected void onResume() {
        super.onResume();
        checkQuestionnaireDone();
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

    // header info
    @SuppressLint("SetTextI18n")
    private void displayText(){
        binding.txtPatientID.setText(patientInfo.getPatientId());
        binding.txtPatientID.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        binding.txtDiagnosis.setText(patientInfo.getDiagnosis());
        binding.txtDiagnosis.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
    }

    // fatigue button / text
    private void checkFatigueI() {
        FatigueQuestionnaire fatigue = (FatigueQuestionnaire) Questionnaires.get(this, QuestionnaireType.FATIGUE);

        if (fatigue == null || fatigue.getProgressStatus() == QuestionnaireStatus.NOT_STARTED) return;

        int questionAnsFatigue = fatigue.getAnsweredQues();
        int avgScoreFatigue = fatigue.getScore();
        int skippedQues = fatigue.getSkippedQues();
        boolean doneFatigue = (fatigue.getProgressStatus() == QuestionnaireStatus.COMPLETED);

        binding.nbrQuestionAnsweredFatigue.setText(String.valueOf(questionAnsFatigue));

        binding.imgFatigueDone.setImageResource(doneFatigue ? R.drawable.questionnaire_done : R.drawable.started);
        binding.imgFatigueDone.setVisibility(View.VISIBLE);
        binding.txtQuestionsSkippedFatigue.setVisibility(doneFatigue ? View.VISIBLE : View.INVISIBLE);

        binding.txtQuestionsSkippedFatigue.setText("(" + skippedQues + " " + getString(R.string.x_skipped));

        if (!doneFatigue) return;

        // done case
        if (avgScoreFatigue != 0) {
            double mean = (double) avgScoreFatigue / (questionAnsFatigue);
            long rounded = Math.round(mean);

            binding.linearAvgScoreFatigue.setVisibility(View.VISIBLE);
            binding.scoreAvgFatigue.setText(String.valueOf(rounded));
        } else {
            binding.linearFatigue.setVisibility(View.GONE);
            binding.txtQuestionnaireSkipped.setVisibility(View.VISIBLE);
        }
    }

    // depression button / text
    private void checkDepression() {
        DepressionAnxietyQuestionnaire depression = (DepressionAnxietyQuestionnaire) Questionnaires.get(this, QuestionnaireType.DEPRESSIONANXIETY);

        if (depression == null || depression.getProgressStatus() == QuestionnaireStatus.NOT_STARTED) return;

        int questionAnsDep = depression.getAnxietyQuestionAnswered() + depression.getDepressionQuestionAnswered();
        int avgScoreDep = depression.getDepressionScore();
        int avgScoreAnx = depression.getAnxietyScore();
        int skippedDepQues = depression.getDepressionSkippedAnswered();
        int skippedAnxQues = depression.getAnxietySkippedAnswered();
        int totalSkipped = skippedDepQues + skippedAnxQues;
        boolean doneDep = (depression.getProgressStatus() == QuestionnaireStatus.COMPLETED);

        binding.nbrQuestionAnsweredDep.setText(String.valueOf(questionAnsDep));
        binding.txtQuestionsSkippedDepAnx.setText("(" + totalSkipped + " " + getString(R.string.x_skipped));
        binding.txtQuestionsSkippedDep.setText("(" + skippedDepQues + " " + getString(R.string.x_skipped));
        binding.txtQuestionsSkippedAnx.setText("(" + skippedAnxQues + " " + getString(R.string.x_skipped));

        binding.imgDepressionDone.setImageResource(doneDep ? R.drawable.questionnaire_done : R.drawable.started);
        binding.imgDepressionDone.setVisibility(View.VISIBLE);
        binding.txtQuestionsSkippedDepAnx.setVisibility(doneDep ? View.VISIBLE : View.INVISIBLE);

        if (!doneDep) return;

        // done case
        boolean hasScores = (avgScoreDep != 0) && (avgScoreAnx != 0);
        boolean fullyAnswered = (questionAnsDep == depression.getQuestionIdsLength());

        if (hasScores || fullyAnswered) {
            binding.linearAvgScoreDep.setVisibility(View.VISIBLE);
            binding.linearAvgScoreAnx.setVisibility(View.VISIBLE);
            binding.txtQuestionsSkippedDep.setVisibility(View.VISIBLE);
            binding.txtQuestionsSkippedAnx.setVisibility(View.VISIBLE);
            binding.scoreAvgDep.setText(String.valueOf(avgScoreDep));
            binding.scoreAvgAnx.setText(String.valueOf(avgScoreAnx));
        } else {
            binding.linearDep.setVisibility(View.GONE);
            binding.txtQuestionnaireSkippedDep.setVisibility(View.VISIBLE);
        }
    }

    // bdi button / text
    private void checkBDI(){
        BDIQuestionnaire bdi = (BDIQuestionnaire) Questionnaires.get(this, QuestionnaireType.BDI);

        if (bdi == null || bdi.getProgressStatus() == QuestionnaireStatus.NOT_STARTED) return;

        int questionAnsBDI = bdi.getQuestionAnswered();
        int scoreBdi = bdi.getTotalScore();
        int skippedQuesBdi = bdi.getQuestionSkipped();
        boolean doneBdi = (bdi.getProgressStatus() == QuestionnaireStatus.COMPLETED);

        binding.nbrQuestionAnsweredBDI.setText(String.valueOf(questionAnsBDI));

        binding.imgBDIDone.setImageResource(doneBdi ? R.drawable.questionnaire_done : R.drawable.started);
        binding.imgBDIDone.setVisibility(View.VISIBLE);
        binding.txtQuestionsSkippedBDI.setVisibility(doneBdi ? View.VISIBLE : View.INVISIBLE);
        binding.txtQuestionsSkippedBDI.setText("(" + skippedQuesBdi + " " + getString(R.string.x_skipped));

        if (!doneBdi) return;

        // done case
        if ((scoreBdi != 0) || (questionAnsBDI == bdi.getQuestionIdsLength())) {
            binding.linearScoreBDI.setVisibility(View.VISIBLE);
            binding.txtQuestionsSkippedBDI.setVisibility(View.VISIBLE);
            binding.txtRawValueBDI.setText(String.valueOf(scoreBdi));
        } else {
            binding.txtQuestionBDI.setVisibility(View.GONE);
            binding.txtQuestionnaireSkippedBdi.setVisibility(View.VISIBLE);
        }
    }

    // PROMIS button / text
    private void checkPromis(){
        PromisQuestionnaire promis = (PromisQuestionnaire) Questionnaires.get(this, QuestionnaireType.PROMIS);

        if (promis == null || promis.getProgressStatus() == QuestionnaireStatus.NOT_STARTED) return;

        int questionAnsPROMIS = promis.getQuestionAnswered();
        int avgScorePromisMental = promis.getMentalRawScore();
        int avgScorePromisPhysical = promis.getPhysicalRawScore();
        int skippedQuesPromis = promis.getQuestionSkipped();
        boolean donePromis = (promis.getProgressStatus() == QuestionnaireStatus.COMPLETED);

        binding.nbrQuestionAnsweredPROMIS.setText(String.valueOf(questionAnsPROMIS));

        binding.imgPromisDone.setImageResource(donePromis ? R.drawable.questionnaire_done : R.drawable.started);
        binding.imgPromisDone.setVisibility(View.VISIBLE);
        binding.txtQuestionsSkippedPROMIS.setVisibility(donePromis ? View.VISIBLE : View.INVISIBLE);
        binding.txtQuestionsSkippedPROMIS.setText("(" + skippedQuesPromis + " " + getString(R.string.x_skipped));

        if (!donePromis) return;

        // done hase
        boolean hasScores = (avgScorePromisMental != 0) && (avgScorePromisPhysical != 0);
        boolean fullyAnswered = (questionAnsPROMIS == promis.getQuestionIdsLength());

        if (hasScores || fullyAnswered) {
            binding.linearScoreMentalPROMIS.setVisibility(View.VISIBLE);
            binding.linearScorePhysicalPROMIS.setVisibility(View.VISIBLE);
            binding.txtQuestionsSkippedPROMIS.setVisibility(View.VISIBLE);
            binding.txtRawValuePhysicalPROMIS.setText(String.valueOf(avgScorePromisPhysical));
            binding.txtRawValueMentalPROMIS.setText(String.valueOf(avgScorePromisMental));
        } else {
            binding.linearPROMIS.setVisibility(View.GONE);
            binding.txtQuestionnaireSkippedPROMIS.setVisibility(View.VISIBLE);
        }
    }

    private void listenRadioGroup(){
        binding.radioGroup2.setOnCheckedChangeListener((group, checkedId) -> {
            redo_questionnaire = false;
            if (checkedId == R.id.rdBtnFatigue) {
                questionnaire = QuestionnaireType.FATIGUE;
                FatigueQuestionnaire ques = (FatigueQuestionnaire) questionnaireMap.get(questionnaire);
                if(ques != null && ques.getSkippedQues()>3)
                    redo_questionnaire=true;
            }else if (checkedId == R.id.rdBtnDA) {
                questionnaire = QuestionnaireType.DEPRESSIONANXIETY;
                DepressionAnxietyQuestionnaire ques = (DepressionAnxietyQuestionnaire) questionnaireMap.get(questionnaire);
                if(ques != null && (ques.getDepressionSkippedAnswered()>3 || ques.getAnxietySkippedAnswered() > 3))
                    redo_questionnaire=true;
            }else if (checkedId == R.id.rdBtnPROMIS) {
                questionnaire = QuestionnaireType.PROMIS;
                PromisQuestionnaire ques = (PromisQuestionnaire) questionnaireMap.get(questionnaire);
                if(ques != null && ques.getQuestionSkipped() >3)
                    redo_questionnaire=true;
            }
            else if (checkedId == R.id.rdBtnBDI) {
                questionnaire =  QuestionnaireType.BDI;
                BDIQuestionnaire ques = (BDIQuestionnaire) questionnaireMap.get(questionnaire);
                if(ques != null && ques.getQuestionSkipped() >3)
                    redo_questionnaire=true;
            }
        });

        binding.radioGroup2Optionnal.setOnCheckedChangeListener((group, checkedId) -> {
//            if (checkedId == R.id.rdBtnWEIMuS) {
//                questionnaire = "WEIMuS";
//            } else if (checkedId == R.id.rdBtnESS) {
//                questionnaire = "ESS";
//            } else {
//                questionnaire = "";
//            }
        });
    }

    private void listenBtnStart(){
        binding.btnConfirm.setOnClickListener(v -> {
            if(questionnaire == null) {
                Toast.makeText(this, "Please select a questionnaire", Toast.LENGTH_SHORT).show();
            }

            boolean quesIsDone = Questionnaires.get(this, questionnaire).getProgressStatus() == QuestionnaireStatus.COMPLETED;
            if(quesIsDone){
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.redo_questionaire, questionnaire))
                        .setMessage(getString(R.string.redo_questionaire_message))
                        .setPositiveButton(getString(R.string.redo), (dialog, which) -> {
                            redo_questionnaire = true;
                            startActivity();
                        })
                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .setCancelable(true)
                        .show();
            } else {
                startActivity();
            }
        });
    }

    private void listenBtnResult(){
        binding.btnResult.setOnClickListener(v -> {
            navigateToNextActivityWithoutFinish(SummaryActivity.class);
        });
    }

    private void startActivity(){
        switch (questionnaire) {
            case FATIGUE:
                navigateToNextActivityWithoutFinish(FatigueQuestionnaireView.class);
                break;
            case DEPRESSIONANXIETY:
                navigateToNextActivityWithoutFinish(DepressionAnxietyView.class);
                break;
            case PROMIS:
                navigateToNextActivityWithoutFinish(PromisView.class);
                break;
            case BDI:
                navigateToNextActivityWithoutFinish(BDIView.class);
                break;
            default:
                Toast.makeText(this, "Please select a questionnaire", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkQuestionnaireDone() {
        List<QuestionnaireType> done = Questionnaires.getCompletedQuestionnaires(this);
        if (new HashSet<>(done).containsAll(Arrays.asList(requiredQuestionnaires))) {
            binding.btnConfirm.setVisibility(View.GONE);
            binding.btnResult.setVisibility(View.VISIBLE);

            for (QuestionnaireType type : requiredQuestionnaires) {
                AbstractQuestionnaire questionnaire = Questionnaires.get(this, type);
                File file = questionnaire.getQuestionnaireFile(this);

                if (file != null && file.exists()) {
                    boolean success = uploadData(file);
                    if (!success) {
                        break;
                    }
                }
            }
        }
    }

    private boolean uploadData(File file){
        return new DataTransfer(this).uploadFile(file);
    }

    @Override
    public void prepareIntent(Intent intent) {
        intent.putExtra("redo_questionnaire", redo_questionnaire);
    }
}