package com.llui.iproms.Questionnaires;

import android.content.Context;

import com.llui.iproms.Enum.QuestionnaireStatus;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.llui.iproms.R;
import com.llui.iproms.Utils.FileManager;

public class DepressionAnxietyQuestionnaire extends AbstractQuestionnaire{
    private int anxietyScore = 0;
    private int depressionScore = 0;
    private int anxietyQuestionAnswered = 0;
    private int anxietySkippedAnswered = 0;
    private int depressionQuestionAnswered = 0;
    private int depressionSkippedAnswered = 0;
    private String category;

    public DepressionAnxietyQuestionnaire() {
        super(new int[]{
                R.string.question_HADS_1,
                R.string.question_HADS_2,
                R.string.question_HADS_3,
                R.string.question_HADS_4,
                R.string.question_HADS_5,
                R.string.question_HADS_6,
                R.string.question_HADS_7,
                R.string.question_HADS_8,
                R.string.question_HADS_9,
                R.string.question_HADS_10,
                R.string.question_HADS_11,
                R.string.question_HADS_12,
                R.string.question_HADS_13,
                R.string.question_HADS_14
        });
        
        answerOptions = new int[]{
                R.array.HADS_answers_1,
                R.array.HADS_answers_2,
                R.array.HADS_answers_3,
                R.array.HADS_answers_4,
                R.array.HADS_answers_5,
                R.array.HADS_answers_6,
                R.array.HADS_answers_7,
                R.array.HADS_answers_8,
                R.array.HADS_answers_9,
                R.array.HADS_answers_10,
                R.array.HADS_answers_11,
                R.array.HADS_answers_12,
                R.array.HADS_answers_13,
                R.array.HADS_answers_14
        };

        checkCategory();
    }

    private void checkCategory(){
        if(currentQuestion%2 == 0){
            category = "depression";
        } else {
            category = "anxiety";
        }
    }

    private void updateCategoryValues(int rating){
        checkCategory();

        if(category.equals("depression")){
            if(rating == -1){
                depressionSkippedAnswered++;
            } else {
                depressionQuestionAnswered++;
                depressionScore += rating;
            }
        } else {
            if(rating == -1){
                anxietySkippedAnswered++;
            } else {
                anxietyQuestionAnswered++;
                anxietyScore += rating;
            }
        }
    }

    @Override
    public void startNextQuestion(Context context, int rating) {
        updateStatus();
        updateCategoryValues(rating);
        quesEntries.put(questionIds[currentQuestion], rating);
        updateCSV(context);
        currentQuestion = Math.min(currentQuestion+1, questionIds.length-1);
    }

    @Override
    public void skipQuestion(Context context) {
        updateStatus();
        updateCategoryValues(-1);
        quesEntries.put(questionIds[currentQuestion], -1);
        updateCSV(context);
        currentQuestion = Math.min(currentQuestion+1, questionIds.length-1);
    }

    // CSV Storage
    @Override
    protected void updateCSV(Context context) {
        File csvFile = FileManager.getHADSFile(context);

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
                            String.valueOf(anxietyScore),
                            String.valueOf(depressionScore),
                            String.valueOf(anxietyQuestionAnswered),
                            String.valueOf(depressionQuestionAnswered),
                            String.valueOf(anxietySkippedAnswered),
                            String.valueOf(depressionSkippedAnswered)
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
                        "Total_anxiety_score", "Total_depression_score",
                        "Question_anxiety_answered", "Question_depression_answered",
                        "Question_anxiety_skipped", "Question_depression_skipped"});
                csvData.add(new String[]{
                        patient.getPatientId(),
                        patient.getDate(),
                        patient.getCaseId(),
                        progressStatus.toString(),
                        String.valueOf(anxietyScore),
                        String.valueOf(depressionScore),
                        String.valueOf(anxietyQuestionAnswered),
                        String.valueOf(depressionQuestionAnswered),
                        String.valueOf(anxietySkippedAnswered),
                        String.valueOf(depressionSkippedAnswered)
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
                } else if (i > currentQuestion) {
                    csvData.add(new String[]{questionText, String.valueOf(rating)});
                } else{
                    String answerText = context.getResources().getStringArray(answerOptions[i])[rating];
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
        File csvFile = FileManager.getHADSFile(context);

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
                anxietyScore = Integer.parseInt(summaryRow[4]);
                depressionScore = Integer.parseInt(summaryRow[5]);
                anxietyQuestionAnswered = Integer.parseInt(summaryRow[6]);
                depressionQuestionAnswered = Integer.parseInt(summaryRow[7]);
                anxietySkippedAnswered = Integer.parseInt(summaryRow[8]);
                depressionSkippedAnswered = Integer.parseInt(summaryRow[9]);
            } else {
                anxietyQuestionAnswered = 0;
                depressionQuestionAnswered = 0;
                anxietySkippedAnswered = 0;
                depressionSkippedAnswered = 0;
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
            currentQuestion = Math.min(anxietyQuestionAnswered + depressionQuestionAnswered + anxietySkippedAnswered + depressionSkippedAnswered, questionIds.length - 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public File getQuestionnaireFile(Context context) {
        return FileManager.getHADSFile(context);
    }

    public int getAnxietyScore() {
        return anxietyScore;
    }

    public int getDepressionScore() {
        return depressionScore;
    }

    public int getAnxietyQuestionAnswered() {
        return anxietyQuestionAnswered;
    }

    public int getAnxietySkippedAnswered() {
        return anxietySkippedAnswered;
    }

    public int getDepressionQuestionAnswered() {
        return depressionQuestionAnswered;
    }

    public int getDepressionSkippedAnswered() {
        return depressionSkippedAnswered;
    }
}