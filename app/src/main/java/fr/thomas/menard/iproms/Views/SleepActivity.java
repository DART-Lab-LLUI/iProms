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
import fr.thomas.menard.iproms.databinding.ActivitySleepBinding;

public class SleepActivity extends BaseActivity {

    private ActivitySleepBinding binding;
    private String rating;
    private int numberQuestion;
    private int total_Score;

    private int skipped_question;

    private boolean touched = false;

    private WriteCSV writeCSVClass;


    @Override
    public void init(){
        retrieveGeneralInfos();
        writeCSVClass = WriteCSV.getInstance(this);
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
            write_csv("exit");
            navigateToNextActivity(OptionalQuestionnairesActivity.class);
            return true;
        } else if (id == R.id.action_skip) {
            skip();
            return true;
        }
        return super.onOptionsItemSelected(item);

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

    private void retrieveGeneralInfos(){
        ReadCSV.retrieveInfos(this);
        numberQuestion = Integer.parseInt(InfoFile.questionAnsSleep);
        int percentage = 100 * numberQuestion / 8;
        binding.txtPoucentageDone.setText(String.valueOf(percentage));
        total_Score = Integer.parseInt(InfoFile.score_sleep);
        skipped_question = Integer.parseInt(InfoFile.skipped_question_sleep);

        if(numberQuestion==0){
            numberQuestion = 1;
        }

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

    private void listenBtnConfirm(){
        binding.btnConfirm.setOnClickListener(v -> {
            write_csv(rating);
            total_Score = total_Score + Integer.parseInt(rating);

            if(numberQuestion==8){

                modifyCSVInfos("done", String.valueOf(total_Score), false, false);
                navigateToNextActivity(OptionalQuestionnairesActivity.class);
            }
            else {
                modifyCSVInfos("not finished", String.valueOf(total_Score), false, false);
                navigateToNextActivity(SleepActivity.class);
            }
        });
    }


    private void listenBtnSkip(){
        binding.btnSkip.setOnClickListener(v -> skip());
    }

    private void skip(){
        write_csv("skip");
        if(numberQuestion==8){
            modifyCSVInfos("done", String.valueOf(total_Score), true, false);
            navigateToNextActivity(OptionalQuestionnairesActivity.class);
        }else{
            modifyCSVInfos("not finished", String.valueOf(total_Score), true, false);
            navigateToNextActivity(SleepActivity.class);
        }

    }

    private void finishQuestionnaire(){
        binding.btnSkipQuestionnaire.setOnClickListener(v -> {
            modifyCSVInfos("done", "0", false, true);
            navigateToNextActivity(OptionalQuestionnairesActivity.class);
        });
    }

    private void modifyCSVInfos(String done, String  score, boolean skip, boolean skip_questionnaire){
        WriteCSV.getInstance(this).modifyCSVInfos_Sleep(this, numberQuestion, skipped_question, done, score, skip, skip_questionnaire);
    }


    private void write_csv(String rating){
        String csv_path = FileManager.getESSFilename(this);
        String idPatient = Patient.getPatient().getPatientId();
        String caseID = Patient.getPatient().getCaseId();
        String date = Patient.getPatient().getDate();

        if(!FileManager.isESSFileExist(this)){
            writeCSVClass.createAndWriteCSV_fatigue(csv_path, idPatient,caseID, date, String.valueOf(numberQuestion), rating);
        }else{
            writeCSVClass.writeDataCSV_fatigue(csv_path, String.valueOf(numberQuestion), rating);
        }
    }

}