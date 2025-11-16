package com.llui.iproms.FileWriter;

import android.content.Context;

import com.llui.iproms.Enum.QuestionnaireStatus;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.llui.iproms.R;
import com.llui.iproms.Utils.FileManager;

public class BDIQuestionnaire extends AbstractQuestionnaire{
    private int totalScore = 0;
    private int questionAnswered = 0;
    private int questionSkipped = 0;

    public BDIQuestionnaire() {
        super(new int[]{
                R.string.bdi_ii_1,
                R.string.bdi_ii_2,
                R.string.bdi_ii_3,
                R.string.bdi_ii_4,
                R.string.bdi_ii_5,
                R.string.bdi_ii_6,
                R.string.bdi_ii_7,
                R.string.bdi_ii_8,
                R.string.bdi_ii_9,
                R.string.bdi_ii_10,
                R.string.bdi_ii_11,
                R.string.bdi_ii_12,
                R.string.bdi_ii_13,
                R.string.bdi_ii_14,
                R.string.bdi_ii_15,
                R.string.bdi_ii_16,
                R.string.bdi_ii_17,
                R.string.bdi_ii_18,
                R.string.bdi_ii_19,
                R.string.bdi_ii_20,
                R.string.bdi_ii_21
        });

        answerOptions = new int[]{
                R.array.bdi_answers_1,
                R.array.bdi_answers_2,
                R.array.bdi_answers_3,
                R.array.bdi_answers_4,
                R.array.bdi_answers_5,
                R.array.bdi_answers_6,
                R.array.bdi_answers_7,
                R.array.bdi_answers_8,
                R.array.bdi_answers_9,
                R.array.bdi_answers_10,
                R.array.bdi_answers_11,
                R.array.bdi_answers_12,
                R.array.bdi_answers_13,
                R.array.bdi_answers_14,
                R.array.bdi_answers_15,
                R.array.bdi_answers_16,
                R.array.bdi_answers_17,
                R.array.bdi_answers_18,
                R.array.bdi_answers_19,
                R.array.bdi_answers_20,
                R.array.bdi_answers_21
        };
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getQuestionAnswered() {
        return questionAnswered;
    }

    public int getQuestionSkipped() {
        return questionSkipped;
    }

    @Override
    public void startNextQuestion(Context context, int rating) {
        updateStatus();
        totalScore += rating;
        questionAnswered++;
        quesEntries.put(questionIds[currentQuestion], rating);
        updateCSV(context);
        currentQuestion = Math.min(currentQuestion+1, questionIds.length-1);
    }

    @Override
    public void skipQuestion(Context context) {
        updateStatus();
        questionSkipped++;
        quesEntries.put(questionIds[currentQuestion], -1);
        updateCSV(context);
        currentQuestion = Math.min(currentQuestion+1, questionIds.length-1);
    }

    // CSV Storage
    @Override
    protected void updateCSV(Context context) {
        File csvFile = FileManager.getBDIFile(context);

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
                            String.valueOf(totalScore),
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
                        "Total_score",
                        "Question_answered", "Question_skipped"});
                csvData.add(new String[]{
                        patient.getPatientId(),
                        patient.getDate(),
                        patient.getCaseId(),
                        progressStatus.toString(),
                        String.valueOf(totalScore),
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
        File csvFile = FileManager.getBDIFile(context);

        if (!csvFile.exists()) {
            return;
        }

        try {
            List<String[]> csvEntries = super.readExistingCSVFile(csvFile);

            if (csvEntries.size() < 4) return; // Not enough rows

            // --- Read patient info and summary from the second row ---
            String[] summaryRow = csvEntries.get(1);
            if (summaryRow.length >= 7) {  // match updateCSV header
                progressStatus = QuestionnaireStatus.valueOf(summaryRow[3]);
                totalScore = Integer.parseInt(summaryRow[4]);
                questionAnswered = Integer.parseInt(summaryRow[5]);
                questionSkipped = Integer.parseInt(summaryRow[6]);
            } else {
                totalScore = 0;
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
        return FileManager.getBDIFile(context);
    }
}
