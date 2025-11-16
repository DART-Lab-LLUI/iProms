package com.llui.iproms.Views;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import com.llui.iproms.FileWriter.AbstractQuestionnaire;
import com.llui.iproms.FileWriter.FatigueQuestionnaire;
import com.llui.iproms.Enum.QuestionnaireType;
import com.llui.iproms.Model.Questionnaires;
import com.llui.iproms.R;
import com.llui.iproms.databinding.ActivityFatigueQuestionnaireBinding;

public class FatigueQuestionnaireView extends BaseActivity {

    private ActivityFatigueQuestionnaireBinding binding;
    private String rating;
    private boolean touched = false, redo_questionnaire=false;
    private AbstractQuestionnaire questionnaire;
    private int currentQuestionNr = 0;

    @Override
    public void init() {
        if(redo_questionnaire){
            AbstractQuestionnaire newQuestionnaire = new FatigueQuestionnaire();
            Questionnaires.setNewQuestionnaire(QuestionnaireType.FATIGUE, newQuestionnaire);
        }

        questionnaire = Questionnaires.get(this, QuestionnaireType.FATIGUE);
        currentQuestionNr = questionnaire.getCurrentQuestion();
        updateView();
    }

    @Override
    public void listenBtn() {
        listenSeekbar();
        listenBtnConfirm();
        listenBtnSkip();
        finishQuestionnaire();
    }

    @Override
    public void setBinding() {
        binding = ActivityFatigueQuestionnaireBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    @Override
    public void processReceivedIntent(Intent intent) {
        super.processReceivedIntent(intent);
        redo_questionnaire = intent.getBooleanExtra("redo_questionnaire", false);
    }

    private void updateView(){
        int pourcentage = 100 * (currentQuestionNr+1) / questionnaire.getQuestionIdsLength();
        binding.txtPoucentageDone.setText(String.valueOf(pourcentage));
        binding.txtQuestion.setText(questionnaire.getCurrentQuestionId());
    }

    private void finishQuestionnaire(){
        binding.btnSkipQuestionnaire.setOnClickListener(v -> {
            questionnaire.skipQuestionnaire(this);
            navigateToNextActivity(MainActivity.class);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_exit) {
            navigateToNextActivity(MainActivity.class);
            return true;
        } else if (id == R.id.action_skip) {
            skip();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void listenBtnConfirm(){
        binding.btnConfirm.setOnClickListener(v -> {
            questionnaire.startNextQuestion(this, Integer.parseInt(rating));
            goToNextActivity();
        });
    }

    private void listenBtnSkip(){
        binding.btnSkip.setOnClickListener(v -> skip());
    }

    private void skip(){
        questionnaire.skipQuestion(this);
        goToNextActivity();
    }

    private void goToNextActivity(){
        if(questionnaire.isQuestionnaireDone()) {
            navigateToNextActivity(MainActivity.class);
        } else {
            navigateToNextActivity(FatigueQuestionnaireView.class);
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
                    rating = String.valueOf(4);
                    binding.txtRating.setText(rating);
                    touched = true;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}