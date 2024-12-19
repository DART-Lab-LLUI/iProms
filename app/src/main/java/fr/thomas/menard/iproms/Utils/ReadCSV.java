package fr.thomas.menard.iproms.Utils;

import android.content.Context;
import android.icu.text.IDNA;
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
    public static void retrieveInfos(Context context){
        String csvFilePath = FileManager.getInfoFilename(context);

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            // Read the header to get column indices
            int fatigueColumnIndex = 3;
            int depressionColumnIndex = 7;
            int bdiIndex = 13;
            int promisColumnIndex = 17;
            int qolColumnIndex = 22; //21?
            int oldDate = 2;
            int sleepIndex = 66;
            int FSMCIndex = 70;

            List<String[]> csvEntries = reader.readAll();
            String[] firstRow = csvEntries.get(1);

            InfoFile.fatigue = firstRow[fatigueColumnIndex];
            InfoFile.depression = firstRow[depressionColumnIndex];
            InfoFile.bdi = firstRow[bdiIndex];
            InfoFile.promis = firstRow[promisColumnIndex];
            InfoFile.qol = firstRow[qolColumnIndex];
            InfoFile.sleep = firstRow[sleepIndex];
            InfoFile.fsmc = firstRow[FSMCIndex];
            InfoFile.oldDate = firstRow[oldDate];

            InfoFile.avg_score_fatigue = (firstRow[fatigueColumnIndex+1]);
            InfoFile.avg_score_depression = firstRow[depressionColumnIndex+1];
            InfoFile.avg_score_anxiety = firstRow[depressionColumnIndex + 2];
            InfoFile.avg_score_PROMIS_physical = firstRow[promisColumnIndex+1];
            InfoFile.avg_score_PROMIS_mental = firstRow[promisColumnIndex+2];
            InfoFile.avg_score_qol = (firstRow[qolColumnIndex+1]);

            InfoFile.questionAnsFatigue = (firstRow[fatigueColumnIndex+2]);
            InfoFile.questionAnsDep = (firstRow[depressionColumnIndex+3]);
            InfoFile.questionAnsBDI = firstRow[bdiIndex+2];
            InfoFile.questionAnsPROMIS = firstRow[promisColumnIndex+3];
            InfoFile.questionAnsQol = (firstRow[qolColumnIndex+2]);

            InfoFile.lastQuestionFatigue = firstRow[fatigueColumnIndex + 3];
            InfoFile.lastQuestionDep = firstRow[depressionColumnIndex + 4];
            InfoFile.skipped_question_anx = firstRow[depressionColumnIndex + 5];
            InfoFile.skipped_question_bdi = firstRow[bdiIndex+3];
            InfoFile.skipped_question_promis = firstRow[promisColumnIndex+4];
            InfoFile.skippedQuestionQOL = firstRow[qolColumnIndex+3];

            InfoFile.score_bdi = firstRow[bdiIndex+1];

            InfoFile.qol1 = firstRow[qolColumnIndex + 4];
            InfoFile.scoreQOL1 = firstRow[qolColumnIndex + 5];
            InfoFile.questionAnsQOL1 = firstRow[qolColumnIndex + 6];
            InfoFile.skippedQuestionQOL1 = firstRow[qolColumnIndex + 7];

            InfoFile.qol2 = firstRow[qolColumnIndex + 8];
            InfoFile.scoreQOL2 = firstRow[qolColumnIndex + 9];
            InfoFile.questionAnsQOL2 = firstRow[qolColumnIndex + 10];
            InfoFile.skippedQuestionQOL2 = firstRow[qolColumnIndex + 11];

            InfoFile.qol3 = firstRow[qolColumnIndex + 12];
            InfoFile.scoreQOL3 = firstRow[qolColumnIndex + 13];
            InfoFile.questionAnsQOL3 = firstRow[qolColumnIndex + 14];
            InfoFile.skippedQuestionQOL3 = firstRow[qolColumnIndex + 15];

            InfoFile.qol4 = firstRow[qolColumnIndex + 16];
            InfoFile.scoreQOL4 = firstRow[qolColumnIndex + 17];
            InfoFile.questionAnsQOL4 = firstRow[qolColumnIndex + 18];
            InfoFile.skippedQuestionQOL4 = firstRow[qolColumnIndex + 19];

            InfoFile.qol5 = firstRow[qolColumnIndex + 20];
            InfoFile.scoreQOL5 = firstRow[qolColumnIndex + 21];
            InfoFile.questionAnsQOL5 = firstRow[qolColumnIndex + 22];
            InfoFile.skippedQuestionQOL5 = firstRow[qolColumnIndex + 23];

            InfoFile.qol6 = firstRow[qolColumnIndex + 24];
            InfoFile.scoreQOL6 = firstRow[qolColumnIndex + 25];
            InfoFile.questionAnsQOL6 = firstRow[qolColumnIndex + 26];
            InfoFile.skippedQuestionQOL6 = firstRow[qolColumnIndex + 27];

            InfoFile.qol7 = firstRow[qolColumnIndex + 28];
            InfoFile.scoreQOL7 = firstRow[qolColumnIndex + 29];
            InfoFile.questionAnsQOL7 = firstRow[qolColumnIndex + 30];
            InfoFile.skippedQuestionQOL7 = firstRow[qolColumnIndex + 31];

            InfoFile.qol8 = firstRow[qolColumnIndex + 32];
            InfoFile.scoreQOL8 = firstRow[qolColumnIndex + 33];
            InfoFile.questionAnsQOL8 = firstRow[qolColumnIndex + 34];
            InfoFile.skippedQuestionQOL8 = firstRow[qolColumnIndex + 35];

            InfoFile.qol9 = firstRow[qolColumnIndex + 36];
            InfoFile.scoreQOL9 = firstRow[qolColumnIndex + 37];
            InfoFile.questionAnsQOL9 = firstRow[qolColumnIndex + 38];
            InfoFile.skippedQuestionQOL9 = firstRow[qolColumnIndex + 39];

            InfoFile.qol10 = firstRow[qolColumnIndex + 40];
            InfoFile.scoreQOL10 = firstRow[qolColumnIndex + 41];
            InfoFile.questionAnsQOL10 = firstRow[qolColumnIndex + 42];
            InfoFile.skippedQuestionQOL10 = firstRow[qolColumnIndex + 43];

            InfoFile.scoreFSMC = firstRow[FSMCIndex+1];
            InfoFile.questionAnsFCSM = firstRow[FSMCIndex+2];
            InfoFile.skipped_question_fsmc = firstRow[FSMCIndex+3];

            InfoFile.score_sleep = firstRow[sleepIndex+1];
            InfoFile.questionAnsSleep = firstRow[sleepIndex+2];
            InfoFile.skipped_question_sleep = firstRow[sleepIndex+3];

            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

}
