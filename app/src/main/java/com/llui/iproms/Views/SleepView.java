package com.llui.iproms.Views;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.llui.iproms.Questionnaires.AbstractQuestionnaire;
import com.llui.iproms.Questionnaires.SleepQuestionnaire;
import com.llui.iproms.Enum.QuestionnaireType;
import com.llui.iproms.Model.Questionnaires;
import com.llui.iproms.R;
import com.llui.iproms.databinding.ActivitySleepBinding;

public class SleepView extends BaseActivity {

    private ActivitySleepBinding binding;
    private String rating;
    private boolean touched = false, redo_questionnaire = false;
    private AbstractQuestionnaire questionnaire;
    private int currentQuestionNr = 0;

    @Override
    public void init(){
        if(redo_questionnaire){
            AbstractQuestionnaire newQuestionnaire = new SleepQuestionnaire();
            Questionnaires.setNewQuestionnaire(QuestionnaireType.SLEEP, newQuestionnaire);
        }

        questionnaire = Questionnaires.get(this, QuestionnaireType.SLEEP);
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
        binding = ActivitySleepBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    private void updateView(){
        int pourcentage = 100 * (currentQuestionNr+1) / questionnaire.getQuestionIdsLength();
        binding.txtPoucentageDone.setText(String.valueOf(pourcentage));
        binding.txtQuestion.setText(questionnaire.getCurrentQuestionId());

        String[] answers = questionnaire.getAnswerOptions(this, 0);
        binding.txtinfo0.setText(answers[0]);
        binding.txtinfo1.setText(answers[1]);
        binding.txtinfo2.setText(answers[2]);
        binding.txtinfo3.setText(answers[3]);
    }

    private void finishQuestionnaire(){
        binding.btnSkipQuestionnaire.setOnClickListener(v -> {
            questionnaire.skipQuestionnaire(this);
            navigateToNextActivity(OptionalQuestionnairesActivity.class);
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
            navigateToNextActivity(OptionalQuestionnairesActivity.class);
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
            navigateToNextActivity(OptionalQuestionnairesActivity.class);
        } else {
            navigateToNextActivity(SleepView.class);
        }
    }

    private void listenSeekbar() {
        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                rating = String.valueOf(progress);
                binding.txtRating.setText(rating);
                binding.btnConfirm.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                binding.btnConfirm.setVisibility(View.VISIBLE);
                if(!touched){
                    rating = String.valueOf(1);
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