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
import fr.thomas.menard.iproms.databinding.ActivityBdiBinding;

public class BDI_Activity extends BaseActivity {

    private ActivityBdiBinding binding;
    private String rating;
    private int numberQuestion;
    private int total_Score;
    private int questionAns, skipped_question;
    private boolean touched = false, redo_questionnaire = false;
    private String[] bdiQuestionScores = new String[21];
    private WriteCSV writeCSVClass;

    @Override
    public void init(){
        writeCSVClass = WriteCSV.getInstance(this);
        retrieveGeneralInfos();
        reinit_questionnaire();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void listenBtn() {
        listenSeekbar();
        listenBtnSkip();
        listenBtnConfirm();
        finishQuestionnaire();
    }

    @Override
    public void setBinding() {
        binding = ActivityBdiBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    private void retrieveGeneralInfos(){
        ReadCSV.retrieveInfos(this);

        // check if InfoFile.questionAnsBDI is empty
        if (InfoFile.questionAnsBDI == null || InfoFile.questionAnsBDI.isEmpty()) {
            numberQuestion = 1; // default 1 if empty
        } else {
            try {
                numberQuestion = Integer.parseInt(InfoFile.questionAnsBDI);
            } catch (NumberFormatException e) {
                Log.e("BDI_Activity", "Error parsing questionAnsBDI: " + e.getMessage());
                numberQuestion = 1; // default 1 if parsing fails
            }
        }

        // check if InfoFile.score_bdi is empty
        if (InfoFile.score_bdi == null || InfoFile.score_bdi.isEmpty()) {
            total_Score = 0; // default 0 if empty
        } else {
            try {
                total_Score = Integer.parseInt(InfoFile.score_bdi);

            } catch (NumberFormatException e) {
                Log.e("BDI_Activity", "Error parsing score_bdi: " + e.getMessage());
                total_Score = 0; // default 0 if parsing fails
            }
        }

        if (InfoFile.skipped_question_bdi == null || InfoFile.skipped_question_bdi.isEmpty()) {
            skipped_question = 0; // default 0 if empty
        } else {
            try {
                skipped_question = Integer.parseInt(InfoFile.skipped_question_bdi);
            } catch (NumberFormatException e) {
                Log.e("BDI_Activity", "Error parsing skipped_question_bdi: " + e.getMessage());
                skipped_question = 0; // default 0 if parsing fails
            }
        }

        questionAns = numberQuestion;

        // retrieve individual scores
        for (int i = 0; i < bdiQuestionScores.length; i++) {
            bdiQuestionScores[i] = InfoFile.bdiQuestionScores[i];
        }

        if(numberQuestion==0) numberQuestion = 1;
        if (numberQuestion > 21) numberQuestion = 21;

        int percentage = 100 * numberQuestion / 21;
        binding.txtPoucentageDone.setText(String.valueOf(percentage));


        Integer questionID = getResources().getIdentifier("bdi_ii_" + numberQuestion, "string", getPackageName());
        Integer txtinfo_0 = getResources().getIdentifier("bdi_ii_"+numberQuestion +"_0", "string", getPackageName());
        Integer txtinfo_1 = getResources().getIdentifier("bdi_ii_"+numberQuestion +"_1", "string", getPackageName());
        Integer txtinfo_2 = getResources().getIdentifier("bdi_ii_"+numberQuestion+"_2", "string", getPackageName());
        Integer txtinfo_3 = getResources().getIdentifier("bdi_ii_"+numberQuestion +"_3", "string", getPackageName());

        if (questionID != 0) {
            binding.txtQuestion.setText(getString(questionID));
        } else {
            Log.d("BDI_Activity", "Resource not found: bdi_ii_" + numberQuestion);
        }

        if (txtinfo_0 != 0) {
            binding.txtinfo0.setText(getString(txtinfo_0));
        } else {
            Log.d("BDI_Activity", "Resource not found: bdi_ii_" + numberQuestion + "_0");
        }

        if (txtinfo_1 != 0) {
            binding.txtinfo1.setText(getString(txtinfo_1));
        } else {
            Log.d("BDI_Activity", "Resource not found: bdi_ii_" + numberQuestion + "_1");
        }

        if (txtinfo_2 != 0) {
            binding.txtinfo3.setText(getString(txtinfo_2));
        } else {
            Log.d("BDI_Activity", "Resource not found: bdi_ii_" + numberQuestion + "_2");
        }

        if (txtinfo_3 != 0) {
            binding.txtinfo4.setText(getString(txtinfo_3));
        } else {
            Log.d("BDI_Activity", "Resource not found: bdi_ii_" + numberQuestion + "_3");
        }
    }

    // reinitialize the quesionnaire; reinitialize summary information
    private void reinit_questionnaire(){
        if(redo_questionnaire){
            WriteCSV.getInstance(this).reinit_questionnaire_BDI(this);

            // reinitialize individual scores
            for (int i = 0; i < bdiQuestionScores.length; i++) {
                bdiQuestionScores[i] = "0";
            }
        }
    }

    private void finishQuestionnaire() {
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

    private void listenBtnConfirm(){
        Log.d("TEST", "listenBtnConfirm() - numberQuestion (before): " + numberQuestion + " - skipped_question" + skipped_question);
        binding.btnConfirm.setOnClickListener(v -> {
            write_csv(rating);
            total_Score = total_Score + Integer.parseInt(rating);

            // store current rating in array
            bdiQuestionScores[numberQuestion - 1] = rating; // numberQuestion starts at 1, array index starts at 0

            if(numberQuestion==21) {
                modifyCSVInfos("done", String.valueOf(total_Score), false, false, numberQuestion);
                navigateToNextActivity(MainActivity.class);
            } else {
                numberQuestion++;
                modifyCSVInfos("not finished", String.valueOf(total_Score), false, false, numberQuestion);
                navigateToNextActivity(BDI_Activity.class);
            }
        });
    }


    private void listenBtnSkip(){
        binding.btnSkip.setOnClickListener(v -> skip());
    }

    private void skip(){
        // record skip
        bdiQuestionScores[numberQuestion - 1] = "-1";
        skipped_question++;
        write_csv("skip");

        if(numberQuestion==21) {
            modifyCSVInfos("done", String.valueOf(total_Score), true, false, numberQuestion);
            navigateToNextActivity(MainActivity.class);
        } else {
            numberQuestion++;
            modifyCSVInfos("not finished", String.valueOf(total_Score), true, false, numberQuestion);
            navigateToNextActivity(BDI_Activity.class);
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
        WriteCSV.getInstance(this).modifyCSVInfos_BDI(this, numberQuestion, skipped_question, done, score, skip, skip_questionnaire, bdiQuestionScores, rating);
    }


    private void write_csv(String rating){
        String csv_path = FileManager.getBDIFilename(this);
        String idPatient = Patient.getPatient().getPatientId();
        String caseID = Patient.getPatient().getCaseId();
        String date = Patient.getPatient().getDate();

        if(!FileManager.isBDIFileExist(this)){
            writeCSVClass.createAndWriteCSV_BDI(csv_path, idPatient,caseID, date, String.valueOf(numberQuestion), rating);
        }else{
            writeCSVClass.writeDataCSV_BDI(csv_path, String.valueOf(numberQuestion), rating, bdiQuestionScores);
        }
    }
}