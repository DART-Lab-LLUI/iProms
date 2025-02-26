package fr.thomas.menard.iproms.Views;

import androidx.annotation.NonNull;
import android.content.Intent;
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
    private int total_Score = 0;

    private WriteCSV writeCSVClass;
    private int skipped_question = 0;

    private boolean touched = false, redo_questionnaire=false;

    @Override
    public void init() {
        writeCSVClass = WriteCSV.getInstance(this);
        ReadCSV.retrieveInfos(this);
        reinit_questionnaire();
        retrieveInfos();
        displayQuestion();
        translateText();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    private void retrieveInfos(){
        total_Score = Integer.parseInt(InfoFile.avg_score_fatigue);
        numberQuestion = Integer.parseInt(InfoFile.questionAnsFatigue);
        skipped_question = Integer.parseInt(InfoFile.lastQuestionFatigue);


        if(numberQuestion==0)
            numberQuestion = 1;


        int pourcentage = 100 * (numberQuestion) / 9;
        binding.txtPoucentageDone.setText(String.valueOf(pourcentage));
    }

    private void reinit_questionnaire(){
        if(redo_questionnaire){
            WriteCSV.getInstance(this).reinit_questionnaire_Fatigue(this);
        }
    }


    private void finishQuestionnaire(){
        binding.btnSkipQuestionnaire.setOnClickListener(v -> {
            modifyCSVInfos("done", "0", false, true);
            navigateToNextActivity(MainActivity.class);
        });
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

    private void listenBtnConfirm(){
        binding.btnConfirm.setOnClickListener(v -> {
            write_csv(rating);
            total_Score = total_Score + Integer.parseInt(rating);

            if(numberQuestion==9){
                modifyCSVInfos("done", String.valueOf(total_Score), false, false);
                navigateToNextActivity(MainActivity.class);
            }else {
                modifyCSVInfos("not finished", String.valueOf(total_Score), false, false);
                navigateToNextActivity(FatigueQuestionnaire.class);
            }
        });
    }

    private void listenBtnSkip(){
        binding.btnSkip.setOnClickListener(v -> skip());
    }

    private void skip(){
        write_csv("skip");
        if(numberQuestion==9){
            modifyCSVInfos("done", String.valueOf(total_Score), true, false);
            navigateToNextActivity(MainActivity.class);
        }else {
            modifyCSVInfos("not finished", String.valueOf(total_Score), true, false);
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

    private void modifyCSVInfos(String done, String  score, boolean skip, boolean skip_questionnaire){
        WriteCSV.getInstance(this).modifyCSVInfos_Fatigue(this, numberQuestion, skipped_question, done, score, skip, skip_questionnaire);
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

    private void write_csv(String rating){
        String csv_path = FileManager.getFSSFilename(this);
        String idPatient = Patient.getPatient().getPatientId(this);
        String caseID = Patient.getPatient().getCaseId(this);
        String date = Patient.getPatient().getDate(this);

        if(!FileManager.isFSSFileExist(this)){
            writeCSVClass.createAndWriteCSV_fatigue(csv_path, idPatient,caseID, date, String.valueOf(numberQuestion), rating);
        }else{
            writeCSVClass.writeDataCSV_fatigue(csv_path,  String.valueOf(numberQuestion), rating);
        }
    }
}