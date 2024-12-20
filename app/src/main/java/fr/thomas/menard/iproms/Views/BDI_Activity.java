package fr.thomas.menard.iproms.Views;

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

    private boolean touched = false;

    private WriteCSV writeCSVClass;


    @Override
    public void init(){
        writeCSVClass = WriteCSV.getInstance(this);
        retrieveGeneralInfos();
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

    private void finishQuestionnaire() {
        binding.btnSkipQuestionnaire.setOnClickListener(v -> {
            modifyCSVInfos("done", "0", false, true);
            navigateToNextActivity(MainActivity.class);
        });
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


    private void retrieveGeneralInfos(){
        ReadCSV.retrieveInfos(this);
        numberQuestion = Integer.parseInt(InfoFile.questionAnsBDI);
        int percentage = 100 * numberQuestion / 21;
        binding.txtPoucentageDone.setText(String.valueOf(percentage));
        total_Score = Integer.parseInt(InfoFile.score_bdi);
        skipped_question = Integer.parseInt(InfoFile.skipped_question_bdi);

        if(numberQuestion==0){
            numberQuestion = 1;
        }

        Integer questionID = getResources().getIdentifier("bdi_ii_" + numberQuestion, "string", getPackageName());
        Integer txtinfo_0 = getResources().getIdentifier("bdi_ii_"+numberQuestion+"_0", "string", getPackageName());
        Integer txtinfo_1 = getResources().getIdentifier("bdi_ii_"+numberQuestion+"_1", "string", getPackageName());
        Integer txtinfo_2 = getResources().getIdentifier("bdi_ii_"+numberQuestion+"_2", "string", getPackageName());
        Integer txtinfo_3 = getResources().getIdentifier("bdi_ii_"+numberQuestion+"_3", "string", getPackageName());


        binding.txtQuestion.setText(getString(questionID));
        binding.txtinfo0.setText(getString(txtinfo_0));
        binding.txtinfo1.setText(getString(txtinfo_1));
        binding.txtinfo3.setText(getString(txtinfo_2));
        binding.txtinfo4.setText(getString(txtinfo_3));
    }

    private void listenBtnConfirm(){
        binding.btnConfirm.setOnClickListener(v -> {
            numberQuestion += 1;
            write_csv(rating);
            total_Score = total_Score + Integer.parseInt(rating);
            questionAns = questionAns + 1;

            if(numberQuestion==22){
                modifyCSVInfos("done", String.valueOf(total_Score), false, false);
                navigateToNextActivity(MainActivity.class);
            }
            else {
                modifyCSVInfos("not finished", String.valueOf(total_Score), false, false);
                navigateToNextActivity(BDI_Activity.class);
            }
        });
    }


    private void listenBtnSkip(){
        binding.btnSkip.setOnClickListener(v -> skip());
    }

    private void skip(){
        write_csv("skip");
        numberQuestion += 1;

        if(numberQuestion==22){
            modifyCSVInfos("done", String.valueOf(total_Score), true, false);
            navigateToNextActivity(MainActivity.class);
        }else{
            modifyCSVInfos("not finished", String.valueOf(total_Score), true, false);
            navigateToNextActivity(BDI_Activity.class);
        }
    }

    private void modifyCSVInfos(String done, String  score, boolean skip, boolean skip_questionnaire){
        WriteCSV.getInstance(this).modifyCSVInfos_BDI(this, numberQuestion, skipped_question, done, score, skip, skip_questionnaire);
    }


    private void write_csv(String rating){
        String csv_path = FileManager.getBDIFilename(this);
        String idPatient = Patient.getPatient().getPatientId();
        String caseID = Patient.getPatient().getCaseId();
        String date = Patient.getPatient().getDate();

        if(!FileManager.isBDIFileExist(this)){
            writeCSVClass.createAndWriteCSV_fatigue(csv_path, idPatient,caseID, date, String.valueOf(numberQuestion), rating);
        }else{
            writeCSVClass.writeDataCSV_fatigue(csv_path, String.valueOf(numberQuestion), rating);
        }
    }
}