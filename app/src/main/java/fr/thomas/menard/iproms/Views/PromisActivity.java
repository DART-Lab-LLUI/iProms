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
import fr.thomas.menard.iproms.databinding.ActivityPromisBinding;

public class PromisActivity extends BaseActivity {

    private ActivityPromisBinding binding;

    private String rating;

    private int numberQuestion = 0;

    private WriteCSV writeCSVClass;

    private String categorie;

    private boolean touched = false, redo_questionnaire=false;

    @Override
    public void init(){
        writeCSVClass = WriteCSV.getInstance(this);

        translateText();
        retrieveInfos();
        displayQuestion();
        displayLegend();
        reinit_questionnaire();
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

    private void translateText(){
        binding.ratingTextLangue.setText(R.string.your_rating);
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

    private void retrieveInfos(){
        ReadCSV.retrieveInfos(this);

        numberQuestion = Integer.parseInt(InfoFile.questionAnsPROMIS);
        if(numberQuestion==0)
            numberQuestion = 1;

        retrieveCategorie(numberQuestion);
        int pourcentage = 100 * (numberQuestion) / 10;
        binding.txtPoucentageDone.setText(String.valueOf(pourcentage));
    }

    private void retrieveCategorie(int numberQuestion){
        if(numberQuestion == 2 || numberQuestion == 4 ||numberQuestion == 5 ||numberQuestion == 10)
            categorie = "physical";
        else if(numberQuestion == 3 || numberQuestion == 6||numberQuestion == 7 ||numberQuestion == 8)
            categorie = "mental";

        else
            categorie = "raw";

    }

    private void reinit_questionnaire(){
        if(redo_questionnaire){
            WriteCSV.getInstance(this).reinit_questionnaire_Promis(this);
        }
    }


    private void finishQuestionnaire(){
        binding.btnSkipQuestionnaire.setOnClickListener(v -> {
            modifyCSVInfos("done", "0", false, true);
            navigateToNextActivity(MainActivity.class);
        });
    }

    private void listenBtnConfirm(){
        binding.btnConfirm.setOnClickListener(v -> {
            write_csv(rating);
            int score = 0;
            if (categorie.equals("physical"))
                score = Integer.parseInt(InfoFile.avg_score_PROMIS_physical) + Integer.parseInt(rating);
            else if (categorie.equals("mental")) {
                score = Integer.parseInt(InfoFile.avg_score_PROMIS_mental) + Integer.parseInt(rating);
            }

            if(numberQuestion==10){
                modifyCSVInfos("done", String.valueOf(score), false, false);
                navigateToNextActivity(MainActivity.class);
            }else {
                modifyCSVInfos("not finished", String.valueOf(score), false, false);
                navigateToNextActivity(PromisActivity.class);
            }
        });
    }

    private void listenBtnSkip(){
        binding.btnSkip.setOnClickListener(v -> skip());
    }

    private void skip(){
        write_csv("skip");
        if(numberQuestion==10){

            if(categorie.equals("physical"))
                modifyCSVInfos("done", String.valueOf(InfoFile.avg_score_PROMIS_physical), true, false);
            else if (categorie.equals("mental")) {
                modifyCSVInfos("done", String.valueOf(InfoFile.avg_score_PROMIS_mental), true, false);
            }

            navigateToNextActivity(MainActivity.class);
        }else {
            if(categorie.equals("physical"))
                modifyCSVInfos("not finished", String.valueOf(InfoFile.avg_score_PROMIS_physical), true, false);
            else if (categorie.equals("mental")) {
                modifyCSVInfos("not finished", String.valueOf(InfoFile.avg_score_PROMIS_mental), true, false);
            }
            navigateToNextActivity(PromisActivity.class);
        }
    }

    private void displayQuestion(){
        if(numberQuestion==1){
            binding.txtQuestion.setText(R.string.promis_1);
        } else if (numberQuestion ==2) {
            binding.txtQuestion.setText(R.string.promis_2);
        }else if (numberQuestion ==3) {
            binding.txtQuestion.setText(R.string.promis_3);
        }else if (numberQuestion ==4) {
            binding.txtQuestion.setText(R.string.promis_4);
        }else if (numberQuestion ==5) {
            binding.txtQuestion.setText(R.string.promis_5);
        }else if (numberQuestion ==6) {
            binding.txtQuestion.setText(R.string.promis_6);
        }else if (numberQuestion ==7) {
            binding.txtQuestion.setText(R.string.promis_7);
        }else if (numberQuestion ==8) {
            binding.txtQuestion.setText(R.string.promis_8);
        }else if (numberQuestion ==9) {
            binding.txtQuestion.setText(R.string.promis_8);
        }else if (numberQuestion ==10) {
            binding.txtQuestion.setText(R.string.promis_10);
        }
    }

    private void displayLegend(){
        if(numberQuestion==1 || numberQuestion==2 || numberQuestion==3 || numberQuestion==4 || numberQuestion==5 || numberQuestion==9){
            binding.txt0.setText(R.string.promis_legend_1to59_1);
            binding.txt1.setText(R.string.promis_legend_1to59_2);
            binding.txt2.setText(R.string.promis_legend_1to59_3);
            binding.txt3.setText(R.string.promis_legend_1to59_4);
            binding.txt6.setText(R.string.promis_legend_1to59_5);
        } else if (numberQuestion==6 || numberQuestion==7 || numberQuestion==8 || numberQuestion==10) {
            binding.txt0.setText(R.string.promis_legend_6to10_1);
            binding.txt1.setText(R.string.promis_legend_6to10_2);
            binding.txt2.setText(R.string.promis_legend_6to10_3);
            binding.txt3.setText(R.string.promis_legend_6to10_4);
            binding.txt6.setText(R.string.promis_legend_6to10_5);
        }
    }

    private void modifyCSVInfos(String done, String  score, boolean skip, boolean skip_questionnaire){
        String csvFilePath = FileManager.getInfoFilename(this);
        WriteCSV.getInstance(this).modifyCSVInfos_Promis(csvFilePath, categorie, numberQuestion, Integer.parseInt(InfoFile.skipped_question_promis), done, score, skip, skip_questionnaire);
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
                    rating = String.valueOf(3);
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
        boolean exist_file = FileManager.isPromisFileExist(this);
        String csv_path = FileManager.getPromisFilename(this);
        String idPatient = Patient.getPatient().getPatientId(this);
        String caseID = Patient.getPatient().getCaseId(this);
        String date = Patient.getPatient().getDate(this);

        if(!exist_file){
            writeCSVClass.createAndWriteCSV_fatigue(csv_path, idPatient,caseID, date, String.valueOf(numberQuestion), rating);
        }else{
            writeCSVClass.writeDataCSV_fatigue(csv_path,  String.valueOf(numberQuestion), rating);
        }
    }
}