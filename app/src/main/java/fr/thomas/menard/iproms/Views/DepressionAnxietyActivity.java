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
import fr.thomas.menard.iproms.databinding.ActivityDepressionAnxietyBinding;

public class DepressionAnxietyActivity extends BaseActivity {


    private ActivityDepressionAnxietyBinding binding;
    private String rating, categorie;
    private int numberQuestion;
    private int total_Score;
    private int skipped_question;

    private int questionAns = 0;

    private WriteCSV writeCSVClass;

    private boolean touched = false, redo_questionnaire = false;


    @Override
    public void init(){
        writeCSVClass = WriteCSV.getInstance(this);
        binding.txtIntro.setText(R.string.txt_intro_depression);
        ReadCSV.retrieveInfos(this);
        reinit_questionnaire();
        getQuestion();
        displayText();
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
        questionAns = intent.getIntExtra("questionAnswered", 0);
        redo_questionnaire = intent.getBooleanExtra("redo_questionnaire",false);
    }

    @Override
    public void prepareIntent(Intent intent) {
        super.prepareIntent(intent);
        intent.putExtra("num_question", numberQuestion + 1);
        intent.putExtra("questionAnswered", questionAns + 1);
    }

    private void getQuestion(){
        numberQuestion = Integer.parseInt(InfoFile.questionAnsDep);
        if(numberQuestion==0)
            numberQuestion = 1;

        if(numberQuestion%2==0)
            categorie = "depression";
        else
            categorie = "anxiety";

        if(categorie.equals("depression"))
            total_Score = Integer.parseInt(InfoFile.avg_score_depression);
        else
            total_Score = Integer.parseInt(InfoFile.avg_score_anxiety);

        skipped_question = Integer.parseInt(InfoFile.lastQuestionDep);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    private void finishQuestionnaire(){
        binding.btnSkipQuestionnaire.setOnClickListener(v -> {
            modifyCSVInfos("done", "0", "skip", true, true);
            navigateToNextActivity(MainActivity.class);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            write_csv("exit");
            navigateToNextActivity(MainActivity.class);
            return true;
        } else if (id == R.id.action_skip) {
            skip();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void reinit_questionnaire(){
        if(redo_questionnaire){
            WriteCSV.getInstance(this).reinit_questionnaire_Depression(this);
        }
    }

    private void displayText() {

        Integer questionID = getResources().getIdentifier("question_HADS_" + numberQuestion, "string", getPackageName());
        Integer info0 = getResources().getIdentifier("question_HADS_" + numberQuestion +"_0", "string", getPackageName());
        Integer info1 = getResources().getIdentifier("question_HADS_" + numberQuestion +"_1", "string", getPackageName());
        Integer info2 = getResources().getIdentifier("question_HADS_" + numberQuestion +"_2", "string", getPackageName());
        Integer info3 = getResources().getIdentifier("question_HADS_" + numberQuestion +"_3", "string", getPackageName());

        binding.txtQuestion.setText(getString(questionID));
        binding.txtinfo0.setText(getString(info0));
        binding.txtinfo1.setText(getString(info1));
        binding.txtinfo2.setText(getString(info2));
        binding.txtinfo3.setText(getString(info3));

        int pourcentage = 100 * numberQuestion / 14;
        binding.txtPoucentageDoneDep.setText(String.valueOf(pourcentage));

        Log.d("TEST", "number question" + numberQuestion + pourcentage);

    }

    private void listenBtnConfirm(){
        binding.btnConfirm.setOnClickListener(v -> {
            write_csv(rating);
            total_Score += Integer.parseInt(rating);

            Intent intent;
            if(numberQuestion==14){
                modifyCSVInfos("done", String.valueOf(total_Score), categorie, false, false);
                navigateToNextActivity(MainActivity.class);
            }else {
                modifyCSVInfos("not finished", String.valueOf(total_Score), categorie, false, false);
                navigateToNextActivityWithoutFinish(DepressionAnxietyActivity.class);
            }
        });
    }


    private void listenBtnSkip(){
        binding.btnSkip.setOnClickListener(v -> skip());
    }
    private void skip(){
        write_csv("skip");
        Intent intent;

        if(numberQuestion==14){
            modifyCSVInfos("done", String.valueOf(total_Score), categorie, true, false);
            navigateToNextActivity(MainActivity.class);
        }else {
            modifyCSVInfos("not finished", String.valueOf(total_Score), categorie, true, false);
            navigateToNextActivityWithoutFinish(DepressionAnxietyActivity.class);
        }
    }


    private void modifyCSVInfos(String done, String  score, String category, boolean skip, boolean skip_questionnaire){
        WriteCSV.getInstance(this).modifyCSVInfos_Depression(this, numberQuestion, skipped_question, done, score, category, skip, skip_questionnaire);
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

    private void write_csv(String rating){
        String csv_path = FileManager.getHADSFilename(this);
        String idPatient = Patient.getPatient().getPatientId(this);
        String caseID = Patient.getPatient().getCaseId(this);
        String date = Patient.getPatient().getDate(this);

        if(!FileManager.isHADSFileExist(this)){
            writeCSVClass.createAndWriteCSV_fatigue(csv_path, idPatient,caseID, date, String.valueOf(numberQuestion), rating);
        }else{
            writeCSVClass.writeDataCSV_fatigue(csv_path, String.valueOf(numberQuestion), rating);
        }
    }

}