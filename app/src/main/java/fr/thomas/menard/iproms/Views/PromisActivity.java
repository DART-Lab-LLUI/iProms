package fr.thomas.menard.iproms.Views;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import java.util.Locale;

import fr.thomas.menard.iproms.BuildConfig;
import fr.thomas.menard.iproms.Model.InfoFile;
import fr.thomas.menard.iproms.Model.Patient;
import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.FileManager;
import fr.thomas.menard.iproms.Utils.ReadCSV;
import fr.thomas.menard.iproms.Utils.WriteCSV;
import fr.thomas.menard.iproms.Utils.tScore;
import fr.thomas.menard.iproms.databinding.ActivityPromisBinding;

public class PromisActivity extends BaseActivity {

    private ActivityPromisBinding binding;
    private String rating;
    private int numberQuestion;
    private int questionAns, skipped_question;
    private int total_score = 0;
    private int physicalRawSum = 0;
    private int mentalRawSum = 0;
    private WriteCSV writeCSVClass;
    private String categorie;
    private String[] promisQuestionScores = new String[10];
    private boolean touched = false, redo_questionnaire=false;

    @Override
    public void init(){
        writeCSVClass = WriteCSV.getInstance(this);
        retrieveInfos();
        reinit_questionnaire();
        translateText();
    }

    @Override
    public void onStart() {
        super.onStart();
        displayQuestion();
        displayLegend();
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

    private void retrieveInfos(){
        ReadCSV.retrieveInfos(this);

        // seed our running total from the CSV summary field

        physicalRawSum = InfoFile.avg_score_PROMIS_physical == null
                || InfoFile.avg_score_PROMIS_physical.isEmpty()
                ? 0
                : Integer.parseInt(InfoFile.avg_score_PROMIS_physical);
        mentalRawSum = InfoFile.avg_score_PROMIS_mental == null
                || InfoFile.avg_score_PROMIS_mental.isEmpty()
                ? 0
                : Integer.parseInt(InfoFile.avg_score_PROMIS_mental);

        if (InfoFile.questionAnsPROMIS == null || InfoFile.questionAnsPROMIS.isEmpty()) {
            numberQuestion = 1;
        } else {
            try {
                numberQuestion = Integer.parseInt(InfoFile.questionAnsPROMIS);
            } catch (NumberFormatException e) {
                Log.e("PromisActivity", "Error parsing questionAnsPROMIS: " + e.getMessage());
                numberQuestion = 1; // default 1 if parsing fails
            }
        }

        if (InfoFile.skipped_question_promis == null || InfoFile.skipped_question_promis.isEmpty()) {
            skipped_question = 0; // default 0 if empty
        } else {
            try {
                skipped_question = Integer.parseInt(InfoFile.skipped_question_promis);
            } catch (NumberFormatException e) {
                Log.e("PromisActivity", "Error parsing skipped_question_promis: " + e.getMessage());
                skipped_question = 0; // default 0 if parsing fails
            }
        }

        questionAns = numberQuestion;

        // retrieve individual scores
        for (int i = 0; i < promisQuestionScores.length; i++) {
            promisQuestionScores[i] = InfoFile.promisQuestionScores[i];
        }

        if(numberQuestion==0) numberQuestion = 1;
        if (numberQuestion > 10) numberQuestion = 10;

        retrieveCategorie(numberQuestion);

        int pourcentage = 100 * (numberQuestion) / 10;
        binding.txtPoucentageDone.setText(String.valueOf(pourcentage));
    }

    private void retrieveCategorie(int numberQuestion){
        if(numberQuestion == 3 || numberQuestion == 6 ||numberQuestion == 7 ||numberQuestion ==8)
            categorie = "physical";
        else if(numberQuestion == 2 || numberQuestion == 4||numberQuestion == 5 ||numberQuestion == 10)
            categorie = "mental";
        else
            categorie = "raw";
    }

    private void reinit_questionnaire(){
        if(redo_questionnaire){
            WriteCSV.getInstance(this).reinit_questionnaire_Promis(this);

            // reinitialize individual scores
            for (int i = 0; i < promisQuestionScores.length; i++) {
                promisQuestionScores[i] = "0";
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
        Log.d("TEST", "listenBtnConfirm() - numberQuestion (before): " + numberQuestion + " - skipped_question" + InfoFile.skipped_question_promis);
        binding.btnConfirm.setOnClickListener(v -> {

            // figure out which bucket this question lives in
            retrieveCategorie(numberQuestion);

            write_csv(rating);
            int answer = Integer.parseInt(rating);

            // update only right raw-sum
            if ("physical".equals(categorie)) physicalRawSum += answer;
            else if ("mental".equals(categorie)) mentalRawSum += answer;

            // store the current rating in per-question array
            promisQuestionScores[numberQuestion - 1] = rating; // numberQuestion starts at 1, array index at 0

            // if last question --> Raw to T-score lookup
            if(numberQuestion == 10) {
                // look up both t-scores from raw accumulators
                double tPhys = tScore.lookupPhsyicalTscore(physicalRawSum);
                double tMntl = tScore.lookupMentalTscore(mentalRawSum);

                // format them
                String physOut = String.format(Locale.getDefault(), "%.1f", tPhys);
                String mntlOut = String.format(Locale.getDefault(), "%.1f", tMntl);

                // update in memory InfoFile
                InfoFile.avg_score_PROMIS_physical = physOut;
                InfoFile.avg_score_PROMIS_mental = mntlOut;

                // persist both into your info file
                String path = FileManager.getInfoFilename(this);
                WriteCSV csv = WriteCSV.getInstance(this);

                // write final "done" row w/ T-score
                csv.modifyCSVInfos_Promis(path, "physical", numberQuestion, skipped_question, "done", physOut, false, false, promisQuestionScores, rating);
                csv.modifyCSVInfos_Promis(path, "mental", numberQuestion, skipped_question, "done", mntlOut, false, false, promisQuestionScores, rating);
                navigateToNextActivity(MainActivity.class);

            } else {
                numberQuestion++;
                String path = FileManager.getInfoFilename(this);
                WriteCSV csv = WriteCSV.getInstance(this);
                csv.modifyCSVInfos_Promis(path, categorie, numberQuestion, skipped_question, "not finished", String.valueOf(physicalRawSum), false, false, promisQuestionScores, rating);
                csv.modifyCSVInfos_Promis(path, categorie, numberQuestion, skipped_question, "not finished", String.valueOf(mentalRawSum), false, false, promisQuestionScores, rating);
                navigateToNextActivity(PromisActivity.class);
            }
        });
    }

    private void listenBtnSkip(){
        binding.btnSkip.setOnClickListener(v -> skip());
    }

    private void skip(){
        // figure out which bucket question in
        retrieveCategorie(numberQuestion);

        // record skip
        promisQuestionScores[numberQuestion - 1] = "-1";

        // bomp local skip counter
        skipped_question++;
        // synt it back into InfoFile so MainActivity will see it
        InfoFile.skipped_question_promis = String.valueOf(skipped_question);

        // write per-question CSV
        write_csv("skip");

        // raw questions don't add to either sum

        // update master CSV row
        if(numberQuestion==10) {
            double tPhys = tScore.lookupPhsyicalTscore(physicalRawSum);
            double tMntl = tScore.lookupMentalTscore(mentalRawSum);
            String physOut = String.format(Locale.getDefault(), "%.1f", tPhys);
            String mntlOut = String.format(Locale.getDefault(), "%.1f", tMntl);

            InfoFile.avg_score_PROMIS_physical = physOut;
            InfoFile.avg_score_PROMIS_mental = mntlOut;

            String path = FileManager.getInfoFilename(this);
            WriteCSV csv = WriteCSV.getInstance(this);
            csv.modifyCSVInfos_Promis(path, "physical", numberQuestion, skipped_question, "not finished", physOut, false, false, promisQuestionScores, rating);
            csv.modifyCSVInfos_Promis(path, "mental", numberQuestion, skipped_question, "not finished", mntlOut, false, false, promisQuestionScores, rating);

        } else {
            numberQuestion++;
            String path = FileManager.getInfoFilename(this);
            WriteCSV csv = WriteCSV.getInstance(this);
            csv.modifyCSVInfos_Promis(path, categorie, numberQuestion, skipped_question, "not finished", String.valueOf(physicalRawSum), false, false, promisQuestionScores, rating);
            csv.modifyCSVInfos_Promis(path, categorie, numberQuestion, skipped_question, "not finished", String.valueOf(mentalRawSum), false, false, promisQuestionScores, rating);
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
            binding.txtQuestion.setText(R.string.promis_9);
        }else if (numberQuestion ==10) {
            binding.txtQuestion.setText(R.string.promis_10);
        }
    }

    private void displayLegend(){

        // how many choiced does this question have?
        final int count;
        if(numberQuestion == 7) {
            count = 11;
            binding.seekbar.setMin(0);
            binding.seekbar.setMax(10);
        } else {
            count = 5;
        }

        // tell the seekbar how many ticks
        binding.seekbar.setMax(count-1);

        // for each of 11 textViews, either hide or show:
        View[] labels = new View[]{
                binding.txt00, binding.txt0, binding.txt1,
                binding.txt2, binding.txt3, binding.txt4,
                binding.txt5, binding.txt6, binding.txt7,
                binding.txt8, binding.txt9};

        for (int i=0; i < labels.length; i++) {
            labels[i].setVisibility(i < count ? View.VISIBLE : View.GONE);
        }

        if (count == 5 && numberQuestion == 1 || numberQuestion == 2 || numberQuestion == 3 || numberQuestion == 4 || numberQuestion == 5 || numberQuestion == 9) {
            binding.txt00.setText(R.string.promis_legend_1to59_1);
            binding.txt0.setText(R.string.promis_legend_1to59_2);
            binding.txt1.setText(R.string.promis_legend_1to59_3);
            binding.txt2.setText(R.string.promis_legend_1to59_4);
            binding.txt3.setText(R.string.promis_legend_1to59_5);
        } else if (count == 5 && numberQuestion==6) {
            binding.txt00.setText(R.string.promis_legend_6_1);
            binding.txt0.setText(R.string.promis_legend_6_2);
            binding.txt1.setText(R.string.promis_legend_6_3);
            binding.txt2.setText(R.string.promis_legend_6_4);
            binding.txt3.setText(R.string.promis_legend_6_5);
        } else if (count == 5 && numberQuestion==10) {
            binding.txt00.setText(R.string.promis_legend_10_1);
            binding.txt0.setText(R.string.promis_legend_10_2);
            binding.txt1.setText(R.string.promis_legend_10_3);
            binding.txt2.setText(R.string.promis_legend_10_4);
            binding.txt3.setText(R.string.promis_legend_10_5);
        } else if (count == 5 && numberQuestion == 8) {
            binding.txt00.setText(R.string.promis_legend_8_1);
            binding.txt0.setText(R.string.promis_legend_8_2);
            binding.txt1.setText(R.string.promis_legend_8_3);
            binding.txt2.setText(R.string.promis_legend_8_4);
            binding.txt3.setText(R.string.promis_legend_8_5);
        } else if (numberQuestion == 7) {
            binding.txt00.setText(R.string.promis_legend_7_0);
            binding.txt0.setText(R.string.promis_legend_7_1);
            binding.txt1.setText(R.string.promis_legend_7_2);
            binding.txt2.setText(R.string.promis_legend_7_3);
            binding.txt3.setText(R.string.promis_legend_7_4);
            binding.txt4.setText(R.string.promis_legend_7_5);
            binding.txt5.setText(R.string.promis_legend_7_6);
            binding.txt6.setText(R.string.promis_legend_7_7);
            binding.txt7.setText(R.string.promis_legend_7_8);
            binding.txt8.setText(R.string.promis_legend_7_9);
            binding.txt9.setText(R.string.promis_legend_7_10);

        }
    }

    private void modifyCSVInfos(String done, String  score, boolean skip, boolean skip_questionnaire, int numberQuestion){
        String csvFilePath = FileManager.getInfoFilename(this);
        WriteCSV.getInstance(this).modifyCSVInfos_Promis(csvFilePath, categorie, numberQuestion, Integer.parseInt(InfoFile.skipped_question_promis), done, score, skip, skip_questionnaire, promisQuestionScores, rating);
    }

    private void listenSeekbar(){
        binding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (numberQuestion == 7) {
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
        String idPatient = Patient.getPatient().getPatientId();
        String caseID = Patient.getPatient().getCaseId();
        String date = Patient.getPatient().getDate();

        if(!exist_file){
            writeCSVClass.createAndWriteCSV_PROMIS(csv_path, idPatient,caseID, date, String.valueOf(numberQuestion), rating);
        }else{
            writeCSVClass.writeDataCSV_PROMIS(csv_path,  String.valueOf(numberQuestion), rating, promisQuestionScores);
        }
    }
}