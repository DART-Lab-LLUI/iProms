package fr.thomas.menard.iproms.Views;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import fr.thomas.menard.iproms.Model.InfoFile;
import fr.thomas.menard.iproms.Model.Patient;
import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.FileManager;
import fr.thomas.menard.iproms.Utils.ReadCSV;
import fr.thomas.menard.iproms.Utils.WriteCSV;
import fr.thomas.menard.iproms.databinding.ActivityFatigueQuestionnaireBinding;

public class FatigueQuestionnaire extends BaseActivity {

    private ActivityFatigueQuestionnaireBinding binding;
    private String rating;
    private int numberQuestion;
    private int total_Score;
    private int questionAns, skipped_question;
    private String[] fatigueQuestionScores = new String[9];
    private boolean touched = false, redo_questionnaire=false;
    private WriteCSV writeCSVClass;

    @Override
    public void init() {
        writeCSVClass = WriteCSV.getInstance(this);
        ReadCSV.retrieveInfos(this);
        reinit_questionnaire();
        retrieveInfos();
        translateText();
    }

    @Override
    public void onStart() {
        super.onStart();
        displayQuestion();
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

    private void translateText(){
        binding.ratingTextLangue.setText(R.string.your_rating);
    }

    // retrieve information from InfoFile class; e.g. summary information
    private void retrieveInfos(){
        if (InfoFile.avg_score_fatigue == null || InfoFile.avg_score_fatigue.isEmpty()) {
            total_Score = 0; // default 0 if empty
        } else {
            try {
                total_Score = Integer.parseInt(InfoFile.avg_score_fatigue);
            } catch (NumberFormatException e) {
                Log.e("FatigueQuestionnaire", "Error parsing avg_score_fatigue: " + e.getMessage());
                total_Score = 0;
            }
        }

        if (InfoFile.questionAnsFatigue == null || InfoFile.questionAnsFatigue.isEmpty()) {
            numberQuestion = 1; // default 1 if empty
        } else {
            try {
                numberQuestion = Integer.parseInt(InfoFile.questionAnsFatigue);
            } catch (NumberFormatException e) {
                Log.e("FatigueQuestionnaire", "Error parsing questionAnsFatigue: " + e.getMessage());
                numberQuestion = 1;
            }
        }

        if (InfoFile.lastQuestionFatigue == null || InfoFile.lastQuestionFatigue.isEmpty()) {
            skipped_question = 0; // default 0 if empty
        } else {
            try {
                skipped_question = Integer.parseInt(InfoFile.lastQuestionFatigue);
            } catch (NumberFormatException e) {
                Log.e("FatigueQuestionnaire", "Error parsing lastQuestionFatigue: " + e.getMessage());
                skipped_question = 0;
            }
        }

        questionAns = numberQuestion; // seed answered count from CSV

        // retrieve individual scores
        for (int i = 0; i < fatigueQuestionScores.length; i++) {
            fatigueQuestionScores[i] = InfoFile.fatigueQuestionScores[i];
        }

        if(numberQuestion==0) numberQuestion = 1;
        if (numberQuestion > 9) numberQuestion = 9;

        int pourcentage = 100 * (numberQuestion) / 9;
        binding.txtPoucentageDone.setText(String.valueOf(pourcentage));
        Log.d("TEST", "retrieveInfos() - numberQuestion: " + numberQuestion + " - skippped_question: " + skipped_question);
    }

    // reinitialize the questionnaire; reinitialize summary information
    private void reinit_questionnaire(){
        if(redo_questionnaire){
            WriteCSV.getInstance(this).reinit_questionnaire_Fatigue(this);

            // reinitialize individual scores
            for (int i = 0; i < fatigueQuestionScores.length; i++) {
                fatigueQuestionScores[i] = "0";
            }
        }
    }

    private void finishQuestionnaire(){
        binding.btnSkipQuestionnaire.setOnClickListener(v -> {
            modifyCSVInfos("done", "0", false, true, numberQuestion);
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
            navigateToNextActivity(MainActivity.class);
            return true;
        } else if (id == R.id.action_skip) {
            skip();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    // call for when user confirms rating for question -> need to collect individual scores and pass them to modifyCSVInfos
    private void listenBtnConfirm(){
        Log.d("TEST", "listenBtnConfirm() - numberQuestion (before): " + numberQuestion + " - skipped_question" + skipped_question);
        binding.btnConfirm.setOnClickListener(v -> {
            write_csv(rating);
            total_Score += Integer.parseInt(rating);

            // store current rating in array
            fatigueQuestionScores[numberQuestion-1] = rating; // numberQuestion starts at 1, array index at 0

            if(numberQuestion==9) {
                modifyCSVInfos("done", String.valueOf(total_Score), false, false, numberQuestion);
                navigateToNextActivity(MainActivity.class);
            } else {
                numberQuestion++; // increment question number
                modifyCSVInfos("not finished", String.valueOf(total_Score), false, false, numberQuestion);
                navigateToNextActivity(FatigueQuestionnaire.class);
            }
        });
    }

    private void listenBtnSkip(){
        binding.btnSkip.setOnClickListener(v -> skip());
    }

    private void skip(){
        // record skip
        fatigueQuestionScores[numberQuestion-1] = "-1";
        skipped_question++; // increment skipped question number
        write_csv("skip");

        if(numberQuestion==9) {
            modifyCSVInfos("done", String.valueOf(total_Score), true, false, numberQuestion);
            navigateToNextActivity(MainActivity.class);
        } else {
            numberQuestion++;
            modifyCSVInfos("not finished", String.valueOf(total_Score), true, false, numberQuestion);
            navigateToNextActivity(FatigueQuestionnaire.class);
        }
    }

    private void displayQuestion(){
        if(numberQuestion==1){
            binding.txtQuestion.setText(R.string.question1_fatigue);
        } else if (numberQuestion ==2) {
            binding.txtQuestion.setText(R.string.question2_fatigue);
        }else if (numberQuestion ==3) {
            binding.txtQuestion.setText(R.string.question3_fatigue);
        }else if (numberQuestion ==4) {
            binding.txtQuestion.setText(R.string.question4_fatigue);
        }else if (numberQuestion ==5) {
            binding.txtQuestion.setText(R.string.question5_fatigue);
        }else if (numberQuestion ==6) {
            binding.txtQuestion.setText(R.string.question6_fatigue);
        }else if (numberQuestion ==7) {
            binding.txtQuestion.setText(R.string.question7_fatigue);
        }else if (numberQuestion ==8) {
            binding.txtQuestion.setText(R.string.question8_fatigue);
        }else if (numberQuestion ==9) {
            binding.txtQuestion.setText(R.string.question9_fatigue);
        }
    }

    // update the main CSV file; need to pass the collection of all the individual scores
    private void modifyCSVInfos(String done, String  score, boolean skip, boolean skip_questionnaire, int numberQuestion){
        Log.d("TEST", "modifyCSVInfos() - numberQuestion: " + numberQuestion + " - skipped_question" + skipped_question); // Add this line
        WriteCSV.getInstance(this).modifyCSVInfos_Fatigue(this, numberQuestion, skipped_question, done, score, skip, skip_questionnaire, fatigueQuestionScores, rating);
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

    // write current question's rating to fatigue-specific CSV file; only deals current question's rating; doesn't handle collection of all individual scores
    private void write_csv(String rating){
        String csv_path = FileManager.getFSSFilename(this);
        String idPatient = Patient.getPatient().getPatientId();
        String caseID = Patient.getPatient().getCaseId();
        String date = Patient.getPatient().getDate();

        if(!FileManager.isFSSFileExist(this)){
            writeCSVClass.createAndWriteCSV_fatigue(csv_path, idPatient,caseID, date, String.valueOf(numberQuestion), rating);
        }else{
            writeCSVClass.writeDataCSV_fatigue(csv_path,  String.valueOf(numberQuestion), rating, fatigueQuestionScores);
        }
    }
}