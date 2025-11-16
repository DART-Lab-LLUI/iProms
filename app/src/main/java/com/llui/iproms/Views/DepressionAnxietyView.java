package com.llui.iproms.Views;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import com.llui.iproms.Enum.QuestionnaireType;
import com.llui.iproms.FileWriter.AbstractQuestionnaire;
import com.llui.iproms.FileWriter.DepressionAnxietyQuestionnaire;
import com.llui.iproms.Model.Questionnaires;
import com.llui.iproms.R;
import com.llui.iproms.databinding.ActivityDepressionAnxietyBinding;

public class DepressionAnxietyView extends BaseActivity {
    private ActivityDepressionAnxietyBinding binding;
    private String rating;
    private boolean touched = false, redo_questionnaire = false;
    private AbstractQuestionnaire questionnaire;
    private int currentQuestionNr = 0;

    @Override
    public void init(){
        if(redo_questionnaire) {
            AbstractQuestionnaire newQuestionnaire = new DepressionAnxietyQuestionnaire();
            Questionnaires.setNewQuestionnaire(QuestionnaireType.DEPRESSIONANXIETY, newQuestionnaire);
        }

        questionnaire = Questionnaires.get(this, QuestionnaireType.DEPRESSIONANXIETY);
        currentQuestionNr = questionnaire.getCurrentQuestion();
        updateView();
    }

    @Override
    public void listenBtn() {
        finishQuestionnaire();
        listenSeekbar();
        listenBtnConfirm();
        listenBtnSkip();
    }

    @Override
    public void setBinding() {
        binding = ActivityDepressionAnxietyBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    @Override
    public void processReceivedIntent(Intent intent) {
        super.processReceivedIntent(intent);
        redo_questionnaire = intent.getBooleanExtra("redo_questionnaire",false);
    }

    @Override
    public void prepareIntent(Intent intent) {
        super.prepareIntent(intent);
    }

    private void finishQuestionnaire(){
        binding.btnSkipQuestionnaire.setOnClickListener(v -> {
            questionnaire.skipQuestionnaire(this);
            navigateToNextActivity(MainActivity.class);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            // write_csv("exit");
            navigateToNextActivity(MainActivity.class);
            return true;
        } else if (id == R.id.action_skip) {
            skip();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void updateView() {
        binding.txtIntro.setText(R.string.txt_intro_depression);
        binding.txtQuestion.setText(questionnaire.getCurrentQuestionId());

        String[] answers = questionnaire.getAnswerOptions(this);
        binding.txtinfo0.setText(answers[0]);
        binding.txtinfo1.setText(answers[1]);
        binding.txtinfo2.setText(answers[2]);
        binding.txtinfo3.setText(answers[3]);

        int pourcentage = 100 * (currentQuestionNr+1) / questionnaire.getQuestionIdsLength();
        binding.txtPoucentageDoneDep.setText(String.valueOf(pourcentage));
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
            navigateToNextActivity(DepressionAnxietyView.class);
        }
    }

    private void listenSeekbar(){
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
                    rating = String.valueOf(2);
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