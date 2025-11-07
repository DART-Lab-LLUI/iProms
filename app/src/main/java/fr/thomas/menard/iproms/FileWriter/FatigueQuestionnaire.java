package fr.thomas.menard.iproms.FileWriter;

import android.content.Context;
import android.util.Log;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.thomas.menard.iproms.Enum.QuestionnaireStatus;
import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.FileManager;

public class FatigueQuestionnaire extends AbstractQuestionnaire {
    private int skipped_ques = 0;
    private int answered_ques = 0;
    private int score = 0;

    public FatigueQuestionnaire() {
        super(new int[]{
                R.string.question1_fatigue,
                R.string.question2_fatigue,
                R.string.question3_fatigue,
                R.string.question4_fatigue,
                R.string.question5_fatigue,
                R.string.question6_fatigue,
                R.string.question7_fatigue,
                R.string.question8_fatigue,
                R.string.question9_fatigue
        });
    }

    protected String getQuestionText(Context context, int resId) {
        return context.getString(resId);
    }



    @Override
    public void startNextQuestion(Context context, int rating) {
        updateStatus();
        score += rating;
        ques_entries.put(questionIds[currentQuestion], rating);
        answered_ques++;
        updateCSV(context);
        currentQuestion = Math.min(currentQuestion+1, questionIds.length-1);
    }

    @Override
    public void skipQuestion(Context context) {
        updateStatus();
        skipped_ques++;
        ques_entries.put(questionIds[currentQuestion], -1);
        updateCSV(context);
        currentQuestion = Math.min(currentQuestion+1, questionIds.length-1);
    }

    // CSV Storage
    @Override
    protected void updateCSV(Context context) {
        File csvFile = FileManager.getFSSFile(context);

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
                            String.valueOf(score),
                            String.valueOf(answered_ques),
                            String.valueOf(skipped_ques)
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
                csvData.add(new String[]{"Patient_ID", "Date", "Case_ID", "Status", "Total_score", "Question_ans", "Skipped_question"});
                csvData.add(new String[]{
                        patient.getPatientId(),
                        patient.getDate(),
                        patient.getCaseId(),
                        progressStatus.toString(),
                        String.valueOf(score),
                        String.valueOf(answered_ques),
                        String.valueOf(skipped_ques)
                });
                csvData.add(new String[]{});
                csvData.add(new String[]{"Question", "Rating"});
            }

            // --- Append each question and its rating ---
            for (int resId : questionIds) {
                String questionText = getQuestionText(context, resId);
                int rating = ques_entries.getOrDefault(resId, 0);
                csvData.add(new String[]{questionText, String.valueOf(rating)});
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
        File csvFile = FileManager.getFSSFile(context);

        if (!csvFile.exists()) {
            return;
        }

        try {
            List<String[]> csvEntries = super.readExistingCSVFile(csvFile);

            if (csvEntries.size() < 4) {
                return;
            }

            // --- Read patient info and summary from the second row ---
            String[] summaryRow = csvEntries.get(1);
            if (summaryRow.length >= 7) {
                progressStatus = QuestionnaireStatus.valueOf(summaryRow[3]);
                score = Integer.parseInt(summaryRow[4]);
                answered_ques = Integer.parseInt(summaryRow[5]);
                skipped_ques = Integer.parseInt(summaryRow[6]);
            } else {
                answered_ques = 0;
                skipped_ques = 0;
            }

            // --- Clear previous entries ---
            ques_entries.clear();

            // --- Find the "Question","Rating" header ---
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
                        int rating = Integer.parseInt(row[1]);

                        // Match the questionText to the resource ID in questionIds
                        for (int resId : questionIds) {
                            String resText = getQuestionText(context, resId);
                            if (resText.equals(questionText)) {
                                ques_entries.put(resId, rating);
                                Log.d("FatigueQuestionnaire", "Question: " + questionText + ", Rating: " + rating);
                                break;
                            }
                        }
                    }
                }
            }

            // --- Set the current question pointer ---
            currentQuestion = Math.min(answered_ques + skipped_ques, questionIds.length - 1);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }




}
