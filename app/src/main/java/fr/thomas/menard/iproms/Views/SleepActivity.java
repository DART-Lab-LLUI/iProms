package fr.thomas.menard.iproms.Views;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import fr.thomas.menard.iproms.Model.InfoFile;
import fr.thomas.menard.iproms.Model.Patient;
import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.FileManager;
import fr.thomas.menard.iproms.Utils.ReadCSV;
import fr.thomas.menard.iproms.Utils.WriteCSV;
import fr.thomas.menard.iproms.databinding.ActivitySleepBinding;

public class SleepActivity extends BaseActivity {

    private ActivitySleepBinding binding;
    private String rating;
    private int numberQuestion;
    private int total_Score;
    private int questionAns, skipped_question;
    private boolean touched = false, redo_questionnaire = false;
    private String[] sleepQuestionScores = new String[8];
    private WriteCSV writeCSVClass;

    @Override
    public void init(){
        retrieveGeneralInfos();
        writeCSVClass = WriteCSV.getInstance(this);
        reinit_questionnaire();
    }

    @Override
    public void onStart() {
        super.onStart();
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

    private void retrieveGeneralInfos(){
        ReadCSV.retrieveInfos(this);
        if (InfoFile.score_sleep == null || InfoFile.score_sleep.isEmpty()) {
            total_Score = 0;
        } else {
            try {
                total_Score = Integer.parseInt(InfoFile.score_sleep);
            } catch (NumberFormatException e) {
                Log.e("SleepActivity", "Error parsing score_sleep: " + e.getMessage());
                total_Score = 0; // default 0 if parsing fails
            }
        }

        if (InfoFile.questionAnsSleep == null || InfoFile.questionAnsSleep.isEmpty()) {
            numberQuestion = 1; // default 1 if empty
        } else {
            try {
                numberQuestion = Integer.parseInt(InfoFile.questionAnsSleep);
            } catch (NumberFormatException e) {
                Log.e("SleepActivity", "Error parsing questionAnsSleep: " + e.getMessage());
                numberQuestion = 1; // default 1 if parsing fails
            }
        }

        if (InfoFile.skipped_question_sleep == null || InfoFile.skipped_question_sleep.isEmpty()) {
            skipped_question = 0; // default 0 if empty
        } else {
            try {
                skipped_question = Integer.parseInt(InfoFile.skipped_question_sleep);
            } catch (NumberFormatException e) {
                Log.e("SleepActivity", "Error parsing skipped_question_sleep: " + e.getMessage());
                skipped_question = 0; // default 0 if parsing fails
            }
        }

        questionAns = numberQuestion;

        // retrieve individual scores
        for (int i = 0; i < sleepQuestionScores.length; i++) {
            sleepQuestionScores[i] = InfoFile.sleepQuestionScores[i];
        }

        if(numberQuestion==0) numberQuestion = 1;
        if (numberQuestion > 8) numberQuestion = 8;

        int percentage = 100 * numberQuestion / 8;
        binding.txtPoucentageDone.setText(String.valueOf(percentage));

        Integer questionID = getResources().getIdentifier("question_ess_" + numberQuestion, "string", getPackageName());
        Integer txtinfo_0 = getResources().getIdentifier("txt_info_ess_0", "string", getPackageName());
        Integer txtinfo_1 = getResources().getIdentifier("txt_info_ess_1", "string", getPackageName());
        Integer txtinfo_2 = getResources().getIdentifier("txt_info_ess_2", "string", getPackageName());
        Integer txtinfo_3 = getResources().getIdentifier("txt_info_ess_3", "string", getPackageName());

        binding.txtQuestion.setText(getString(questionID));
        binding.txtinfo0.setText(getString(txtinfo_0));
        binding.txtinfo1.setText(getString(txtinfo_1));
        binding.txtinfo2.setText(getString(txtinfo_2));
        binding.txtinfo3.setText(getString(txtinfo_3));
    }

    private void reinit_questionnaire() {
        if (redo_questionnaire) {
            WriteCSV.getInstance(this).reinit_questionnaire_Sleep(this);

            // reinitialize individual scores
            for (int i = 0; i < sleepQuestionScores.length; i++) {
                sleepQuestionScores[i] = "0";
            }
        }
    }

    private void finishQuestionnaire(){
        binding.btnSkipQuestionnaire.setOnClickListener(v -> {
            modifyCSVInfos("done", "0", false, true, numberQuestion);
            navigateToNextActivity(OptionalQuestionnairesActivity.class);
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
            //modifyLast_general_Question();
            // write_csv("exit");
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
            write_csv(rating);
            total_Score += Integer.parseInt(rating);

            // store current rating in array
            sleepQuestionScores[numberQuestion - 1] = rating; // numberQuestion starts at 1, array index at 0

            if(numberQuestion==8) {
                modifyCSVInfos("done", String.valueOf(total_Score), false, false, numberQuestion);
                navigateToNextActivity(OptionalQuestionnairesActivity.class);
            } else {
                numberQuestion++;
                modifyCSVInfos("not finished", String.valueOf(total_Score), false, false, numberQuestion);
                navigateToNextActivity(SleepActivity.class);
            }
        });
    }


    private void listenBtnSkip(){
        binding.btnSkip.setOnClickListener(v -> skip());
    }

    private void skip(){
        // record skip
        sleepQuestionScores[numberQuestion - 1] = "-1";
        skipped_question++;
        write_csv("skip");

        if(numberQuestion==8) {
            modifyCSVInfos("done", String.valueOf(total_Score), true, false, numberQuestion);
            navigateToNextActivity(OptionalQuestionnairesActivity.class);
        } else {
            numberQuestion++;
            modifyCSVInfos("not finished", String.valueOf(total_Score), true, false, numberQuestion);
            navigateToNextActivity(SleepActivity.class);
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

    private void modifyCSVInfos(String done, String  score, boolean skip, boolean skip_questionnaire, int numberQuestion){
        WriteCSV.getInstance(this).modifyCSVInfos_Sleep(this, numberQuestion, skipped_question, done, score, skip, skip_questionnaire, sleepQuestionScores, rating);
    }


    private void write_csv(String rating){
        String csv_path = FileManager.getESSFilename(this);
        String idPatient = Patient.getPatient().getPatientId();
        String caseID = Patient.getPatient().getCaseId();
        String date = Patient.getPatient().getDate();

        if(!FileManager.isESSFileExist(this)){
            writeCSVClass.createAndWriteCSV_sleep(csv_path, idPatient,caseID, date, String.valueOf(numberQuestion), rating);
        }else{
            writeCSVClass.writeDataCSV_sleep(csv_path, String.valueOf(numberQuestion), rating, sleepQuestionScores);
        }
    }

}