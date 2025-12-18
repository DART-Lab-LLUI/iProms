package com.llui.iproms.Views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.io.File;

import com.llui.iproms.Enum.QuestionnaireStatus;
import com.llui.iproms.Questionnaires.SleepQuestionnaire;
import com.llui.iproms.Enum.QuestionnaireType;
import com.llui.iproms.Questionnaires.FSMCQuestionnaire;
import com.llui.iproms.Model.Patient;
import com.llui.iproms.Model.Questionnaires;
import com.llui.iproms.R;
import com.llui.iproms.Utils.DataTransfer;
import com.llui.iproms.databinding.ActivityOptionnalQuestionnairesBinding;

public class OptionalQuestionnairesActivity extends BaseActivity {

    private ActivityOptionnalQuestionnairesBinding binding;
    private String questionnaire;
    private boolean redo_questionnaire;
    private SleepQuestionnaire sleepQuestionnaire;
    private FSMCQuestionnaire fsmcQuestionnaire;
    private boolean isStrokePatient;

    @Override
    public void init() {
        isStrokePatient = Patient.getPatient().getDiagnosis().equals("Stroke");

        // --- Always load sleep questionnaire ---
        sleepQuestionnaire = (SleepQuestionnaire) Questionnaires.get(this, QuestionnaireType.SLEEP);

        // --- Only load FSMC if not a stroke patient ---
        if (isStrokePatient) {
            binding.rdBtnFSMC.setVisibility(View.GONE);
            binding.txtQuestionFatigueFSMC.setVisibility(View.GONE);
        } else {
            fsmcQuestionnaire = (FSMCQuestionnaire) Questionnaires.get(this, QuestionnaireType.FSMC);
        }

        // --- Display questionnaires ---
        displaySleep();
        if (!isStrokePatient) displayFSMC();

        // --- If both done, show summary table ---
        checkQuestionnaireDone();
    }

    @Override
    public void listenBtn() {
        listenRadioGroup();
        listenBtnConfirm();
    }

    @Override
    public void setBinding() {
        binding = ActivityOptionnalQuestionnairesBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    private void checkQuestionnaireDone() {
        boolean sleepDone = sleepQuestionnaire.getProgressStatus() == QuestionnaireStatus.COMPLETED;
        boolean fsmcDone = !isStrokePatient &&
                fsmcQuestionnaire != null &&
                fsmcQuestionnaire.getProgressStatus() == QuestionnaireStatus.COMPLETED;

        if (isStrokePatient && sleepDone) {
            binding.btnConfirm.setVisibility(View.GONE);
            initTab();
            uploadData(sleepQuestionnaire.getQuestionnaireFile(this));
        } else if (sleepDone && fsmcDone) {
            binding.btnConfirm.setVisibility(View.GONE);
            initTab();
            uploadData(sleepQuestionnaire.getQuestionnaireFile(this));
            uploadData(fsmcQuestionnaire.getQuestionnaireFile(this));
        }
    }

    @SuppressLint("SetTextI18n")
    private void displayFSMC() {
        if (fsmcQuestionnaire == null || fsmcQuestionnaire.getProgressStatus() == QuestionnaireStatus.NOT_STARTED)
            return;

        int answered = fsmcQuestionnaire.getAnsweredQues();
        int skipped = fsmcQuestionnaire.getSkippedQues();
        int score = fsmcQuestionnaire.getScore();
        boolean done = fsmcQuestionnaire.getProgressStatus() == QuestionnaireStatus.COMPLETED;

        binding.txtQuestionFatigueFSMC.setText(getString(R.string.questions_answered)
                + answered + " / 20 - (" + skipped + " " + getString(R.string.x_skipped) + ")");

        if (done) {
            binding.imgDoneFsmc.setVisibility(View.VISIBLE);
            if (score == 0) {
                binding.txtQuestionFatigueFSMC.setVisibility(View.GONE);
                binding.txtQuestionnaireSkippedFsmc.setVisibility(View.VISIBLE);
            } else {
                binding.rdBtnFSMC.setEnabled(false);
                binding.txtQuestionFatigueFSMC.setVisibility(View.VISIBLE);
                binding.txtRawValueFsmc.setText(getString(R.string.total_score) + score + " / 63");
                binding.txtRawValueFsmc.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void displaySleep() {
        if (sleepQuestionnaire == null || sleepQuestionnaire.getProgressStatus() == QuestionnaireStatus.NOT_STARTED)
            return;

        int answered = sleepQuestionnaire.getAnsweredQues();
        int skipped = sleepQuestionnaire.getSkippedQues();
        int score = sleepQuestionnaire.getScore();
        boolean done = sleepQuestionnaire.getProgressStatus() == QuestionnaireStatus.COMPLETED;

        binding.txtQuestionFatigueSleep.setText(getString(R.string.questions_answered)
                + answered + " / 8 - (" + skipped + " " + getString(R.string.x_skipped) + ")");

        if (done) {
            binding.imgDoneSleep.setVisibility(View.VISIBLE);
            if (score == 0) {
                binding.txtQuestionFatigueSleep.setVisibility(View.GONE);
                binding.txtQuestionnaireSkipped.setVisibility(View.VISIBLE);
            } else {
                binding.rdBtnESSFatigue.setEnabled(false);
                binding.txtQuestionFatigueSleep.setVisibility(View.VISIBLE);
                binding.txtRawValueSleep.setText(getString(R.string.total_score) + score + " / 24");
                binding.txtRawValueSleep.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initTab() {
        binding.tableLayout.setVisibility(View.VISIBLE);
        binding.btnConfirm.setVisibility(View.GONE);

        // Sleep always present
        binding.cellScoreSleep.setText(String.valueOf(sleepQuestionnaire.getScore()));
        displayResult("sleep");

        // FSMC only if not stroke
        if (!isStrokePatient && fsmcQuestionnaire != null) {
            binding.cellFSMCScore.setText(String.valueOf(fsmcQuestionnaire.getScore()));
            displayResult("fsmc");
        }
    }

    private void displayResult(String category) {
        int questionAnsSleep = sleepQuestionnaire.getAnsweredQues();
        int scoreSleep = sleepQuestionnaire.getScore();
        String text_interpretations;

        if(category.equals("sleep")){
            if(scoreSleep == 0 && (questionAnsSleep != 9)){
                text_interpretations = getString(R.string.skipped_questionniare);
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellResultSleep.setColorSections(upperlimit, colors);
                binding.cellResultSleep.setUserScore(2.75f);
                binding.cellResultSleep.setUserText(text_interpretations);
            }else{
                if (scoreSleep<4){
                    text_interpretations = getString(R.string.good);
                } else if (scoreSleep <10) {
                    text_interpretations = getString(R.string.moderate);
                } else {
                    text_interpretations = getString(R.string.severe);
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

                float user_score_rescale = binding.cellResultSleep.rescaleValue((float) scoreSleep,min,redEnd, 40.0f);

                binding.cellResultSleep.setColorSections(upperlimit, colors);
                binding.cellResultSleep.setUserScore(user_score_rescale);
                binding.cellResultSleep.setUserText(text_interpretations);
            }
        }

        if(!patientInfo.getDiagnosis().equals("Stroke")) {

            int questionAnsFSMC = fsmcQuestionnaire.getAnsweredQues();
            int scoreFSMC = fsmcQuestionnaire.getScore();

            if (category.equals("fsmc")) {
                if (scoreFSMC == 0 && (questionAnsFSMC != 21)) {
                    String txt_interpretations = getString(R.string.skipped_questionniare);
                    int[] colors = {Color.rgb(128, 128, 128)};
                    float[] upperlimit = {5.5f};
                    binding.cellFSMCResult.setColorSections(upperlimit, colors);
                    binding.cellFSMCResult.setUserScore(2.75f);
                    binding.cellFSMCResult.setUserText(txt_interpretations);
                } else {
                    String txt_interpretations = "";
                    if (scoreFSMC < 43) {
                        txt_interpretations = getString(R.string.good);
                    } else if (scoreFSMC < 53) {
                        txt_interpretations = getString(R.string.mild_fatigue);
                    } else if (scoreFSMC < 63) {
                        txt_interpretations = getString(R.string.moderate_fatigue);
                    } else {
                        txt_interpretations = getString(R.string.severe_fatigue);
                    }

                    int[] colors = {android.graphics.Color.GREEN, Color.YELLOW, Color.rgb(255, 165, 0), Color.RED};

                    float min = 0.0f;
                    float greenEnd = 43.0f;
                    float yellowEnd = 53.0f;
                    float orangeEnd = 63.0f;
                    float redEnd = 80.0f;

                    float[] upperlimit = {binding.cellFSMCResult.rescaleValue(greenEnd, min, redEnd, 40.0f),
                            binding.cellFSMCResult.rescaleValue(yellowEnd, min, redEnd, 40.0f),
                            binding.cellFSMCResult.rescaleValue(orangeEnd, min, redEnd, 40.0f),
                            binding.cellFSMCResult.rescaleValue(redEnd, min, redEnd, 40.0f)};

                    float user_score_rescale = binding.cellFSMCResult.rescaleValue((float) scoreFSMC, min, redEnd, 40.0f);

                    binding.cellFSMCResult.setColorSections(upperlimit, colors);
                    binding.cellFSMCResult.setUserScore(user_score_rescale);
                    binding.cellFSMCResult.setUserText(txt_interpretations);
                }
            }
        }
    }

    private void listenRadioGroup() {
        binding.radioGroup2.setOnCheckedChangeListener((group, checkedId) -> {
            redo_questionnaire = false; // reset
            if (checkedId == R.id.rdBtnESSFatigue) {
                questionnaire = "sleep";
                if (sleepQuestionnaire.getSkippedQues() > 3)
                    redo_questionnaire = true;
            } else if (checkedId == R.id.rdBtnFSMC && !isStrokePatient) {
                questionnaire = "fsmc";
                if (fsmcQuestionnaire.getSkippedQues() > 4)
                    redo_questionnaire = true;
            }
        });
    }

    private void listenBtnConfirm() {
        binding.btnConfirm.setOnClickListener(v -> {
            if ("sleep".equals(questionnaire)) {
                navigateToNextActivityWithoutFinish(SleepView.class);
            } else if ("fsmc".equals(questionnaire) && !isStrokePatient) {
                navigateToNextActivityWithoutFinish(IntroductionFSMCActivity.class);
            }
        });
    }

    private void uploadData(File file) {
        new DataTransfer(this).uploadFile(file);
    }

    @Override
    public void prepareIntent(Intent intent) {
        super.prepareIntent(intent);
        intent.putExtra("questionnaire", questionnaire);
    }
}
