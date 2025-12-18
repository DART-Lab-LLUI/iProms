package com.llui.iproms.Views;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;


import com.llui.iproms.Enum.QuestionnaireType;
import com.llui.iproms.Questionnaires.AbstractQuestionnaire;
import com.llui.iproms.Questionnaires.PromisQuestionnaire;
import com.llui.iproms.Model.Questionnaires;
import com.llui.iproms.R;
import com.llui.iproms.databinding.ActivityPromisBinding;

public class PromisView extends BaseActivity {
    private ActivityPromisBinding binding;
    private String rating;
    private AbstractQuestionnaire questionnaire;
    private int currentQuestionNr = 0;
    private boolean touched = false, redo_questionnaire=false;

    @Override
    public void init(){
        if(redo_questionnaire){
            AbstractQuestionnaire newQuestionnaire = new PromisQuestionnaire();
            Questionnaires.setNewQuestionnaire(QuestionnaireType.PROMIS, newQuestionnaire);
        }

        questionnaire = Questionnaires.get(this, QuestionnaireType.PROMIS);
        currentQuestionNr = questionnaire.getCurrentQuestion();
        updateView();
    }

    @Override
    public void listenBtn() {
        listenBtnConfirm();
        listenSeekbar();
        listenBtnSkip();
        finishQuestionnaire();
    }

    @Override
    public void setBinding() {
        binding = ActivityPromisBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    @Override
    public void processReceivedIntent(Intent intent) {
        super.processReceivedIntent(intent);
        redo_questionnaire = intent.getBooleanExtra("redo_questionnaire", false);
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
            navigateToNextActivity(PromisView.class);
        }
    }

    private void updateView(){
        binding.txtQuestion.setText(questionnaire.getCurrentQuestionId());

        int pourcentage = 100 * (currentQuestionNr+1) / questionnaire.getQuestionIdsLength();
        binding.txtPoucentageDone.setText(String.valueOf(pourcentage));

        displayLegend();
    }

    private void displayLegend(){
        int count;
        if(questionnaire.getCurrentQuestion() == 6) {
            count = 11;
            binding.seekbar.setMin(0);
            binding.seekbar.setMax(10);
        } else {
            count = 5;
        }

        // tell the seekbar how many ticks
        binding.seekbar.setMin(0);
        binding.seekbar.setMax(count-1);

        // for each of 11 textViews, either hide or show:
        TextView[] labels = new TextView[]{
                binding.txt00, binding.txt0, binding.txt1,
                binding.txt2, binding.txt3, binding.txt4,
                binding.txt5, binding.txt6, binding.txt7,
                binding.txt8, binding.txt9};

        for (int i=0; i < labels.length; i++) {
            labels[i].setVisibility(i < count ? View.VISIBLE : View.GONE);
        }

        String[] answers = questionnaire.getAnswerOptions(this);

        for (int i = 0; i < Math.min(count, answers.length); i++) {
            labels[i].setText(answers[i]);
        }
    }

    private void listenSeekbar(){
        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (questionnaire.getCurrentQuestion() == 6) {
                    rating = String.valueOf(progress);
                }  else {
                    rating = String.valueOf(progress + 1);
                }
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