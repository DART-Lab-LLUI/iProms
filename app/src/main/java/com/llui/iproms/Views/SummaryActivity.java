package com.llui.iproms.Views;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import java.util.Locale;

import com.llui.iproms.Enum.QuestionnaireType;
import com.llui.iproms.Questionnaires.BDIQuestionnaire;
import com.llui.iproms.Questionnaires.DepressionAnxietyQuestionnaire;
import com.llui.iproms.Questionnaires.FatigueQuestionnaire;
import com.llui.iproms.Questionnaires.PromisQuestionnaire;
import com.llui.iproms.Model.Questionnaires;
import com.llui.iproms.R;
import com.llui.iproms.Utils.RankingBarView;
import com.llui.iproms.Utils.tScore;
import com.llui.iproms.databinding.ActivitySummaryBinding;

public class SummaryActivity extends BaseActivity {

    private ActivitySummaryBinding binding;
    private String txt_interpretations;

    @Override
    public void init(){
        initTab();
        initPromisSummary();
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
        FatigueQuestionnaire fatigueQuestionnaire = (FatigueQuestionnaire) Questionnaires.get(this, QuestionnaireType.FATIGUE);
        DepressionAnxietyQuestionnaire depressionAnxietyQuestionnaire = (DepressionAnxietyQuestionnaire) Questionnaires.get(this, QuestionnaireType.DEPRESSIONANXIETY);
        BDIQuestionnaire bdiQuestionnaire = (BDIQuestionnaire) Questionnaires.get(this, QuestionnaireType.BDI);


        double mean_fatigue = (double) fatigueQuestionnaire.getScore() / (fatigueQuestionnaire.getAnsweredQues());
        String mean_sfatigue = String.valueOf(Math.round(mean_fatigue));
        binding.cellScoreFatiue.setText(mean_sfatigue+ " / 7");
        displayInterpretations("fatigue");
        binding.cellDepressionScore.setText(depressionAnxietyQuestionnaire.getDepressionScore() + " / 21");
        displayInterpretations("depression");
        binding.cellAnxietyScore.setText(depressionAnxietyQuestionnaire.getAnxietyScore()+ " / 21");
        displayInterpretations("anxiety");
        binding.cellBDIScore.setText(bdiQuestionnaire.getTotalScore() +" / 63");
        displayInterpretations("bdi");
    }
    private void initPromisSummary() {
        // 1) Sum up your raw‐score buckets
        PromisQuestionnaire promisQuestionnaire = (PromisQuestionnaire) Questionnaires.get(this, QuestionnaireType.PROMIS);
        int rawP = promisQuestionnaire.getPhysicalRawScore();
        int rawM = promisQuestionnaire.getMentalRawScore();

        // 2) Turn them into T−scores
        String sP = String.format(Locale.getDefault(), "%.1f",
                tScore.lookupPhsyicalTscore(rawP));
        String sM = String.format(Locale.getDefault(), "%.1f",
                tScore.lookupMentalTscore(rawM));

        // 3) Show the text in your “Score” column
        binding.cellPromisPhysicalScore.setText(rawP + " / 20");
        binding.cellPromisMentalScore .setText(rawM   + " / 20");

        // 4) Finally color & size your bars
        //    Use two‐arg displayInterpretations that you add below:
        displayInterpretations("promis_physical", sP);
        displayInterpretations("promis_mental", sM);
    }

    private void listnenBtn(){
        binding.btnOptionnalQuestions.setOnClickListener(v -> {
            navigateToNextActivityWithoutFinish(OptionalQuestionnairesActivity.class);
        });
    }

    @SuppressLint("ResourceAsColor")
    private String displayInterpretations(String categorie){
        FatigueQuestionnaire fatigueQuestionnaire = (FatigueQuestionnaire) Questionnaires.get(this, QuestionnaireType.FATIGUE);

        if(categorie.equals("fatigue")){
            if(fatigueQuestionnaire.getScore() == 0){
                txt_interpretations = getString(R.string.skipped_questionniare);
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellResultFatigue.setColorSections(upperlimit, colors);
                binding.cellResultFatigue.setUserScore(2.75f);
                binding.cellResultFatigue.setUserText(txt_interpretations);
            }else{
                double mean_fatigue = (double) fatigueQuestionnaire.getScore() / (fatigueQuestionnaire.getAnsweredQues() - fatigueQuestionnaire.getSkippedQues());
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

        DepressionAnxietyQuestionnaire depressionAnxietyQuestionnaire = (DepressionAnxietyQuestionnaire) Questionnaires.get(this, QuestionnaireType.DEPRESSIONANXIETY);
        if(categorie.equals("depression")){
            int avgScoreDepression = depressionAnxietyQuestionnaire.getDepressionScore();
            int questionAnsDep = depressionAnxietyQuestionnaire.getDepressionQuestionAnswered();

            if(avgScoreDepression==0 && questionAnsDep != 15){
                txt_interpretations = getString(R.string.skipped_questionniare);
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellDepressionResult.setColorSections(upperlimit, colors);
                binding.cellDepressionResult.setUserScore(2.75f);
                binding.cellDepressionResult.setUserText(txt_interpretations);
            }else{
                if(avgScoreDepression<7){
                    txt_interpretations = getString(R.string.within_normal_limits);

                } else if (avgScoreDepression<11) {
                    txt_interpretations = getString(R.string.borderline_case);

                }
                else{
                    txt_interpretations = getString(R.string.impaired);

                }

                float[] upperLimits_dep = {(float) (7.0f/4), (float) (11.0f/4), (float) (21.0f/4)};
                int[] colors_dep = {android.graphics.Color.GREEN, Color.YELLOW, Color.RED};
                binding.cellDepressionResult.setColorSections(upperLimits_dep, colors_dep);
                binding.cellDepressionResult.setUserScore((float) ((float) avgScoreDepression/4));
                binding.cellDepressionResult.setUserText(txt_interpretations);
            }

        }


        if(categorie.equals("anxiety")){
            int avgScoreAnx = depressionAnxietyQuestionnaire.getAnxietyScore();
            int questionAnsDep = depressionAnxietyQuestionnaire.getAnxietyQuestionAnswered();

            if(avgScoreAnx==0 && questionAnsDep!= 15){
                txt_interpretations = getString(R.string.skipped_questionniare);
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellAnxietyResult.setColorSections(upperlimit, colors);
                binding.cellAnxietyResult.setUserScore(2.75f);
                binding.cellAnxietyResult.setUserText(txt_interpretations);
            }else{
                if(avgScoreAnx<7) {
                    txt_interpretations = getString(R.string.within_normal_limits);

                }else if (avgScoreAnx<11) {
                    txt_interpretations = getString(R.string.borderline_case);

                }else{
                    txt_interpretations = getString(R.string.impaired);

                }
                float[] upperLimits_dep = {(float) (7.0f/4), (float) (11.0f/4), (float) (21.0f/4)};
                int[] colors_dep = {android.graphics.Color.GREEN, Color.YELLOW, Color.RED};
                binding.cellAnxietyResult.setColorSections(upperLimits_dep, colors_dep);
                binding.cellAnxietyResult.setUserScore((float) ((float) avgScoreAnx/4));
                binding.cellAnxietyResult.setUserText(txt_interpretations);
            }

        }

        if(categorie.equals("bdi")){
            BDIQuestionnaire bdiQuestionnaire = (BDIQuestionnaire) Questionnaires.get(this, QuestionnaireType.BDI);
            int scoreBDI = bdiQuestionnaire.getTotalScore();
            int questionAnsBDI = bdiQuestionnaire.getQuestionAnswered();
            String txt_interpretations;
            if(scoreBDI == 0 && questionAnsBDI != 21){
                txt_interpretations = getString(R.string.skipped_questionniare);
                int[] colors = {Color.rgb(128,128,128)};
                float[] upperlimit = {5.5f};
                binding.cellBDIResult.setColorSections(upperlimit, colors);
                binding.cellBDIResult.setUserScore(2.75f);
                binding.cellBDIResult.setUserText(txt_interpretations);
            }else{
                if(scoreBDI<14){
                    txt_interpretations = getString(R.string.inconspicuous);
                } else if (scoreBDI<20) {
                    txt_interpretations = getString(R.string.mild_depressive);
                } else if (scoreBDI<29) {
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

                float user_score_rescale = binding.cellBDIResult.rescaleValue((float) scoreBDI,min,redEnd, 40.0f);

                binding.cellBDIResult.setColorSections(upperlimit, colors);
                binding.cellBDIResult.setUserScore(user_score_rescale);
                binding.cellBDIResult.setUserText(txt_interpretations);
            }

        }

        return txt_interpretations;
    }
    // * new: handles only the two PROMIS rows, given the T-score string
    private void displayInterpretations(String kind, String tscore) {
        PromisQuestionnaire promisQuestionnaire = (PromisQuestionnaire) Questionnaires.get(this, QuestionnaireType.PROMIS);

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
        if (tscore.equals("No t-score") || promisQuestionnaire.getQuestionSkipped() > 3) {
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
        FatigueQuestionnaire fatigueQuestionnaire = (FatigueQuestionnaire) Questionnaires.get(this, QuestionnaireType.FATIGUE);
        DepressionAnxietyQuestionnaire hadsQuestionnaire = (DepressionAnxietyQuestionnaire) Questionnaires.get(this, QuestionnaireType.DEPRESSIONANXIETY);

        if ((fatigueQuestionnaire.getScore() / 9 > 3) || (hadsQuestionnaire.getDepressionScore() > 7) || (hadsQuestionnaire.getAnxietyScore() > 7)) {
            binding.txtDescriptionOthers.setVisibility(View.VISIBLE);
        } else {
            binding.txtSummaryDescription.setVisibility(View.VISIBLE);
        }
    }
}