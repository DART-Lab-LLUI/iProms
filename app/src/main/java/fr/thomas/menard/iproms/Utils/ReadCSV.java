package fr.thomas.menard.iproms.Utils;

import android.content.Context;
import android.util.Log;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import fr.thomas.menard.iproms.Model.InfoFile;

public class ReadCSV {
    private static int findColumnIndex (String [] headerRow, String columnName) {
        if (headerRow == null) {
            return -1;
        }
        for (int i = 0; i < headerRow.length; i++) {
            if (headerRow[i].trim().equalsIgnoreCase(columnName.trim())) {
                return i;
            }
        }
        return -1; // column not found
    }

    private static String getValue(String[] row, int index) {
        if (row == null || index < 0 || index >= row.length || row[index] == null) {
            return "";
        }

        String value = row[index].trim();
        if (value.equalsIgnoreCase("null") || value.equals("-1")) {
            return "";
        }

        return value;
    }

    public static void retrieveInfos(Context context){
        String csvFilePath = FileManager.getInfoFilename(context);

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> csvEntries = reader.readAll();
            if (csvEntries.size() < 2) {
                Log.e("ReadCSV", "CSV file is empty or has only header row.");
                reader.close();
                return;
            }

            String[] headerRow = csvEntries.get(0);
            String[] firstRow = csvEntries.get(1);

            // initialize arrays in InfoFile
            InfoFile.fatigueQuestionScores = new String[9];
            InfoFile.depressionQuestionScores = new String[14];
            InfoFile.bdiQuestionScores = new String[21];
            InfoFile.promisQuestionScores = new String[10];
            InfoFile.qolQuestionScores = new String[10];
            // for each subscale use SUB_COUNTS array
            int[] SUB_COUNTS = {8, 8, 8, 8, 8, 8, 9, 8, 8, 8};
            InfoFile.qol1QuestionScores = new String[SUB_COUNTS[0]];
            InfoFile.qol2QuestionScores = new String[SUB_COUNTS[1]];
            InfoFile.qol3QuestionScores = new String[SUB_COUNTS[2]];
            InfoFile.qol4QuestionScores = new String[SUB_COUNTS[3]];
            InfoFile.qol5QuestionScores = new String[SUB_COUNTS[4]];
            InfoFile.qol6QuestionScores = new String[SUB_COUNTS[5]];
            InfoFile.qol7QuestionScores = new String[SUB_COUNTS[6]];
            InfoFile.qol8QuestionScores = new String[SUB_COUNTS[7]];
            InfoFile.qol9QuestionScores = new String[SUB_COUNTS[8]];
            InfoFile.qol10QuestionScores = new String[SUB_COUNTS[9]];

            InfoFile.fsmcQuestionScores = new String[20];
            InfoFile.sleepQuestionScores = new String[8];

            // basic column indices (before question scores)
            int patientIdIndex = findColumnIndex(headerRow, "Patient_ID");
            int caseIdIndex = findColumnIndex(headerRow, "Case_ID");
            int oldDate = findColumnIndex(headerRow, "Date"); // "Date" field -> at column index 2
            int fatigueColumnIndex = findColumnIndex(headerRow, "Fatigue");
            int depressionColumnIndex = findColumnIndex(headerRow, "Depression_Anxiety");
            int bdiIndex = findColumnIndex(headerRow, "BDI_II");
            int promisColumnIndex = findColumnIndex(headerRow, "PROMIS");
            int qolColumnIndex = findColumnIndex(headerRow, "QOL");
            int sleepIndex = findColumnIndex(headerRow, "Sleep");
            int FSMCIndex = findColumnIndex(headerRow, "FSMC");

            // read basic info
            InfoFile.patientId = getValue(firstRow, patientIdIndex);
            InfoFile.caseId = getValue(firstRow, caseIdIndex);
            InfoFile.oldDate = getValue(firstRow, oldDate); // whatever value in column index 2 of CSV ("Time" field) -> stored in InfoFile.oldDate

            Log.d("ReadCSV", "Loaded Date = " + InfoFile.oldDate);

            // dynamic fatigue block
            InfoFile.fatigue = getValue(firstRow, fatigueColumnIndex);
            InfoFile.avg_score_fatigue = getValue(firstRow, findColumnIndex(headerRow, "Score_fatigue"));
            InfoFile.questionAnsFatigue = getValue(firstRow, findColumnIndex(headerRow, "Question_ans_fatigue"));
            InfoFile.lastQuestionFatigue = getValue(firstRow, findColumnIndex(headerRow, "Skipped_question_fatigue"));

            // pull in each indivdiual item
            for (int i = 0; i < InfoFile.fatigueQuestionScores.length; i++) {
                String colName = "Fatigue_Q" + (i + 1);
                int col = findColumnIndex(headerRow, colName);
                InfoFile.fatigueQuestionScores[i] = getValue(firstRow, col);
            }

            // dynamic depression/anxiety block
            InfoFile.depression = getValue(firstRow, depressionColumnIndex);
            InfoFile.avg_score_depression = getValue(firstRow, findColumnIndex(headerRow, "Score_depression"));
            InfoFile.avg_score_anxiety = getValue(firstRow, findColumnIndex(headerRow, "Score_anxiety"));
            InfoFile.questionAnsDep = getValue(firstRow, findColumnIndex(headerRow, "Question_ans_dep"));
            InfoFile.lastQuestionDep = getValue(firstRow, findColumnIndex(headerRow, "Skipped_question_dep"));
            InfoFile.skipped_question_anx = getValue(firstRow, findColumnIndex(headerRow, "Skipped_question_anx"));

            // pull in each individual item
            for (int i = 0; i < InfoFile.depressionQuestionScores.length; i++) {
                String colName = "Depression_Q" + (i + 1);
                int col = findColumnIndex(headerRow, colName);
                InfoFile.depressionQuestionScores[i] = getValue(firstRow, col);
            }

            // dynamic bdi block
            InfoFile.bdi = getValue(firstRow, bdiIndex);
            InfoFile.score_bdi = getValue(firstRow, findColumnIndex(headerRow, "Score_bdi"));
            InfoFile.questionAnsBDI = getValue(firstRow, findColumnIndex(headerRow, "Question_ans_bdi"));
            InfoFile.skipped_question_bdi = getValue(firstRow, findColumnIndex(headerRow, "Skipped_question_bdi"));

            // pull in each individual item
            for (int i = 0; i < InfoFile.bdiQuestionScores.length; i++) {
                String colName = "BDI_Q" + (i + 1);
                int col = findColumnIndex(headerRow, colName);
                InfoFile.bdiQuestionScores[i] = getValue(firstRow, col);
            }

            // dynamic PROMIS block
            InfoFile.promis = getValue(firstRow, promisColumnIndex);
            InfoFile.avg_score_PROMIS_mental = getValue(firstRow, findColumnIndex(headerRow, "Score_promis_mental"));
            InfoFile.avg_score_PROMIS_physical = getValue(firstRow, findColumnIndex(headerRow, "Score_promis_physical"));
            InfoFile.questionAnsPROMIS = getValue(firstRow, findColumnIndex(headerRow, "Question_ans_promis"));
            InfoFile.skipped_question_promis = getValue(firstRow, findColumnIndex(headerRow, "Skipped_question_promis"));

            // pull in each individual item
            for (int i = 0; i < InfoFile.promisQuestionScores.length; i++) {
                String colName = "PROMIS_Q" + (i + 1);
                int col = findColumnIndex(headerRow, colName);
                InfoFile.promisQuestionScores[i] = getValue(firstRow, col);
            }

            // dynamic QOL block
            InfoFile.qol = getValue(firstRow, qolColumnIndex);
            InfoFile.avg_score_qol = getValue(firstRow, findColumnIndex(headerRow, "Score_qol"));
            InfoFile.questionAnsQol = getValue(firstRow, findColumnIndex(headerRow, "Question_ans_qol"));
            InfoFile.skippedQuestionQOL = getValue(firstRow, findColumnIndex(headerRow, "Skipped_question_qol"));

            // all 10 general items
            for (int i = 0; i < InfoFile.qolQuestionScores.length; i++) {
                int idx = findColumnIndex(headerRow, "QOL_Q" + (i + 1));
                InfoFile.qolQuestionScores[i] = getValue(firstRow, idx);
            }

            // for each sub-scale
            for (int n = 1; n <= 10; n++) {
                String base = "QOL" + n;
                String lcBase = base.toLowerCase();

                // find summary columns
                int doneCol = findColumnIndex(headerRow, base);
                int scoreCol = findColumnIndex(headerRow, "Score_" + lcBase);
                int ansCol = findColumnIndex(headerRow, "Question_ans_" + lcBase);
                int skipCol = findColumnIndex(headerRow, "Skipped_question_" + lcBase);
                int qStart = findColumnIndex(headerRow, base + "_Q1");
                int count = SUB_COUNTS[n-1];

                // assign into InfoFile via reflection or switch
                switch (n) {
                    case 1:
                        InfoFile.qol1 = getValue(firstRow, doneCol);
                        InfoFile.scoreQOL1 = getValue(firstRow, scoreCol);
                        InfoFile.questionAnsQOL1 = getValue(firstRow, ansCol);
                        InfoFile.skippedQuestionQOL1 = getValue(firstRow, skipCol);
                        for (int i = 0; i<count; i++) {
                            InfoFile.qol1QuestionScores[i] = getValue(firstRow, qStart + i);
                        }
                        break;
                    case 2:
                        InfoFile.qol2 = getValue(firstRow, doneCol);
                        InfoFile.scoreQOL2 = getValue(firstRow, scoreCol);
                        InfoFile.questionAnsQOL2 = getValue(firstRow, ansCol);
                        InfoFile.skippedQuestionQOL2 = getValue(firstRow, skipCol);
                        for (int i = 0; i<count; i++) {
                            InfoFile.qol2QuestionScores[i] = getValue(firstRow, qStart + i);
                        }
                        break;
                    case 3:
                        InfoFile.qol3 = getValue(firstRow, doneCol);
                        InfoFile.scoreQOL3 = getValue(firstRow, scoreCol);
                        InfoFile.questionAnsQOL3 = getValue(firstRow, ansCol);
                        InfoFile.skippedQuestionQOL3 = getValue(firstRow, skipCol);
                        for (int i = 0; i<count; i++) {
                            InfoFile.qol3QuestionScores[i] = getValue(firstRow, qStart + i);
                        }
                        break;
                    case 4:
                        InfoFile.qol4 = getValue(firstRow, doneCol);
                        InfoFile.scoreQOL4 = getValue(firstRow, scoreCol);
                        InfoFile.questionAnsQOL4 = getValue(firstRow, ansCol);
                        InfoFile.skippedQuestionQOL4 = getValue(firstRow, skipCol);
                        for (int i = 0; i<count; i++) {
                            InfoFile.qol4QuestionScores[i] = getValue(firstRow, qStart + i);
                        }
                        break;
                    case 5:
                        InfoFile.qol5 = getValue(firstRow, doneCol);
                        InfoFile.scoreQOL5 = getValue(firstRow, scoreCol);
                        InfoFile.questionAnsQOL5 = getValue(firstRow, ansCol);
                        InfoFile.skippedQuestionQOL5 = getValue(firstRow, skipCol);
                        for (int i = 0; i<count; i++) {
                            InfoFile.qol5QuestionScores[i] = getValue(firstRow, qStart + i);
                        }
                        break;
                    case 6:
                        InfoFile.qol6 = getValue(firstRow, doneCol);
                        InfoFile.scoreQOL6 = getValue(firstRow, scoreCol);
                        InfoFile.questionAnsQOL6 = getValue(firstRow, ansCol);
                        InfoFile.skippedQuestionQOL6 = getValue(firstRow, skipCol);
                        for (int i = 0; i<count; i++) {
                            InfoFile.qol6QuestionScores[i] = getValue(firstRow, qStart + i);
                        }
                        break;
                    case 7:
                        InfoFile.qol7 = getValue(firstRow, doneCol);
                        InfoFile.scoreQOL7 = getValue(firstRow, scoreCol);
                        InfoFile.questionAnsQOL7 = getValue(firstRow, ansCol);
                        InfoFile.skippedQuestionQOL7 = getValue(firstRow, skipCol);
                        for (int i = 0; i<count; i++) {
                            InfoFile.qol7QuestionScores[i] = getValue(firstRow, qStart + i);
                        }
                        break;
                    case 8:
                        InfoFile.qol8 = getValue(firstRow, doneCol);
                        InfoFile.scoreQOL8 = getValue(firstRow, scoreCol);
                        InfoFile.questionAnsQOL8 = getValue(firstRow, ansCol);
                        InfoFile.skippedQuestionQOL8 = getValue(firstRow, skipCol);
                        for (int i = 0; i<count; i++) {
                            InfoFile.qol8QuestionScores[i] = getValue(firstRow, qStart + i);
                        }
                        break;
                    case 9:
                        InfoFile.qol9 = getValue(firstRow, doneCol);
                        InfoFile.scoreQOL9 = getValue(firstRow, scoreCol);
                        InfoFile.questionAnsQOL9 = getValue(firstRow, ansCol);
                        InfoFile.skippedQuestionQOL9 = getValue(firstRow, skipCol);
                        for (int i = 0; i<count; i++) {
                            InfoFile.qol9QuestionScores[i] = getValue(firstRow, qStart + i);
                        }
                        break;
                    case 10:
                        InfoFile.qol10 = getValue(firstRow, doneCol);
                        InfoFile.scoreQOL10 = getValue(firstRow, scoreCol);
                        InfoFile.questionAnsQOL10 = getValue(firstRow, ansCol);
                        InfoFile.skippedQuestionQOL10 = getValue(firstRow, skipCol);
                        for (int i = 0; i<count; i++) {
                            InfoFile.qol10QuestionScores[i] = getValue(firstRow, qStart + i);
                        }
                        break;
                }
            }

            // dynamic FSMC block
            InfoFile.fsmc = getValue(firstRow, FSMCIndex);
            InfoFile.scoreFSMC = getValue(firstRow, findColumnIndex(headerRow, "Score_FSMC"));
            InfoFile.questionAnsFCSM = getValue(firstRow, findColumnIndex(headerRow, "Question_ans_fsmc"));
            InfoFile.skipped_question_fsmc = getValue(firstRow, findColumnIndex(headerRow, "Skipped_question_fsmc"));
            for (int i = 0; i < InfoFile.fsmcQuestionScores.length; i++) {
                String colName = "FSMC_Q" + (i + 1);
                int col = findColumnIndex(headerRow, colName);
                InfoFile.fsmcQuestionScores[i] = getValue(firstRow, col);
            }

            // dynamic sleep block
            InfoFile.sleep = getValue(firstRow, sleepIndex);
            InfoFile.score_sleep = getValue(firstRow, findColumnIndex(headerRow, "Score_Sleep"));
            InfoFile.questionAnsSleep = getValue(firstRow, findColumnIndex(headerRow, "Question_ans_sleep"));
            InfoFile.skipped_question_sleep = getValue(firstRow, findColumnIndex(headerRow, "Skipped_question_sleep"));

            // pull in each individual item
            for (int i = 0; i < InfoFile.sleepQuestionScores.length; i++) {
                String colName = "Sleep_Q" + (i + 1);
                int col = findColumnIndex(headerRow, colName);
                InfoFile.sleepQuestionScores[i] = getValue(firstRow, col);
            }

            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

}
