package fr.thomas.menard.iproms.Views;

import android.content.Context;
import android.content.Intent;
import android.icu.text.IDNA;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import fr.thomas.menard.iproms.App.MyApplication;
import fr.thomas.menard.iproms.Model.InfoFile;
import fr.thomas.menard.iproms.Model.Patient;
import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.FileManager;
import fr.thomas.menard.iproms.Utils.LocaleHelper;
import fr.thomas.menard.iproms.Utils.ReadCSV;
import fr.thomas.menard.iproms.Utils.WriteCSV;
import fr.thomas.menard.iproms.databinding.ActivityFsmcactivityBinding;

public class FSMCActivity extends BaseActivity {

    ActivityFsmcactivityBinding binding;
    private Context context;
    private String rating;
    private int numberQuestion;
    private int total_Score;
    private int questionAns, skipped_question;
    private String[] fsmcQuestionScores = new String[20];
    private boolean touched = false, redo_questionnaire = false;
    private WriteCSV writeCSVClass;

    @Override
    public void init(){
        writeCSVClass = WriteCSV.getInstance(this);
        context = LocaleHelper.setLocale(getApplicationContext(), MyApplication.language.getLanguage());
        retrieveGeneralInfos();
        reinit_questionnaire();
    }

    @Override
    public void onStart() {
        super.onStart();
        displayQuestions();
    }

    @Override
    public void listenBtn() {
        listenSeekbar();
        retrieveGeneralInfos();
        displayQuestions();
        listenBtnSkip();
        listenBtnConfirm();
        finishQuestionnaire();
    }

    @Override
    public void setBinding() {
        binding = ActivityFsmcactivityBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    private void retrieveGeneralInfos(){
        ReadCSV.retrieveInfos(this);
        if (InfoFile.scoreFSMC == null || InfoFile.scoreFSMC.isEmpty()) {
            total_Score = 0;
        } else {
            try {
                total_Score = Integer.parseInt(InfoFile.scoreFSMC);
            } catch (NumberFormatException e) {
                Log.e("FSMCActivity", "Error parsing scoreFSMC: " + e.getMessage());
                total_Score = 0; // default 0 if parsing fails
            }
        }

        if (InfoFile.questionAnsFCSM == null || InfoFile.questionAnsFCSM.isEmpty()) {
            numberQuestion = 1; // default 1 if empty
        } else {
            try {
                numberQuestion = Integer.parseInt(InfoFile.questionAnsFCSM);
            } catch (NumberFormatException e) {
                Log.e("FSMCActivity", "Error parsing questionAnsFCSM: " + e.getMessage());
                numberQuestion = 1; // default 1 if parsing fails
            }
        }

        if (InfoFile.skipped_question_fsmc == null || InfoFile.skipped_question_fsmc.isEmpty()) {
            skipped_question = 0; // default 0 if empty
        } else {
            try {
                skipped_question = Integer.parseInt(InfoFile.skipped_question_fsmc);
            } catch (NumberFormatException e) {
                Log.e("FSMCActivity", "Error parsing skipped_question_fsmc: " + e.getMessage());
                skipped_question = 0; // default 0 if parsing fails
            }
        }

        questionAns = numberQuestion;

        // retrieve individual scores
        for (int i = 0; i < fsmcQuestionScores.length; i++) {
            fsmcQuestionScores[i] = InfoFile.fsmcQuestionScores[i];
        }

        if(numberQuestion==0) numberQuestion = 1;
        if (numberQuestion > 20)  numberQuestion = 20;

        int percentage = 100 * numberQuestion / 20;
        binding.txtPoucentageDone.setText(String.valueOf(percentage));
        Log.d("TEST", "retrieveGeneralInfos() - numberQuestion: " + numberQuestion + " - skipped_question" + skipped_question);
    }

    private void displayQuestions(){
        int questionID = getResources().getIdentifier("question_fsmc_" + numberQuestion, "string", getPackageName());
        int txtinfo_0 = getResources().getIdentifier("txt_info_fsmc_0", "string", getPackageName());
        int txtinfo_1 = getResources().getIdentifier("txt_info_fsmc_1", "string", getPackageName());
        int txtinfo_2 = getResources().getIdentifier("txt_info_fsmc_2", "string", getPackageName());
        int txtinfo_3 = getResources().getIdentifier("txt_info_fsmc_3", "string", getPackageName());
        int txtinfo_4 = getResources().getIdentifier("txt_info_fsmc_4", "string", getPackageName());


        binding.txtQuestion.setText(getString(questionID));
        binding.txtinfo0.setText(getString(txtinfo_0));
        binding.txtinfo1.setText(getString(txtinfo_1));
        binding.txtinfo2.setText(getString(txtinfo_2));
        binding.txtinfo3.setText(getString(txtinfo_3));
        binding.txtinfo4.setText(getString(txtinfo_4));

    }

    private void reinit_questionnaire() {
        if (redo_questionnaire) {
            WriteCSV.getInstance(this).reinit_questionnaire_FSMC(this);

            // reinitialize individual scores
            for (int i = 0; i < fsmcQuestionScores.length; i++) {
                fsmcQuestionScores[i] = "0";
            }
        }
    }

    private void finishQuestionnaire() {
        binding.btnSkipQuestionnaire.setOnClickListener(v -> {
            String csvFilePath = FileManager.getInfoFilename(context);
            writeCSVClass.modifyCSVInfos_FCSM(csvFilePath, "done", "0", false, true, numberQuestion, skipped_question, fsmcQuestionScores, rating);
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
        Log.d("TEST", "listenBtnConfirm() - numberQuestion (before): " + numberQuestion + " - skipped_question" + skipped_question);
        binding.btnConfirm.setOnClickListener(v -> {
            write_csv(rating);
            total_Score = total_Score + Integer.parseInt(rating);

            // store current rating in array
            fsmcQuestionScores[numberQuestion - 1] = rating; // numberQuestion starts at 1, array index at 0

            String csvFilePath = FileManager.getInfoFilename(context);
            if(numberQuestion==20) {
                writeCSVClass.modifyCSVInfos_FCSM(csvFilePath, "done", String.valueOf(total_Score), false, false, numberQuestion, skipped_question, fsmcQuestionScores, rating);
                navigateToNextActivity(OptionalQuestionnairesActivity.class);
            } else {
                numberQuestion++;
                writeCSVClass.modifyCSVInfos_FCSM(csvFilePath, "not finished", String.valueOf(total_Score), false, false, numberQuestion, skipped_question, fsmcQuestionScores, rating);
                navigateToNextActivity(FSMCActivity.class);
            }
        });
    }

    private void listenBtnSkip(){
        binding.btnSkip.setOnClickListener(v -> skip());
    }

    private void skip(){
        // record skip
        fsmcQuestionScores[numberQuestion - 1] = "-1";
        skipped_question++;
        write_csv("skip");
        String csvFilePath = FileManager.getInfoFilename(context);

        if(numberQuestion==20) {
            writeCSVClass.modifyCSVInfos_FCSM(csvFilePath, "done", String.valueOf(total_Score), true, false, numberQuestion, skipped_question, fsmcQuestionScores, rating);
            navigateToNextActivity(OptionalQuestionnairesActivity.class);
        } else {
            numberQuestion++;
            writeCSVClass.modifyCSVInfos_FCSM(csvFilePath, "not finished", String.valueOf(total_Score), true, false, numberQuestion, skipped_question, fsmcQuestionScores, rating);
            navigateToNextActivity(FSMCActivity.class);
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

    private void write_csv(String rating){
        Patient patient = Patient.getPatient();
        String filepath = FileManager.getFSMCFilename(this);
        if(!FileManager.isFSMCFileExist(this)){
            writeCSVClass.createAndWriteCSV_FSMC(filepath, patient.getPatientId(), patient.getCaseId(), patient.getDate(), String.valueOf(numberQuestion), rating);
        }else{
            writeCSVClass.writeDataCSV_FSMC(filepath, String.valueOf(numberQuestion), rating, fsmcQuestionScores);
        }
    }

    @Override
    public void prepareIntent(Intent intent) {
        super.prepareIntent(intent);
        intent.putExtra("num_question", numberQuestion);
    }

    @Override
    public void processReceivedIntent(Intent intent) {
        super.processReceivedIntent(intent);
        total_Score = intent.getIntExtra("totalScore", 0);
    }
}