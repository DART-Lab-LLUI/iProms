package fr.thomas.menard.iproms.Views;
import static fr.thomas.menard.iproms.Model.InfoFile.*;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.io.File;
import java.util.Locale;

import fr.thomas.menard.iproms.Model.InfoFile;
import fr.thomas.menard.iproms.Model.Patient;
import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.DataTransfer;
import fr.thomas.menard.iproms.Utils.FileManager;
import fr.thomas.menard.iproms.Utils.RankingBarView;
import fr.thomas.menard.iproms.Utils.ReadCSV;
import fr.thomas.menard.iproms.Utils.WriteCSV;
import fr.thomas.menard.iproms.Utils.tScore;
import fr.thomas.menard.iproms.databinding.ActivitySummaryBinding;

public class SummaryActivity extends BaseActivity {

    private ActivitySummaryBinding binding;
    String categorie;
    private void retrieveCategorie(int numberQuestion){
        if(numberQuestion == 3 || numberQuestion == 6 ||numberQuestion == 7 ||numberQuestion == 8)
            categorie = "physical";
        else if(numberQuestion == 2 || numberQuestion == 4||numberQuestion == 5 ||numberQuestion == 10)
            categorie = "mental";
        else
            categorie = "raw";
    }

    private String tscore_promis_physical, tscore_promis_mental;

    private double[][] tscorePromisPhysical = tScore.getScoreTable_promis_physical();
    private double[][] tscorePromisMental = tScore.getScoreTable_promis_mental();

    private File folderSRC;
    private String txt_interpretations;

    private WriteCSV writeCSV;

    private int safeParse(String safe) {
        if (safe == null || safe.isEmpty()) return 0;
        try {
            return Integer.parseInt(safe);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void init(){
        writeCSV = WriteCSV.getInstance(this);
        ReadCSV.retrieveInfos(this);
        initTab();
        initPromisSummary();
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
    }
    private void initPromisSummary() {
        // 1) Sum up your raw‐score buckets
        int rawP = sumPromisRaw("physical");
        int rawM = sumPromisRaw("mental");

        // 2) Turn them into T−scores
        String sP = String.format(Locale.getDefault(), "%.1f",
                tScore.lookupPhsyicalTscore(rawP));
        String sM = String.format(Locale.getDefault(), "%.1f",
                tScore.lookupMentalTscore(rawM));

        Log.d("PROMIS", "SummaryActivity: PhyTxt="
                + InfoFile.avg_score_PROMIS_physical
                + "  MenTxt=" + InfoFile.avg_score_PROMIS_mental);

        // 3) Show the text in your “Score” column
        binding.cellPromisPhysicalScore.setText(avg_score_PROMIS_physical + " / 80");
        binding.cellPromisMentalScore .setText(avg_score_PROMIS_mental   + " / 80");

        // 4) Finally color & size your bars
        //    Use two‐arg displayInterpretations that you add below:
        displayInterpretations("promis_physical", avg_score_PROMIS_physical);
        displayInterpretations("promis_mental",   avg_score_PROMIS_mental);
    }

    private int sumPromisRaw(String domain) {
        int sum = 0;
        // InfoFile.promisQuestionScores[] is a String[] of length 10
        for (int q = 1; q <= 10; q++) {
            retrieveCategorie(q);
            if (categorie.equals(domain)) {
                String raw = InfoFile.promisQuestionScores[q - 1];
                if (raw != null && !raw.isEmpty()) {
                    try {
                        sum += Integer.parseInt(raw);
                    } catch (NumberFormatException e) {
                        // skip bad entries
                    }
                }
            }
        }
        return sum;
    }

    private void promisTscore(){
        int rawPhys = safeParse(avg_score_PROMIS_physical);
        int rawMent = safeParse(avg_score_PROMIS_mental);
        int skipped = safeParse(skipped_question_promis);

        // physical
        if(rawPhys >= 4 && rawPhys <= tscorePromisPhysical.length + 3 && skipped < 4) {
            int idx = rawPhys - 4;
            tscore_promis_physical = String.valueOf(tscorePromisPhysical[idx][1]);
        } else if (skipped == 10) {
            tscore_promis_physical = "0";
        } else {
            tscore_promis_physical = "No t-score";
        }

        // mental
        if (rawMent >= 4 && rawMent <= tscorePromisMental.length + 3 && skipped < 4) {
            int idx = rawMent - 4;
            tscore_promis_mental = String.valueOf(tscorePromisMental[idx][1]);
        } else if (skipped == 10) {
            tscore_promis_mental = "0";
        }else {
            tscore_promis_mental = "No t-score";
        }
    }

    private void listnenBtn(){
        binding.btnOptionnalQuestions.setOnClickListener(v -> {
            navigateToNextActivityWithoutFinish(OptionalQuestionnairesActivity.class);
        });
    }

    private void createResultCSV(){
        if(!FileManager.isResultFileExist(this)){
            double mean_fatigue = (double) Integer.parseInt(avg_score_fatigue) / (Integer.parseInt(questionAnsFatigue) - Integer.parseInt(lastQuestionFatigue) - 1);
            String csv_path = FileManager.getResultFilename(this);
            String idPatient = Patient.getPatient().getPatientId();
            String caseID = Patient.getPatient().getCaseId();
            String date = Patient.getPatient().getDate();

            writeCSV.createAndWriteResult(csv_path, idPatient, caseID, date,
                    String.valueOf(mean_fatigue), fatigueQuestionScores,
                    avg_score_depression, avg_score_anxiety, depressionQuestionScores,
                    score_bdi, bdiQuestionScores,
                    avg_score_PROMIS_physical, avg_score_PROMIS_mental, promisQuestionScores,
                    scoreQOL1, scoreQOL2, scoreQOL3, scoreQOL4, scoreQOL5, scoreQOL6, scoreQOL7, scoreQOL8, scoreQOL9, scoreQOL10, qolQuestionScores,
                    qol1QuestionScores, qol2QuestionScores, qol3QuestionScores, qol4QuestionScores, qol5QuestionScores, qol6QuestionScores, qol7QuestionScores, qol8QuestionScores, qol9QuestionScores, qol10QuestionScores);
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
                txt_interpretations = getString(R.string.skipped_questionniare);
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellResultFatigue.setColorSections(upperlimit, colors);
                binding.cellResultFatigue.setUserScore(2.75f);
                binding.cellResultFatigue.setUserText(txt_interpretations);
            }else{
                double mean_fatigue = (double) Integer.parseInt(avg_score_fatigue) / (Integer.parseInt(questionAnsFatigue) - Integer.parseInt(lastQuestionFatigue) - 1);
                if(mean_fatigue < 4){
                    txt_interpretations = getString(R.string.within_normal_limits);
                }else{
                    txt_interpretations = getString(R.string.impaired);
                }

                float[] upperLimits_fatigue = {(float) (4.0f/1.3), (float) (7.0f/1.3)};
                int[] colors_fatigue = {android.graphics.Color.GREEN, Color.RED};
                binding.cellResultFatigue.setColorSections(upperLimits_fatigue, colors_fatigue);
                binding.cellResultFatigue.setUserText(getString(R.string.impaired));
                binding.cellResultFatigue.setUserScore((float) ((float) mean_fatigue/1.3));
            }


        }

        if(categorie.equals("depression")){
            if(Integer.parseInt(avg_score_depression)==0 && !questionAnsDep.equals("15")){
                txt_interpretations = getString(R.string.skipped_questionniare);
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellDepressionResult.setColorSections(upperlimit, colors);
                binding.cellDepressionResult.setUserScore(2.75f);
                binding.cellDepressionResult.setUserText(txt_interpretations);
            }else{
                if(Integer.parseInt(avg_score_depression)<7){
                    txt_interpretations = getString(R.string.within_normal_limits);

                } else if (Integer.parseInt(avg_score_depression)<11) {
                    txt_interpretations = getString(R.string.borderline_case);

                }
                else{
                    txt_interpretations = getString(R.string.impaired);

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
                txt_interpretations = getString(R.string.skipped_questionniare);
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellAnxietyResult.setColorSections(upperlimit, colors);
                binding.cellAnxietyResult.setUserScore(2.75f);
                binding.cellAnxietyResult.setUserText(txt_interpretations);
            }else{
                if(Integer.parseInt(avg_score_anxiety)<7) {
                    txt_interpretations = getString(R.string.within_normal_limits);

                }else if (Integer.parseInt(avg_score_anxiety)<11) {
                    txt_interpretations = getString(R.string.borderline_case);

                }else{
                    txt_interpretations = getString(R.string.impaired);

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
            if(score_bdi.equals("0") && !questionAnsBDI.equals("21")){
                txt_interpretations = getString(R.string.skipped_questionniare);
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellBDIResult.setColorSections(upperlimit, colors);
                binding.cellBDIResult.setUserScore(2.75f);
                binding.cellBDIResult.setUserText(txt_interpretations);
            }else{
                if(Integer.parseInt(score_bdi)<14){
                    txt_interpretations = getString(R.string.inconspicuous);
                } else if (Integer.parseInt(score_bdi)<20) {
                    txt_interpretations = getString(R.string.mild_depressive);
                } else if (Integer.parseInt(score_bdi)<29) {
                    txt_interpretations = getString(R.string.moderate_depressive);
                }else {
                    txt_interpretations = getString(R.string.severe_depressive);
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

        return txt_interpretations;
    }
    // * new: handles only the two PROMIS rows, given the T-score string
    private void displayInterpretations(String kind, String tscore) {
        boolean isPhysical = kind.equals("promis_physical");
        RankingBarView bar = isPhysical
                ? binding.cellPromisPhysicalResult
                : binding.cellPromisMentalResult;

        // parse out your numeric t-score
        float userT = 0f;
        try {
            userT = Float.parseFloat(tscore);
        } catch (NumberFormatException ignore) { /* handle “No t-score” below */ }

        // decide interpretation
        final String label;
        if (tscore.equals("No t-score") || Integer.parseInt(skipped_question_promis) > 3) {
            label = getString(R.string.skipped_questionniare);
            bar.setColorSections(new float[]{5.5f}, new int[]{0xFF888888});
            bar.setUserScore(2.75f);
        } else {
            // your cut-points for PROMIS T
            if (userT > 70)           label = getString(R.string.very_high);
            else if (userT > 60)      label = getString(R.string.high);
            else if (userT > 40)      label = getString(R.string.average);
            else if (userT > 30)      label = getString(R.string.low);
            else                      label = getString(R.string.very_low);

            // now draw the colored zones & dot
            float min = 20.2f, green=40, yellow=60, red=80;
            int[] colors = { Color.RED, Color.YELLOW, Color.GREEN };
            float[] limits = {
                    bar.rescaleValue(green,  min, red, 70f),
                    bar.rescaleValue(yellow, min, red, 70f),
                    bar.rescaleValue(red,    min, red, 70f)
            };
            bar.setColorSections(limits, colors);
            float userPos = bar.rescaleValue(userT, min, red, 70f);
            bar.setUserScore(userPos);
        }

        // **here** we show the interpretation instead of the raw T-score
        bar.setUserText(label);
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