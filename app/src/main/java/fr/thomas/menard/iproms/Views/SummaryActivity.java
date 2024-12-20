package fr.thomas.menard.iproms.Views;
import static fr.thomas.menard.iproms.Model.InfoFile.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.io.File;

import fr.thomas.menard.iproms.Model.MyApplication;
import fr.thomas.menard.iproms.Model.Patient;
import fr.thomas.menard.iproms.Utils.DataTransfer;
import fr.thomas.menard.iproms.Utils.FileManager;
import fr.thomas.menard.iproms.Utils.LocaleHelper;
import fr.thomas.menard.iproms.Utils.ReadCSV;
import fr.thomas.menard.iproms.Utils.WriteCSV;
import fr.thomas.menard.iproms.Utils.tScore;
import fr.thomas.menard.iproms.databinding.ActivitySummaryBinding;

public class SummaryActivity extends BaseActivity {

    private ActivitySummaryBinding binding;

    private String tscore_promis_physical, tscore_promis_mental;

    private double[][] tscorePromisPhysical = tScore.getScoreTable_promis_physical();
    private double[][] tscorePromisMental = tScore.getScoreTable_promis_mental();

    private File folderSRC;
    private String txt_interpretations;

    private WriteCSV writeCSV;


    @Override
    public void init(){
        writeCSV = WriteCSV.getInstance(this);
        ReadCSV.retrieveInfos(this);
        promisTscore();
        initTab();
        createResultCSV();
        uploadResultCSV();
        checkScoreforOthersQuestionnaires();
    }

    @Override
    public void listenBtn() {
        listnenBtn();
    }

    @Override
    public void setBinding() {
        binding = ActivitySummaryBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    private void initTab(){
        double mean_fatigue = (double) Integer.parseInt(avg_score_fatigue) / (Integer.parseInt(questionAnsFatigue) - Integer.parseInt(lastQuestionFatigue) - 1);
        String mean_sfatigue = String.valueOf(Math.round(mean_fatigue));
        binding.cellScoreFatiue.setText(mean_sfatigue+ " / 7");
        displayInterpretations("fatigue");
        binding.cellDepressionScore.setText(avg_score_depression + " / 21");
        displayInterpretations("depression");
        binding.cellAnxietyScore.setText(avg_score_anxiety+ " / 21");
        displayInterpretations("anxiety");
        binding.cellBDIScore.setText(score_bdi+" / 63");
        displayInterpretations("bdi");
        binding.cellPromisPhysicalScore.setText(avg_score_PROMIS_physical + " / 20");
        displayInterpretations("promis");
        binding.cellPromisMentalScore.setText(avg_score_PROMIS_mental + " / 20");
        displayInterpretations("promis");
    }

    private void listnenBtn(){
        binding.btnOptionnalQuestions.setOnClickListener(v -> {
            navigateToNextActivityWithoutFinish(OptionalQuestionnairesActivity.class);
        });
    }

    private void promisTscore(){
        if(!avg_score_PROMIS_physical.equals("0") && Integer.parseInt(skipped_question_promis) < 4){
            if (Integer.parseInt(skipped_question_promis) == 10) {
                tscore_promis_physical = "0";

            }else {
                Log.d("TEST", "tes " + tscorePromisPhysical[10][1]);
                tscore_promis_physical = String.valueOf(tscorePromisPhysical[Integer.parseInt(avg_score_PROMIS_physical) - 4 ][1]);

            }

        }else{
            tscore_promis_physical = "No t-score";
        }

        if(!avg_score_PROMIS_mental.equals("0") && Integer.parseInt(skipped_question_promis) < 4){
            if (Integer.parseInt(skipped_question_promis) == 10) {
                tscore_promis_mental = "0";

            }else {
                tscore_promis_mental = String.valueOf(tscorePromisMental[Integer.parseInt(avg_score_PROMIS_mental ) - 4 ][1]);

            }

        }else{
            tscore_promis_mental = "No t-score";
        }
    }

    private void createResultCSV(){
        if(!FileManager.isResultFileExist(this)){
            double mean_fatigue = (double) Integer.parseInt(avg_score_fatigue) / (Integer.parseInt(questionAnsFatigue) - Integer.parseInt(lastQuestionFatigue) - 1);
            String csv_path = FileManager.getResultFilename(this);
            String idPatient = Patient.getPatient().getPatientId();
            String caseID = Patient.getPatient().getCaseId();
            String date = Patient.getPatient().getDate();

            writeCSV.createAndWriteResult(csv_path, idPatient, caseID, date,
                    String.valueOf(mean_fatigue),
                    avg_score_depression, avg_score_anxiety, score_bdi,
                    avg_score_PROMIS_physical, avg_score_PROMIS_mental, scoreQOL1, scoreQOL2, scoreQOL3, scoreQOL4,
                    scoreQOL5, scoreQOL7, scoreQOL8, scoreQOL9, scoreQOL10);
        }
    }

    private void uploadResultCSV(){
        uploadData(FileManager.getResultFile(this));
        binding.linearUpload.setVisibility(View.VISIBLE);
    }



    @SuppressLint("ResourceAsColor")
    private String displayInterpretations(String categorie){

        if(categorie.equals("fatigue")){
            if(avg_score_fatigue.equals("0")){
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellResultFatigue.setColorSections(upperlimit, colors);
                binding.cellResultFatigue.setUserScore(2.75f);
                binding.cellResultFatigue.setUserText(txt_interpretations);
            }else{
                double mean_fatigue = (double) Integer.parseInt(avg_score_fatigue) / (Integer.parseInt(questionAnsFatigue) - Integer.parseInt(lastQuestionFatigue) - 1);
                if(mean_fatigue < 4){
                    txt_interpretations = "Within normal limits";
                }else{
                    txt_interpretations = "Impaired";
                }

                float[] upperLimits_fatigue = {(float) (4.0f/1.3), (float) (7.0f/1.3)};
                int[] colors_fatigue = {android.graphics.Color.GREEN, Color.RED};
                binding.cellResultFatigue.setColorSections(upperLimits_fatigue, colors_fatigue);
                binding.cellResultFatigue.setUserText("Impaired");
                binding.cellResultFatigue.setUserScore((float) ((float) mean_fatigue/1.3));
            }


        }

        if(categorie.equals("depression")){
            if(Integer.parseInt(avg_score_depression)==0 && !questionAnsDep.equals("15")){
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellDepressionResult.setColorSections(upperlimit, colors);
                binding.cellDepressionResult.setUserScore(2.75f);
                binding.cellDepressionResult.setUserText(txt_interpretations);
            }else{
                if(Integer.parseInt(avg_score_depression)<7){
                    txt_interpretations = "Within normal limits";

                } else if (Integer.parseInt(avg_score_depression)<11) {
                    txt_interpretations = "Bordeling case";

                }
                else{
                    txt_interpretations = "Impaired";

                }

                float[] upperLimits_dep = {(float) (7.0f/4), (float) (11.0f/4), (float) (21.0f/4)};
                int[] colors_dep = {android.graphics.Color.GREEN, Color.YELLOW, Color.RED};
                binding.cellDepressionResult.setColorSections(upperLimits_dep, colors_dep);
                binding.cellDepressionResult.setUserScore((float) ((float) Integer.parseInt(avg_score_depression)/4));
                binding.cellDepressionResult.setUserText(txt_interpretations);
            }

        }


        if(categorie.equals("anxiety")){
            if(avg_score_anxiety.equals("0") && !questionAnsDep.equals("15")){
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellAnxietyResult.setColorSections(upperlimit, colors);
                binding.cellAnxietyResult.setUserScore(2.75f);
                binding.cellAnxietyResult.setUserText(txt_interpretations);
            }else{
                if(Integer.parseInt(avg_score_anxiety)<7) {
                    txt_interpretations = "Within normal limits";

                }else if (Integer.parseInt(avg_score_anxiety)<11) {
                    txt_interpretations = "Bordeling case";

                }else{
                    txt_interpretations = "Impaired";

                }
                float[] upperLimits_dep = {(float) (7.0f/4), (float) (11.0f/4), (float) (21.0f/4)};
                int[] colors_dep = {android.graphics.Color.GREEN, Color.YELLOW, Color.RED};
                binding.cellAnxietyResult.setColorSections(upperLimits_dep, colors_dep);
                binding.cellAnxietyResult.setUserScore((float) ((float) Integer.parseInt(avg_score_anxiety)/4));
                binding.cellAnxietyResult.setUserText(txt_interpretations);
            }

        }

        if(categorie.equals("bdi")){
            String txt_interpretations;
            if(score_bdi.equals("0") && !questionAnsBDI.equals("22")){
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellBDIResult.setColorSections(upperlimit, colors);
                binding.cellBDIResult.setUserScore(2.75f);
                binding.cellBDIResult.setUserText(txt_interpretations);
            }else{
                if(Integer.parseInt(score_bdi)<14){
                    txt_interpretations = "unauffällig";
                } else if (Integer.parseInt(score_bdi)<20) {
                    txt_interpretations = "milde depressive";
                } else if (Integer.parseInt(score_bdi)<29) {
                    txt_interpretations = "moderate depressive";
                }else {
                    txt_interpretations = "schwere depressive";
                }

                int[] colors = {android.graphics.Color.GREEN, Color.YELLOW,Color.rgb(255,165,0), Color.RED};

                float min = 0.0f;
                float greenEnd = 14.0f;
                float yellowEnd = 20.0f;
                float orangeEnd = 29.0f;
                float redEnd = 63.0f;

                float[] upperlimit = {binding.cellBDIResult.rescaleValue(greenEnd,min,redEnd,  40.0f),
                        binding.cellBDIResult.rescaleValue(yellowEnd, min,redEnd, 40.0f),
                        binding.cellBDIResult.rescaleValue(orangeEnd, min,redEnd, 40.0f),
                        binding.cellBDIResult.rescaleValue(redEnd, min,redEnd, 40.0f)};

                float user_score = Float.parseFloat(score_bdi);
                float user_score_rescale = binding.cellBDIResult.rescaleValue(user_score,min,redEnd, 40.0f);

                binding.cellBDIResult.setColorSections(upperlimit, colors);
                binding.cellBDIResult.setUserScore(user_score_rescale);
                binding.cellBDIResult.setUserText(txt_interpretations);
            }

        }



        if(categorie.equals("promis")) {
            if (tscore_promis_physical.equals("No t-score") || Integer.parseInt(skipped_question_promis)>3) {
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellPromisPhysicalResult.setColorSections(upperlimit, colors);
                binding.cellPromisPhysicalResult.setUserScore(2.75f);
                binding.cellPromisPhysicalResult.setUserText(txt_interpretations);

            } else {
                if (Double.parseDouble(tscore_promis_physical) > 70) {
                    txt_interpretations = "Very high";
                } else if (Double.parseDouble(tscore_promis_physical) < 71 && Double.parseDouble(tscore_promis_physical) > 60) {
                    txt_interpretations = "High";

                } else if (Double.parseDouble(tscore_promis_physical) < 61 && Double.parseDouble(tscore_promis_physical) > 40) {
                    txt_interpretations = "Average";

                } else if (Double.parseDouble(tscore_promis_physical) < 41 && Double.parseDouble(tscore_promis_physical) > 30) {
                    txt_interpretations = "Low";
                }
                else if (Double.parseDouble(tscore_promis_physical) < 31) {
                    txt_interpretations = "Very low";

                }
                int[] colors = { Color.RED, Color.YELLOW, android.graphics.Color.GREEN};

                float min = 20.2f;
                float greenEnd = 40.0f;
                float yellowEnd = 60.0f;
                float redEnd = 80.0f;

                float[] upperlimit = {binding.cellPromisMentalResult.rescaleValue(greenEnd,min, redEnd, 70.0f),
                        binding.cellPromisMentalResult.rescaleValue(yellowEnd, min, redEnd,70.0f),
                        binding.cellPromisMentalResult.rescaleValue(redEnd, min, redEnd,70.0f)};

                float user_score = Float.parseFloat(tscore_promis_physical);
                float user_score_rescale = binding.cellPromisPhysicalResult.rescaleValue(user_score, min, redEnd,70.0f);

                binding.cellPromisPhysicalResult.setColorSections(upperlimit, colors);
                binding.cellPromisPhysicalResult.setUserScore(user_score_rescale);
                binding.cellPromisPhysicalResult.setUserText(txt_interpretations);

            }

            if (tscore_promis_mental.equals("No t-score") || Integer.parseInt(skipped_question_promis)>3) {
                txt_interpretations = "Questionnaire skipped";
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellPromisMentalResult.setColorSections(upperlimit, colors);
                binding.cellPromisMentalResult.setUserScore(2.75f);
                binding.cellPromisMentalResult.setUserText(txt_interpretations);

            } else {
                if (Double.parseDouble(tscore_promis_mental) > 70) {
                    txt_interpretations = "Very high";
                } else if (Double.parseDouble(tscore_promis_mental) < 71 && Double.parseDouble(tscore_promis_mental) > 60) {
                    txt_interpretations = "High";

                } else if (Double.parseDouble(tscore_promis_mental) < 61 && Double.parseDouble(tscore_promis_mental) > 40) {
                    txt_interpretations = "Average";

                } else if (Double.parseDouble(tscore_promis_mental) < 41 && Double.parseDouble(tscore_promis_mental) > 30) {
                    txt_interpretations = "Low";
                }
                else if (Double.parseDouble(tscore_promis_mental) < 31) {
                    txt_interpretations = "Very low";

                }
                int[] colors = { Color.RED, Color.YELLOW, android.graphics.Color.GREEN};

                float min = 20.2f;
                float greenEnd = 40.0f;
                float yellowEnd = 60.0f;
                float redEnd = 80.0f;

                float[] upperlimit = {binding.cellPromisMentalResult.rescaleValue(greenEnd,min, redEnd, 70.0f),
                        binding.cellPromisMentalResult.rescaleValue(yellowEnd, min, redEnd,70.0f),
                        binding.cellPromisMentalResult.rescaleValue(redEnd, min, redEnd,70.0f)};

                float user_score = Float.parseFloat(tscore_promis_mental);
                float user_score_rescale = binding.cellPromisMentalResult.rescaleValue(user_score, min, redEnd,70.0f);

                binding.cellPromisMentalResult.setColorSections(upperlimit, colors);
                binding.cellPromisMentalResult.setUserScore(user_score_rescale);
                binding.cellPromisMentalResult.setUserText(txt_interpretations);

            }
        }

        return txt_interpretations;
    }


    private void checkScoreforOthersQuestionnaires() {
        if ((Integer.parseInt(avg_score_fatigue) / 9 > 3) || (Integer.parseInt(avg_score_depression) > 7) || (Integer.parseInt(avg_score_anxiety) > 7)) {
            binding.txtDescriptionOthers.setVisibility(View.VISIBLE);
        } else {
            binding.txtSummaryDescription.setVisibility(View.VISIBLE);
        }
    }

    private void uploadData(File file){
        DataTransfer dataTransfer = new DataTransfer(this);
        dataTransfer.uploadFile(file);
    }
}