package fr.thomas.menard.iproms.Views;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import fr.thomas.menard.iproms.Model.InfoFile;
import fr.thomas.menard.iproms.Model.Patient;
import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.FileManager;
import fr.thomas.menard.iproms.Utils.ReadCSV;
import fr.thomas.menard.iproms.Utils.WriteCSV;
import fr.thomas.menard.iproms.databinding.ActivityQualityofLifeBinding;

public class QualityofLifeActivity extends BaseActivity {

    private ActivityQualityofLifeBinding binding;

    private String rating;
    private int numberQuestion;
    private int total_Score;
    private int total_score_qol;
    private int numberQuestion_qol;
    private int skipped_question;

    private int questionAns = 0, question_general_Ans;

    private boolean touched = false, redo_questionnaire = false;

    private String qol;

    private String[] qolQuestionScore = new String[10];
    private String[] qol1QuestionScores = new String[8];
    private String[] qol2QuestionScores = new String[8];
    private String[] qol3QuestionScores = new String[8];
    private String[] qol4QuestionScores = new String[8];
    private String[] qol5QuestionScores = new String[8];
    private String[] qol6QuestionScores = new String[8];
    private String[] qol7QuestionScores = new String[9];
    private String[] qol8QuestionScores = new String[8];
    private String[] qol9QuestionScores = new String[8];
    private String[] qol10QuestionScores = new String[8];

    private WriteCSV writeCSVClass;

    @Override
    public void init(){

        writeCSVClass = WriteCSV.getInstance(this);
        binding.txtIntro.setText(R.string.txt_intro_depression);

        reinit_questionnaire();
        retrieveGeneralInfos();
        retrieveInfos(qol);
        displayText();
    }

    @Override
    public void onStart() {
        super.onStart();
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
        binding = ActivityQualityofLifeBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    @Override
    public void processReceivedIntent(Intent intent) {
        super.processReceivedIntent(intent);
        total_Score = intent.getIntExtra("totalScore", 0);
        questionAns = intent.getIntExtra("questionAnswered", 0);
        qol = intent.getStringExtra("qol");
        redo_questionnaire = intent.getBooleanExtra("redo_questionnaire",false);
    }

    @Override
    public void prepareIntent(Intent intent) {
        super.prepareIntent(intent);
        intent.putExtra("totalScore", total_score_qol);
        intent.putExtra("questionAnswered", questionAns);
        intent.putExtra("qol", qol);
    }

    private void reinit_questionnaire(){
        if(redo_questionnaire) {
            WriteCSV.getInstance(this).reinit_questionnaire_QQL(this);

            // reinitialize the individual scores
            for (int i = 0; i < qolQuestionScore.length; i++) {
                qolQuestionScore[i] = "0";
            }
            for (int i = 0; i < qol1QuestionScores.length; i++) {
                qol1QuestionScores[i] = "0";
            }
            for (int i = 0; i < qol2QuestionScores.length; i++) {
                qol2QuestionScores[i] = "0";
            }
            for (int i = 0; i < qol3QuestionScores.length; i++) {
                qol3QuestionScores[i] = "0";
            }
            for (int i = 0; i < qol4QuestionScores.length; i++) {
                qol4QuestionScores[i] = "0";
            }
            for (int i = 0; i < qol5QuestionScores.length; i++) {
                qol5QuestionScores[i] = "0";
            }
            for (int i = 0; i < qol6QuestionScores.length; i++) {
                qol6QuestionScores[i] = "0";
            }
            for (int i = 0; i < qol7QuestionScores.length; i++) {
                qol7QuestionScores[i] = "0";
            }
            for (int i = 0; i < qol8QuestionScores.length; i++) {
                qol8QuestionScores[i] = "0";
            }
            for (int i = 0; i < qol9QuestionScores.length; i++) {
                qol9QuestionScores[i] = "0";
            }
            for (int i = 0; i < qol10QuestionScores.length; i++) {
                qol10QuestionScores[i] = "0";
            }
        }

            /* String csvFilePath = FileManager.getInfoFilename(this);

            try {
                CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

                // Create a CSVReader with FileReader and custom CSVParser
                CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                        .withCSVParser(csvParser)
                        .build();

                int qolColumnIndex = 21;

                switch (qol) {
                    case "qol1":
                        qolColumnIndex += 4;
                        break;
                    case "qol2":
                        qolColumnIndex += 8;
                        break;
                    case "qol3":
                        qolColumnIndex += 12;
                        break;
                    case "qol4":
                        qolColumnIndex += 16;
                        break;
                    case "qol5":
                        qolColumnIndex += 20;
                        break;
                    case "qol6":
                        qolColumnIndex += 24;
                        break;
                    case "qol7":
                        qolColumnIndex += 28;
                        break;
                    case "qol8":
                        qolColumnIndex += 32;
                        break;
                    case "qol9":
                        qolColumnIndex += 36;
                        break;
                    case "qol10":
                        qolColumnIndex += 40;
                        break;
                }
                List<String[]> csvEntries = reader.readAll();
                String[] row = csvEntries.get(1);
                row[qolColumnIndex] = "null";
                row[qolColumnIndex+1] = "0";
                row[qolColumnIndex + 2] = "0";
                row[qolColumnIndex + 3] = "0";

                if(qol.equals("qol7"))
                    row[15] = String.valueOf(Integer.parseInt(row[15]) - 9);
                else{
                    row[15] = String.valueOf(Integer.parseInt(row[15]) - 8);
                }
                if(Integer.parseInt(row[15])<0){
                    row[15] = "0";
                }

                CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
                writer.writeAll(csvEntries);
                writer.close();


                reader.close();

            } catch (IOException | CsvException e) {
                Log.d("TEST", "infos " + e.getMessage());
                e.printStackTrace();
            } */
    }

    private void finishQuestionnaire(){
        binding.btnSkipQuestionnaire.setOnClickListener(v -> {
            modifyCSVInfos("done", "0", qol, true, true, numberQuestion);
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
            write_csv("exit");
            navigateToNextActivity(OptionalQuestionnairesActivity.class);
            return true;
        } else if (id == R.id.action_skip) {
            skip();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void retrieveGeneralInfos(){
        ReadCSV.retrieveInfos(this);
        question_general_Ans = Integer.parseInt(InfoFile.questionAnsQol);
        total_Score = Integer.parseInt(InfoFile.avg_score_qol);
        skipped_question = Integer.parseInt(InfoFile.skippedQuestionQOL);

        if(question_general_Ans == 0)
            question_general_Ans = 1;
        numberQuestion_qol = question_general_Ans;


        int pourcentage = 100 * numberQuestion_qol / 73;
        binding.txtPoucentageDoneQOL.setText(String.valueOf(pourcentage));

        Log.d("TEST", "retrieveInfos() - numberQuestion: " + numberQuestion + " - skipped_question" + skipped_question);
    }

    private void retrieveInfos(String qol){
        String csvFilePath = FileManager.getInfoFilename(this);
        int qolColumnIndex = 21;

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            switch (qol) {
                case "qol1":
                    qolColumnIndex += 4;
                    break;
                case "qol2":
                    qolColumnIndex += 8;
                    break;
                case "qol3":
                    qolColumnIndex += 12;
                    break;
                case "qol4":
                    qolColumnIndex += 16;
                    break;
                case "qol5":
                    qolColumnIndex += 20;
                    break;
                case "qol7":
                    qolColumnIndex += 28;
                    break;
                case "qol8":
                    qolColumnIndex += 32;
                    break;
                case "qol9":
                    qolColumnIndex += 36;
                    break;
                case "qol10":
                    qolColumnIndex += 40;
                    break;
            }


            List<String[]> csvEntries = reader.readAll();
            String[] firstRow = csvEntries.get(1);

            total_score_qol = Integer.parseInt(firstRow[qolColumnIndex+1]);
            numberQuestion = Integer.parseInt((firstRow[qolColumnIndex+2]));

            if(numberQuestion==0)
                numberQuestion = 1;

            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayText(){
        Integer questionID = getResources().getIdentifier("question_"+qol+"_" + numberQuestion, "string", getPackageName());
        binding.txtQuestion.setText(getString(questionID));
        if(qol.equals("qol1")){
            binding.txtDomain.setText(R.string.emotional_behavioral_dyscontrol);
            binding.txtContext.setText(R.string.in_the_past_7_days);
        } else if (qol.equals("qol2")) {
            binding.txtDomain.setText(R.string.sleep_disturbance);
            binding.txtContext.setText(R.string.in_the_past_7_days);
        }else if (qol.equals("qol3")) {
            binding.txtDomain.setText(R.string.ability_in_social_roles);
            binding.txtContext.setText(R.string.in_the_past_7_days);
        }else if (qol.equals("qol4")) {
            binding.txtDomain.setText(R.string.satisfaction_with_social_act);
            binding.txtContext.setText(R.string.in_the_past_7_days);
            binding.txtinfo0.setText(R.string.satisfaction_info_0);
            binding.txtinfo1.setText(R.string.satisfaction_info_1);
            binding.txtinfo2.setText(R.string.satisfaction_info_2);
            binding.txtinfo3.setText(R.string.satisfaction_info_3);
            binding.txtinfo4.setText(R.string.satisfaction_info_4);
        }else if (qol.equals("qol5")) {
            binding.txtDomain.setText(R.string.applied_cognition_general_concerns);
            if(numberQuestion<3){
                binding.txtContext.setText(R.string.in_the_past_7_days);
            }else{
                binding.txtContext.setText(R.string.how_difficult);
                binding.txtinfo0.setText(R.string.applied_exec_function_info_1);
                binding.txtinfo1.setText(R.string.applied_exec_function_info_2);
                binding.txtinfo2.setText(R.string.applied_exec_function_info_3);
                binding.txtinfo3.setText(R.string.applied_exec_function_info_4);
                binding.txtinfo4.setText(R.string.applied_exec_function_info_5);
            }

        }else if (qol.equals("qol7")) {
            binding.txtDomain.setText(R.string.positiv_effect_well_being);
            binding.txtContext.setText(R.string.lately);
        }else if (qol.equals("qol8")) {
            binding.txtDomain.setText(R.string.applied_cognition_executive_function);
            binding.txtContext.setText(R.string.lately);
        }else if (qol.equals("qol9")) {
            binding.txtDomain.setText(R.string.upper_function);
            binding.txtContext.setText("");
            binding.txtinfo0.setText(R.string.upper_info_1);
            binding.txtinfo1.setText(R.string.uppper_info_2);
            binding.txtinfo2.setText(R.string.uppper_info_3);
            binding.txtinfo3.setText(R.string.uppper_info_4);
            binding.txtinfo4.setText(R.string.uppper_info_5);
        }else if (qol.equals("qol10")) {
            binding.txtDomain.setText(R.string.lower_function);
            binding.txtContext.setText("");
            binding.txtinfo0.setText(R.string.upper_info_1);
            binding.txtinfo1.setText(R.string.uppper_info_2);
            binding.txtinfo2.setText(R.string.uppper_info_3);
            binding.txtinfo3.setText(R.string.uppper_info_4);
            binding.txtinfo4.setText(R.string.uppper_info_5);
        }
    }


    private void listenBtnConfirm(){
        Log.d("TEST", "listenBtnConfirm() - numberQuestion (before): " + numberQuestion_qol + " - skipped_question" + skipped_question);
        binding.btnConfirm.setOnClickListener(v -> {
            numberQuestion_qol += 1;
            write_csv(rating);
            total_score_qol = total_score_qol + Integer.parseInt(rating);
            total_Score = total_Score + Integer.parseInt(rating);
            questionAns = questionAns + 1;


            modifyCSVGeneralQOL(String.valueOf(total_Score), String.valueOf(question_general_Ans +1), false);
            Log.d("TEST", "log" + numberQuestion_qol + "_" + qol);
            //81 question
            if(qol.equals("qol7")){
                if(numberQuestion==9){
                    Log.d("TEST", "listenBtnConfirm() - numberQuestion (before): " + numberQuestion + " - skipped_question" + skipped_question);
                    modifyCSVInfos("done", String.valueOf(total_score_qol), qol, false, false, numberQuestion);
                    navigateToNextActivity(OptionalQuestionnairesActivity.class);
                }
                else {
                    numberQuestion++;
                    Log.d("TEST", "skip() - numberQuestion (after): " + numberQuestion + " - skipped_question" + skipped_question);
                    Log.d("TEST", "skip() - modifyCSVInfos() - numberQuestion: " + numberQuestion + " - skipped_question" + skipped_question);
                    modifyCSVInfos("not finished", String.valueOf(total_score_qol), qol, false, false, numberQuestion);
                    navigateToNextActivity(QualityofLifeActivity.class);
                }
            }else{
                if(numberQuestion==8) {
                    Log.d("TEST", "skip() - numberQuestion(after): " + numberQuestion + " - skipped_question" + skipped_question);
                    Log.d("TEST", "skip() - modifyCSVInfos() - numberQuestion: " + numberQuestion + " - skipped_question" + skipped_question);
                    modifyCSVInfos("done", String.valueOf(total_score_qol), qol, false, false, numberQuestion);
                    navigateToNextActivity(OptionalQuestionnairesActivity.class);
                } else {
                    modifyCSVInfos("not finished", String.valueOf(total_score_qol), qol, false, false, numberQuestion);
                    numberQuestion++;
                    navigateToNextActivity(QualityofLifeActivity.class);
                }
            }
        });
    }

    private void listenBtnSkip(){
        binding.btnSkip.setOnClickListener(v -> skip());
    }

    private void skip(){
        skipped_question++;
        Log.d("TEST", "skip() - numberQuestion (before): " + numberQuestion);
        write_csv("skip");
        numberQuestion_qol += 1;
        modifyCSVGeneralQOL(String.valueOf(total_Score), String.valueOf(question_general_Ans + 1), true);
        if(qol.equals("qol7")){
            if(numberQuestion==9) {
                modifyCSVInfos("done", String.valueOf(total_score_qol), qol, true, false, numberQuestion);
                navigateToNextActivity(OptionalQuestionnairesActivity.class);
            } else {
                numberQuestion++;
                Log.d("TEST", "skip() - numberQuestion (after): " + numberQuestion);
                Log.d("TEST", "skip() - modifyCSVInfos() - numberQuestion: " + numberQuestion);
                modifyCSVInfos("not finished", String.valueOf(total_score_qol), qol, true, false, numberQuestion);
                navigateToNextActivity(QualityofLifeActivity.class);
            }
        }else{
            if(numberQuestion==8){
                modifyCSVInfos("done", String.valueOf(total_score_qol), qol, true, false, numberQuestion);
                navigateToNextActivity(OptionalQuestionnairesActivity.class);
            } else {
                numberQuestion++;
                Log.d("TEST", "skip() - numberQuestion (after): " + numberQuestion);
                Log.d("TEST", "skip() - modifyCSVInfos() - numberQuestion: " + numberQuestion);
                modifyCSVInfos("not finished", String.valueOf(total_score_qol), qol, true, false, numberQuestion);

                navigateToNextActivity(QualityofLifeActivity.class);
            }
        }
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


    private void modifyCSVInfos(String done, String  score, String qol, boolean skip, boolean skip_questionnaire, int numberQuestion){
        if(!skip_questionnaire) {
            WriteCSV.getInstance(this).modifyCSVInfos_QQL(this, numberQuestion, skipped_question, done, score, qol, skip, skip_questionnaire, qolQuestionScore, rating);
        }
    }

    private void modifyCSVGeneralQOL(String  score, String answered, boolean skip){
        WriteCSV.getInstance(this).modifyCSVGeneralQOL(this, skipped_question, "not finished", score, answered, skip, qolQuestionScore);
    }


    private void write_csv(String rating){
        String full_qol;
        switch (qol) {
            case "qol1":
                full_qol = "Emotional and Behavioral Dyscontrol";
                break;
            case "qol2":
                full_qol ="Sleep Disturbance";
                break;
            case "qol3":
                full_qol = "Ability to Participate in Social Roles and Activities";
                break;
            case "qol4":
                full_qol = "Satisfaction with Social Roles and Activities";
                break;
            case "qol5":
                full_qol = "Cognitive function";
                break;
            case "qol7":
                full_qol = "Positive Affect and Well-Being";
                break;
            case "qol8":
                full_qol = "Stigma";
                break;
            case "qol9":
                full_qol = "Upper Extremity Function";
                break;
            case "qol10":
                full_qol = "Lower Extremity Function";
                break;
            default:
                full_qol = "NONE";
                break;
        }

        String csv_path = FileManager.getQQLFilename(this);
        boolean exist_file = FileManager.isQQLFileExist(this);
        String idPatient = Patient.getPatient().getPatientId();
        String caseID = Patient.getPatient().getCaseId();
        String date = Patient.getPatient().getDate();

        if(!exist_file){
            writeCSVClass.createAndWriteCSV_QOL(csv_path, idPatient,caseID, date, full_qol, String.valueOf(numberQuestion_qol + 1), rating);
        }else{
            writeCSVClass.writeDataCSV_QOL(csv_path, full_qol, String.valueOf(numberQuestion_qol + 1), rating);
        }
    }


}