package fr.thomas.menard.iproms.FileWriter;

import android.content.Context;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.thomas.menard.iproms.Enum.QuestionnaireStatus;
import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.FileManager;
import fr.thomas.menard.iproms.Utils.tScore;

public class PromisQuestionnaire extends AbstractQuestionnaire {
    private int physicalRawScore = 0;
    private int mentalRawScore = 0;
    private String physicalTScore;
    private String mentalTcore;
    private int questionAnswered = 0;
    private int questionSkipped = 0;
    private String category;

    public PromisQuestionnaire() {
        super(new int[]{
                R.string.promis_1,
                R.string.promis_2,
                R.string.promis_3,
                R.string.promis_4,
                R.string.promis_5,
                R.string.promis_6,
                R.string.promis_7,
                R.string.promis_8,
                R.string.promis_9,
                R.string.promis_10,
        });

        answerOptions = new int[]{
                R.array.promis_legend_1to59,
                R.array.promis_legend_1to59,
                R.array.promis_legend_1to59,
                R.array.promis_legend_1to59,
                R.array.promis_legend_1to59,
                R.array.promis_legend_6,
                R.array.promis_legend_7,
                R.array.promis_legend_8,
                R.array.promis_legend_1to59,
                R.array.promis_legend_10
        };

        checkCategory();
    }

    private void checkCategory(){
        if(currentQuestion == 2 || (currentQuestion >= 5 && currentQuestion <=7)){
            category = "physical";
        } else if(currentQuestion == 1 || currentQuestion == 3|| currentQuestion == 4 || currentQuestion == 9) {
            category = "mental";
        } else
            category = "raw";
    }


    private void updateCategoryValues(int rating){
        checkCategory();

        if (currentQuestion == 6){
            rating = recodeGlobal07(rating);
        }

        if(category.equals("physical")){
            physicalRawScore += rating;
        } else if (category.equals("mental")) {
            mentalRawScore += rating;
        }

        if(isLastQuestion()){
            double tPhys = tScore.lookupPhsyicalTscore(physicalRawScore);
            double tMntl = tScore.lookupMentalTscore(mentalRawScore);
            physicalTScore = String.format(Locale.getDefault(), "%.1f", tPhys);
            mentalTcore = String.format(Locale.getDefault(), "%.1f", tMntl);
        }
    }

    public static int recodeGlobal07(int originalScore) {
        if (originalScore < 0 || originalScore > 10) {
            throw new IllegalArgumentException("Score must be between 0 and 10");
        }

        if (originalScore == 0) return 5;
        else if (originalScore <= 3) return 4;
        else if (originalScore <= 6) return 3;
        else if (originalScore <= 9) return 2;
        else return 1; // originalScore == 10
    }


    @Override
    public void startNextQuestion(Context context, int rating) {
        updateStatus();
        updateCategoryValues(rating);
        questionAnswered++;
        quesEntries.put(questionIds[currentQuestion], rating);
        updateCSV(context);
        currentQuestion = Math.min(currentQuestion+1, questionIds.length-1);
    }

    @Override
    public void skipQuestion(Context context) {
        updateStatus();
        updateCategoryValues(0);
        questionSkipped++;
        quesEntries.put(questionIds[currentQuestion], -1);
        updateCSV(context);
        currentQuestion = Math.min(currentQuestion+1, questionIds.length-1);
    }

    // CSV Storage
    @Override
    protected void updateCSV(Context context) {
        File csvFile = FileManager.getPromisFile(context);

        List<String[]> csvData = new ArrayList<>();
        boolean fileExists = csvFile.exists() && csvFile.length() > 0;

        try {
            if (fileExists) {
                // --- Read existing file ---
                csvData = super.readExistingCSVFile(csvFile);

                // --- Update header row if needed ---
                if (csvData.size() > 1) {
                    csvData.set(1, new String[]{
                            patient.getPatientId(),
                            patient.getDate(),
                            patient.getCaseId(),
                            progressStatus.toString(),
                            String.valueOf(physicalRawScore),
                            String.valueOf(mentalRawScore),
                            String.valueOf(physicalTScore),
                            String.valueOf(mentalTcore),
                            String.valueOf(questionAnswered),
                            String.valueOf(questionSkipped)
                    });
                }

                // Remove all old question rows after "Question","Rating"
                int questionHeaderIndex = -1;
                for (int i = 0; i < csvData.size(); i++) {
                    if (csvData.get(i).length > 0 && "Question".equals(csvData.get(i)[0])) {
                        questionHeaderIndex = i;
                        break;
                    }
                }
                if (questionHeaderIndex != -1) {
                    // Keep everything up to "Question","Rating"
                    csvData = csvData.subList(0, questionHeaderIndex + 1);
                }

            } else {
                // --- Create new file ---
                csvData.add(new String[]{
                        "Patient_ID", "Date", "Case_ID", "Status",
                        "Total_physical_raw_score", "Total_mental_raw_score",
                        "Total_physical_t_score", "Total_mental_t_score",
                        "Question_answered", "Question_skipped"});
                csvData.add(new String[]{
                        patient.getPatientId(),
                        patient.getDate(),
                        patient.getCaseId(),
                        progressStatus.toString(),
                        String.valueOf(physicalRawScore),
                        String.valueOf(mentalRawScore),
                        String.valueOf(physicalTScore),
                        String.valueOf(mentalTcore),
                        String.valueOf(questionAnswered),
                        String.valueOf(questionSkipped)
                });
                csvData.add(new String[]{});
                csvData.add(new String[]{"Question", "Rating", "Rating Answer"});
            }

            // --- Append each question and its rating ---
            for (int i = 0; i < questionIds.length; i++) {
                String questionText = getQuestionText(context, questionIds[i]);
                int rating = quesEntries.getOrDefault(questionIds[i], 0);

                if (rating == -1){
                    csvData.add(new String[]{questionText, String.valueOf(rating)});
                } else if (i > currentQuestion-1) {
                    csvData.add(new String[]{questionText, String.valueOf(rating)});
                } else if (i == 6) {
                    int recoded_rating = recodeGlobal07(rating);
                    String answerText = context.getResources().getStringArray(answerOptions[i])[rating];
                    csvData.add(new String[]{questionText, String.valueOf(recoded_rating), answerText});
                } else{
                    String answerText = context.getResources().getStringArray(answerOptions[i])[rating-1];
                    csvData.add(new String[]{questionText, String.valueOf(rating), answerText});
                }
            }

            // --- Write back to file ---
            CSVWriter writer = super.getCSVWriter(csvFile);
            writer.writeAll(csvData);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Read CSV
    /**
     * Reads the Fatigue questionnaire CSV file and populates the questionnaire object.
     * @param context Context for file access
     */
    @Override
    public void readCSV(Context context) {
        File csvFile = FileManager.getPromisFile(context);

        if (!csvFile.exists()) {
            return;
        }

        try {
            List<String[]> csvEntries = super.readExistingCSVFile(csvFile);

            if (csvEntries.size() < 4) return; // Not enough rows

            // --- Read patient info and summary from the second row ---
            String[] summaryRow = csvEntries.get(1);
            if (summaryRow.length >= 10) {  // match updateCSV header
                progressStatus = QuestionnaireStatus.valueOf(summaryRow[3]);
                physicalRawScore = Integer.parseInt(summaryRow[4]);
                mentalRawScore = Integer.parseInt(summaryRow[5]);
                physicalTScore = summaryRow[6];
                mentalTcore = summaryRow[7];
                questionAnswered = Integer.parseInt(summaryRow[8]);
                questionSkipped = Integer.parseInt(summaryRow[9]);
            } else {
                physicalRawScore = 0;
                mentalRawScore = 0;
                questionAnswered = 0;
                questionSkipped = 0;
            }

            // --- Clear previous entries ---
            quesEntries.clear();

            // --- Find the "Question","Rating","Rating Answer" header ---
            int questionHeaderIndex = -1;
            for (int i = 0; i < csvEntries.size(); i++) {
                String[] row = csvEntries.get(i);
                if (row.length >= 2 && "Question".equals(row[0]) && "Rating".equals(row[1])) {
                    questionHeaderIndex = i;
                    break;
                }
            }

            if (questionHeaderIndex != -1) {
                // --- Read question ratings starting after the header ---
                for (int i = questionHeaderIndex + 1; i < csvEntries.size(); i++) {
                    String[] row = csvEntries.get(i);
                    if (row.length >= 2) {
                        String questionText = row[0];
                        int rating = 0;
                        try {
                            rating = Integer.parseInt(row[1]);
                        } catch (NumberFormatException e) {
                            rating = 0; // default if missing
                        }

                        // --- Match the question text to a resource ID ---
                        for (int resId : questionIds) {
                            String resText = getQuestionText(context, resId);
                            if (resText.equals(questionText)) {
                                quesEntries.put(resId, rating);
                                break;
                            }
                        }
                    }
                }
            }

            // --- Set currentQuestion pointer to first unanswered/skipped question ---
            currentQuestion = Math.min(questionAnswered + questionSkipped, questionIds.length - 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public File getQuestionnaireFile(Context context) {
        return FileManager.getPromisFile(context);
    }

    public int getPhysicalRawScore() {
        return physicalRawScore;
    }

    public int getMentalRawScore() {
        return mentalRawScore;
    }

    public String getPhysicalTScore() {
        return physicalTScore;
    }

    public String getMentalTcore() {
        return mentalTcore;
    }

    public int getQuestionAnswered() {
        return questionAnswered;
    }

    public int getQuestionSkipped() {
        return questionSkipped;
    }
}
