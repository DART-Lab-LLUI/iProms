package fr.thomas.menard.iproms.Utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.common.collect.EnumBiMap;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import org.apache.commons.io.output.BrokenWriter;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.thomas.menard.iproms.Model.InfoFile;
import fr.thomas.menard.iproms.Model.Patient;

public class WriteCSV extends ViewModel {

    private int findColumnIndex(String[] header, String name) {
        for (int i = 0; i < header.length; i++) {
            if (header[i].equals(name)) {
                return i;
            }
        }

        return -1;
    }

    public static WriteCSV getInstance(@NonNull ViewModelStoreOwner owner) {

        return new ViewModelProvider(owner, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(WriteCSV.class);
    }

    // helper function to create and initialize score arrays
    private String[] createDefaultScores(int numQuestions) {
        String[] scores = new String[numQuestions]; // creates new String array of specified size
        for (int i = 0; i < numQuestions; i++) {
            scores[i] = " "; // loop fills each element of array with " "
        }
        return scores;
    }

    private static final int [] QOL_SUB_COUNTS = {8, 8, 8, 8, 8, 8, 9, 8, 8, 8};

    public void initInfos(String path, Context context){
        Patient patientInfo = Patient.getPatient();

        // generate current timestamp in desired format
        String currentTimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        // define number of questions for each questionnaire
        int numQuestionsFatigue = 9;
        int numQuestionsDepression = 14;
        int numQuestionsBDI = 21;
        int numQuestionsPROMIS = 10;
        int numQuestionsQOL = 10;
        int numQuestionsSleep = 8;
        int numQuestionFSMC = 20;
        int numQuestionQOL1 = 8;
        int numQuestionQOL2 = 8;
        int numQuestionQOL3 = 8;
        int numQuestionQOL4 = 8;
        int numQuestionQOL5 = 8;
        int numQuestionQOL6 = 8;
        int numQuestionQOL7 = 9;
        int numQuestionQOL8 = 8;
        int numQuestionQOL9 = 8;
        int numQuestionQOL10 = 8;

        // create and initialize score arrays
        String[] fatiqueQuestionScores = createDefaultScores(numQuestionsFatigue);
        String[] depressionQuestionScores = createDefaultScores(numQuestionsDepression);
        String[] bdiQuestionScores = createDefaultScores(numQuestionsBDI);
        String[] promisQuestionScores = createDefaultScores(numQuestionsPROMIS);
        String[] sleepQuestionScores = createDefaultScores(numQuestionsSleep);
        String[] fsmcQuestionScores = createDefaultScores(numQuestionFSMC);
        String[] qolQuestionScores = createDefaultScores(numQuestionsQOL);
        String[] qol1QuestionScores = createDefaultScores(numQuestionQOL1);
        String[] qol2QuestionScores = createDefaultScores(numQuestionQOL2);
        String[] qol3QuestionScores = createDefaultScores(numQuestionQOL3);
        String[] qol4QuestionScores = createDefaultScores(numQuestionQOL4);
        String[] qol5QuestionScores = createDefaultScores(numQuestionQOL5);
        String[] qol6QuestionScores = createDefaultScores(numQuestionQOL6);
        String[] qol7QuestionScores = createDefaultScores(numQuestionQOL7);
        String[] qol8QuestionScores = createDefaultScores(numQuestionQOL8);
        String[] qol9QuestionScores = createDefaultScores(numQuestionQOL9);
        String[] qol10QuestionScores = createDefaultScores(numQuestionQOL10);


        // pass currentTimestamp instead of patientInfo.getDate(context)
        createAndWriteInfos(path,
                patientInfo.getPatientId(),
                patientInfo.getCaseId(),
                currentTimestamp, // updated timestamp for this new cycle -> used as "Time" field
                "null", "0", "0", "0", fatiqueQuestionScores,
                "null", "0", "0", "0", "0", "0", depressionQuestionScores,
                "null", "0", "0", "0", bdiQuestionScores,
                "null", "0","0", "0", "0", promisQuestionScores,
                "null", "0", "0", "0", sleepQuestionScores,
                "null", "0", "0", "0", fsmcQuestionScores,
                "null", "0", "0", "0", qolQuestionScores,
                "null", "0", "0", "0", qol1QuestionScores,
                "null", "0", "0", "0", qol2QuestionScores,
                "null", "0", "0", "0", qol3QuestionScores,
                "null", "0", "0", "0", qol4QuestionScores,
                "null", "0", "0", "0", qol5QuestionScores,
                "null", "0", "0", "0", qol6QuestionScores,
                "null", "0", "0", "0", qol7QuestionScores,
                "null", "0", "0", "0", qol8QuestionScores,
                "null", "0", "0", "0", qol9QuestionScores,
                "null", "0", "0", "0", qol10QuestionScores
        );
    }


    public void createAndWriteInfos(String filePath, String patientID, String caseId, String oldDate,
                                    String fatigue, String avg_score_fatigue, String question_ans_fatigue,String last_question_fatigue, String[] fatigueQuestionScores,
                                    String depression, String avg_score_dep, String score_anxiety, String question_ans_dep,String last_question_depression, String question_skipped_anx, String [] depressionQuestionScores,
                                    String bdi, String avg_score_bdi, String question_ans_bdi,String last_question_bdi, String [] bdiQuestionScores,
                                    String promis, String avg_score_promis_physical, String avg_score_promis_mental, String question_ans_promis,String last_question_promis, String [] promisQuestionScores,
                                    String sleep, String score_Sleep, String question_ans_sleep, String skip_question_sleep, String [] sleepQuestionScores,
                                    String FSMC, String score_fsmc, String question_ans_fsmc, String skip_question_fsmc, String [] fsmcQuestionScores,
                                    String qol, String avg_score_qol, String question_ans_qol, String skip_question_qol, String [] qolQuestionScores,
                                    String qol1, String score_qol1, String question_ans_qol1, String skip_question_qol1, String [] qol1QuestionScores,
                                    String qol2, String score_qol2, String question_ans_qol2, String skip_question_qol2, String [] qol2QuestionScores,
                                    String qol3, String score_qol3, String question_ans_qol3, String skip_question_qol3, String [] qol3QuestionScores,
                                    String qol4, String score_qol4, String question_ans_qol4, String skip_question_qol4, String [] qol4QuestionScores,
                                    String qol5, String score_qol5, String question_ans_qol5, String skip_question_qol5, String [] qol5QuestionScores,
                                    String qol6, String score_qol6, String question_ans_qol6, String skip_question_qol6, String [] qol6QuestionScores,
                                    String qol7, String score_qol7, String question_ans_qol7, String skip_question_qol7, String [] qol7QuestionScores,
                                    String qol8, String score_qol8, String question_ans_qol8, String skip_question_qol8, String [] qol8QuestionScores,
                                    String qol9, String score_qol9, String question_ans_qol9, String skip_question_qol9, String [] qol9QuestionScores,
                                    String qol10, String score_qol10, String question_ans_qol10, String skip_question_qol10, String [] qol10QuestionScores) {



        File file = new File(filePath);

        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter with ';' as separator
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            List<String[]> summ = new ArrayList<String[]>();

            // CSV Header; empty list to store header column names
            List<String> header = new ArrayList<>();
            // adds standard column names to header list
            header.addAll(List.of("Patient_ID", "Case_ID", "Date", "Fatigue", "Score_fatigue", "Question_ans_fatigue", "Skipped_question_fatigue",
                    "Depression_Anxiety", "Score_depression", "Score_anxiety", "Question_ans_dep", "Skipped_question_dep","Skipped_question_anx",
                    "BDI_II", "Score_bdi", "Question_ans_bdi", "Skipped_question_bdi",
                    "PROMIS", "Score_promis_physical", "Score_promis_mental", "Question_ans_promis", "Skipped_question_promis",
                    "Sleep","Score_sleep", "Question_ans_sleep", "Skipped_question_sleep",
                    "FSMC", "Score_fsmc", "Question_ans_fsmc", "Skipped_question_fsmc",
                    "QOL", "Score_qol", "Question_ans_qol", "Skipped_question_qol",
                    "QOL1","Score_qol1", "Question_ans_qol1", "Skipped_question_qol1",
                    "QOL2","Score_qol2", "Question_ans_qol2", "Skipped_question_qol2",
                    "QOL3","Score_qol3", "Question_ans_qol3", "Skipped_question_qol3",
                    "QOL4","Score_qol4", "Question_ans_qol4", "Skipped_question_qol4",
                    "QOL5","Score_qol5", "Question_ans_qol5", "Skipped_question_qol5",
                    "QOL6","Score_qol6", "Question_ans_qol6", "Skipped_question_qol6",
                    "QOL7","Score_qol7", "Question_ans_qol7", "Skipped_question_qol7",
                    "QOL8","Score_qol8", "Question_ans_qol8", "Skipped_question_qol8",
                    "QOL9","Score_qol9", "Question_ans_qol9", "Skipped_question_qol9",
                    "QOL10","Score_qol10", "Question_ans_qol10", "Skipped_question_qol10"));


            // add headers for individual question scores
            for (int i = 0; i < fatigueQuestionScores.length; i++) {
                header.add("Fatigue_Q" + (i + 1));
            }
            for (int i = 0; i < depressionQuestionScores.length; i++) {
                header.add("Depression_Q" + (i + 1));
            }
            for (int i = 0; i < bdiQuestionScores.length; i++) {
                header.add("BDI_Q" + (i + 1));
            }
            for (int i = 0; i < promisQuestionScores.length; i++) {
                header.add("PROMIS_Q" + (i + 1));
            }

            for (int i = 0; i < sleepQuestionScores.length; i++) {
                header.add("Sleep_Q" + (i + 1));
            }
            for (int i = 0; i < fsmcQuestionScores.length; i++) {
                header.add("FSMC_Q" + (i + 1));
            }

            for (int i = 0; i < qolQuestionScores.length; i++) {
                header.add("QOL_Q" + (i + 1));
            }
            for (int i = 0; i < qol1QuestionScores.length; i++) {
                header.add("QOL1_Q" + (i + 1));
            }
            for (int i = 0; i < qol2QuestionScores.length; i++) {
                header.add("QOL2_Q" + (i + 1));
            }
            for (int i = 0; i < qol3QuestionScores.length; i++) {
                header.add("QOL3_Q" + (i + 1));
            }
            for (int i = 0; i < qol4QuestionScores.length; i++) {
                header.add("QOL4_Q" + (i + 1));
            }
            for (int i = 0; i < qol5QuestionScores.length; i++) {
                header.add("QOL5_Q" + (i + 1));
            }
            for (int i = 0; i < qol6QuestionScores.length; i++) {
                header.add("QOL6_Q" + (i + 1));
            }
            for (int i = 0; i < qol7QuestionScores.length; i++) {
                header.add("QOL7_Q" + (i + 1));
            }
            for (int i = 0; i < qol8QuestionScores.length; i++) {
                header.add("QOL8_Q" + (i + 1));
            }
            for (int i = 0; i < qol9QuestionScores.length; i++) {
                header.add("QOL9_Q" + (i + 1));
            }
            for (int i = 0; i < qol10QuestionScores.length; i++) {
                header.add("QOL10_Q" + (i + 1));
            }

            // add header to csv file
            summ.add(header.toArray(new String[0]));

            // empty list to store data for current row
            List<String> dataRow = new ArrayList<>();
            // adds all standard data values to dataRow list
            dataRow.addAll(List.of(patientID, caseId, oldDate, fatigue, avg_score_fatigue, question_ans_fatigue,last_question_fatigue,
                    depression, avg_score_dep, score_anxiety, question_ans_dep, last_question_depression,question_skipped_anx,
                    bdi, avg_score_bdi, question_ans_bdi, last_question_bdi,
                    promis, avg_score_promis_physical, avg_score_promis_mental, question_ans_promis, last_question_promis,
                    sleep, score_Sleep, question_ans_sleep, skip_question_sleep,
                    FSMC, score_fsmc, question_ans_fsmc, skip_question_fsmc,
                    qol, avg_score_qol, question_ans_qol, skip_question_qol,
                    qol1, score_qol1, question_ans_qol1, skip_question_qol1,
                    qol2, score_qol2, question_ans_qol2, skip_question_qol2,
                    qol3, score_qol3, question_ans_qol3, skip_question_qol3,
                    qol4, score_qol4, question_ans_qol4, skip_question_qol4,
                    qol5, score_qol5, question_ans_qol5, skip_question_qol5,
                    qol6, score_qol6, question_ans_qol6, skip_question_qol6,
                    qol7, score_qol7, question_ans_qol7, skip_question_qol7,
                    qol8, score_qol8, question_ans_qol8, skip_question_qol8,
                    qol9, score_qol9, question_ans_qol9, skip_question_qol9,
                    qol10, score_qol10, question_ans_qol10, skip_question_qol10));


            // add all question scores to respective questionnaire to dataRow list
            dataRow.addAll(List.of(fatigueQuestionScores));
            dataRow.addAll(List.of(depressionQuestionScores));
            dataRow.addAll(List.of(bdiQuestionScores));
            dataRow.addAll(List.of(promisQuestionScores));
            dataRow.addAll(List.of(sleepQuestionScores));
            dataRow.addAll(List.of(fsmcQuestionScores));
            dataRow.addAll(List.of(qolQuestionScores));
            dataRow.addAll(List.of(qol1QuestionScores));
            dataRow.addAll(List.of(qol2QuestionScores));
            dataRow.addAll(List.of(qol3QuestionScores));
            dataRow.addAll(List.of(qol4QuestionScores));
            dataRow.addAll(List.of(qol5QuestionScores));
            dataRow.addAll(List.of(qol6QuestionScores));
            dataRow.addAll(List.of(qol7QuestionScores));
            dataRow.addAll(List.of(qol8QuestionScores));
            dataRow.addAll(List.of(qol9QuestionScores));
            dataRow.addAll(List.of(qol10QuestionScores));


            // add dataRow to summ list
            summ.add(dataRow.toArray(new String[0]));

            writer.writeAll(summ);

            // closing writer connection
            writer.close();

        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void createAndWriteSleepResult(String filePath, String patientID, String caseId, String oldDate,
                                          String score_Sleep, String[] sleepQuestionScores) {


        File file = new File(filePath);

        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter with ';' as separator
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            List<String[]> summ = new ArrayList<String[]>();

            // CSV Header
            List<String> header = new ArrayList<>();
            header.addAll(List.of("Patient_ID", "Case_ID", "Date", "Score_sleep"));

            // add headers for individual question scores
            for (int i = 0; i < sleepQuestionScores.length; i++) {
                header.add("Sleep_Q" + (i + 1));
            }
            summ.add(header.toArray(new String[0]));

            // data row
            List<String> dataRow = new ArrayList<>();
            dataRow.addAll(List.of(patientID, caseId, oldDate, score_Sleep));

            // add individual question scores
            dataRow.addAll(List.of(sleepQuestionScores));

            summ.add(dataRow.toArray(new String[0]));
            summ.add(new String[]{});

            writer.writeAll(summ);

            // closing writer connection
            writer.close();

        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createAndWriteFSMCResult(String filePath, String patientID, String caseId, String oldDate,
                                         String score_fsmc, String[] fsmcQuestionScore) {


        File file = new File(filePath);

        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter with ';' as separator
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            List<String[]> summ = new ArrayList<String[]>();

            // CSV Header
            List<String> header = new ArrayList<>();
            header.addAll(List.of("Patient_ID", "Case_ID", "Date", "Score_fsmc"));

            // add headers for individual question scores
            for (int i = 0; i < fsmcQuestionScore.length; i++) {
                header.add("FSMC_Q" + (i + 1));
            }

            summ.add(header.toArray(new String[0]));

            // data row
            List<String> dataRow = new ArrayList<>();
            dataRow.addAll(List.of(patientID, caseId, oldDate, score_fsmc));

            // add individual question scores
            dataRow.addAll(List.of(fsmcQuestionScore));

            summ.add(dataRow.toArray(new String[0]));
            summ.add(new String[]{});

            writer.writeAll(summ);

            // closing writer connection
            writer.close();

        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createAndWriteResult(String filePath, String patientID, String caseId, String oldDate,
                                     String avg_score_fatigue, String[] fatigueQuestionScores,
                                     String avg_score_dep, String score_anxiety, String[] depressionQuestionScores,
                                     String scoreBDI, String[] bdiQuestionScores,
                                     String score_physical, String score_mental, String[] promisQuestionScores,
                                     String score_qol1, String score_qol2, String score_qol3, String score_qol4, String score_qol5, String score_qol6, String score_qol7, String score_qol8, String score_qol9, String score_qol10,
                                     String[] qolQuestionScores, String[] qol1QuestionScores, String[] qol2QuestionScores, String[] qol3QuestionScores, String[] qol4QuestionScores, String[] qol5QuestionScores,
                                     String[] qol6QuestionScores, String[] qol7QuestionScores, String[] qol8QuestionScores, String[] qol9QuestionScores, String[] qol10QuestionScores) {

        File file = new File(filePath);

        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter with ';' as separator
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            List<String[]> summ = new ArrayList<String[]>();

            // CSV Header
            List<String> header = new ArrayList<>();
            header.addAll(List.of("Patient_ID", "Case_ID", "Date",
                    "Score_fatigue",
                    "Score_depression", "Score_anxiety",
                    "Score_BDI",
                    "Score_promis_physical", "Score_promis_mental",
                    "Score_qol1",
                    "Score_qol2",
                    "Score_qol3",
                    "Score_qol4",
                    "Score_qol5",
                    "Score_qol6",
                    "Score_qol7",
                    "Score_qol8",
                    "Score_qol9",
                    "Score_qol10"));

            // add headers for individual question scores
            for (int i = 0; i < fatigueQuestionScores.length; i++) {
                header.add("Fatigue_Q" + (i + 1));
            }
            for (int i = 0; i < depressionQuestionScores.length; i++) {
                header.add("Depression_Q" + (i + 1));
            }
            for (int i = 0; i < bdiQuestionScores.length; i++) {
                header.add("BDI_Q" + (i + 1));
            }
            for (int i = 0; i < promisQuestionScores.length; i++) {
                header.add("PROMIS_Q" + (i + 1));
            }
            for (int i = 0; i < qolQuestionScores.length; i++) {
                header.add("QOL_Q" + (i + 1));
            }
            for (int i = 0; i < qol1QuestionScores.length; i++) {
                header.add("QOL1_Q" + (i + 1));
            }
            for (int i = 0; i < qol2QuestionScores.length; i++) {
                header.add("QOL2_Q" + (i + 1));
            }
            for (int i = 0; i < qol3QuestionScores.length; i++) {
                header.add("QOL3_Q" + (i + 1));
            }
            for (int i = 0; i < qol4QuestionScores.length; i++) {
                header.add("QOL4_Q" + (i + 1));
            }
            for (int i = 0; i < qol5QuestionScores.length; i++) {
                header.add("QOL5_Q" + (i + 1));
            }
            for (int i = 0; i < qol6QuestionScores.length; i++) {
                header.add("QOL6_Q" + (i + 1));
            }
            for (int i = 0; i < qol7QuestionScores.length; i++) {
                header.add("QOL7_Q" + (i + 1));
            }
            for (int i = 0; i < qol8QuestionScores.length; i++) {
                header.add("QOL8_Q" + (i + 1));
            }
            for (int i = 0; i < qol9QuestionScores.length; i++) {
                header.add("QOL9_Q" + (i + 1));
            }
            for (int i = 0; i < qol10QuestionScores.length; i++) {
                header.add("QOL10_Q" + (i + 1));
            }

            summ.add(header.toArray(new String[0]));

            // data row
            List<String> dataRow = new ArrayList<>();
            dataRow.addAll(List.of(patientID, caseId, oldDate, avg_score_fatigue, avg_score_dep, score_anxiety, scoreBDI, score_physical, score_mental,
                    score_qol1, score_qol2, score_qol3, score_qol4, score_qol5, score_qol6, score_qol7, score_qol8, score_qol9, score_qol10));

            // add individual question scores
            if (fatigueQuestionScores != null) {
                dataRow.addAll(Arrays.asList(fatigueQuestionScores));
            } else {
                for (int i = 0; i < 9; i++) dataRow.add("0");
            }
            if (depressionQuestionScores != null) {
                dataRow.addAll(Arrays.asList(depressionQuestionScores));
            } else {
                for (int i = 0; i < 14; i++) dataRow.add("0");
            }
            if (bdiQuestionScores != null) {
                dataRow.addAll(Arrays.asList(bdiQuestionScores));
            } else {
                for (int i = 0; i < 21; i++) dataRow.add("0");
            }
            if (promisQuestionScores != null) {
                dataRow.addAll(Arrays.asList(promisQuestionScores));
            } else {
                for (int i = 0; i < 10; i++) dataRow.add("0");
            }
            if (qolQuestionScores != null) {
                dataRow.addAll(Arrays.asList(qolQuestionScores));
            } else {
                for (int i = 0; i < 10; i++) dataRow.add("0");
            }
            if (qol1QuestionScores != null) {
                dataRow.addAll(Arrays.asList(qol1QuestionScores));
            } else {
                for (int i = 0; i < 8; i++) dataRow.add("0");
            }
            if (qol2QuestionScores != null) {
                dataRow.addAll(Arrays.asList(qol2QuestionScores));
            } else {
                for (int i = 0; i < 8; i++) dataRow.add("0");
            }
            if (qol3QuestionScores != null) {
                dataRow.addAll(Arrays.asList(qol3QuestionScores));
            } else {
                for (int i = 0; i < 8; i++) dataRow.add("0");
            }
            if (qol4QuestionScores != null) {
                dataRow.addAll(Arrays.asList(qol4QuestionScores));
            } else {
                for (int i = 0; i < 8; i++) dataRow.add("0");
            }
            if (qol5QuestionScores != null) {
                dataRow.addAll(Arrays.asList(qol5QuestionScores));
            } else {
                for (int i = 0; i < 8; i++) dataRow.add("0");
            }
            if (qol6QuestionScores != null) {
                dataRow.addAll(Arrays.asList(qol6QuestionScores));
            } else {
                for (int i = 0; i < 8; i++) dataRow.add("0");
            }
            if (qol7QuestionScores != null) {
                dataRow.addAll(Arrays.asList(qol7QuestionScores));
            } else {
                for (int i = 0; i < 9; i++) dataRow.add("0");
            }
            if (qol8QuestionScores != null) {
                dataRow.addAll(Arrays.asList(qol8QuestionScores));
            } else {
                for (int i = 0; i < 8; i++) dataRow.add("0");
            }
            if (qol9QuestionScores != null) {
                dataRow.addAll(Arrays.asList(qol9QuestionScores));
            } else {
                for (int i = 0; i < 8; i++) dataRow.add("0");
            }
            if (qol10QuestionScores != null) {
                dataRow.addAll(Arrays.asList(qol10QuestionScores));
            } else {
                for (int i = 0; i < 8; i++) dataRow.add("0");
            }

            summ.add(dataRow.toArray(new String[0]));
            summ.add(new String[]{});

            writer.writeAll(summ);

            // closing writer connection
            writer.close();

        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    public void createAndWriteCSV_fatigue(String filePath, String idPatient, String caseID, String oldDate, String num_question, String rating)
    {

        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);

        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter with ';' as separator
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            //List of task summary
            List<String[]> summ = new ArrayList<String[]>();
            summ.add(new String[] { "Patient_ID", "Date", "Case_ID"});
            summ.add(new String[] { idPatient, oldDate, caseID});
            summ.add(new String[]{});
            writer.writeAll(summ);

            // create a List which contains String array
            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[] { "Question number", "Rating" });

            // check if rating is skip and write "-1" instead
            if (rating.equals("skip")) {
                data.add(new String[] {num_question, "-1"});
            } else {
                data.add(new String[] { num_question, rating});
            }
            writer.writeAll(data);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeDataCSV_fatigue(String filePath, String num_question, String rating, String[] fatigueQuestionScores)
    {

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> csvEntries = reader.readAll();
            reader.close();

            String[] header = csvEntries.get(0);
            String[] row = csvEntries.size() > 1 ? csvEntries.get(csvEntries.size() - 1) : new String [header.length];

            // find first "Depression_Q1" column
            int qStart = findColumnIndex(header, "Fatigue_Q1");

            // write users answer into correct column
            if (qStart >= 0) {
                int questionIndex = Integer.parseInt(num_question) -1;
                int targetCol = qStart + questionIndex;
                row[targetCol] = rating.equals("skip") ? "-1" : rating;
            }

            // rewrite all of stored scores
            if (qStart >= 0) {
                for (int i = 0; i < fatigueQuestionScores.length; i++) {
                    row[qStart + i] = fatigueQuestionScores[i];
                }
            }


            CSVWriter writer = new CSVWriter(new FileWriter(filePath));
            writer.writeAll(csvEntries);
            writer.close();

        } catch (IOException | CsvException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createAndWriteCSV_depression(String filePath, String idPatient, String caseID, String oldDate, String num_question, String rating) {
        File file = new File(filePath);

        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter with ';' as separator
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            //List of task summary
            List<String[]> summ = new ArrayList<String[]>();
            summ.add(new String[] { "Patient_ID", "Date", "Case_ID"});
            summ.add(new String[] { idPatient, oldDate, caseID});
            summ.add(new String[]{});
            writer.writeAll(summ);

            // create a List which contains String array
            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[] { "Question number", "Rating" });

            // check if rating is skip and write "-1" instead
            if (rating.equals("skip")) {
                data.add(new String[] {num_question, "-1"});
            } else {
                data.add(new String[] { num_question, rating});
            }

            writer.writeAll(data);
            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeDataCSV_depression(String filePath, String num_question, String rating, String[] depressionQuestionScores) {
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> csvEntries = reader.readAll();
            reader.close();

            String[] header = csvEntries.get(0);
            String[] row = csvEntries.size() > 1 ? csvEntries.get(csvEntries.size() - 1) : new String [header.length];

            // find first "Depression_Q1" column
            int qStart = findColumnIndex(header, "Depression_Q1");

            // write users answer into correct column
            if (qStart >= 0) {
                int questionIndex = Integer.parseInt(num_question) -1;
                int targetCol = qStart + questionIndex;
                row[targetCol] = rating.equals("skip") ? "-1" : rating;
            }

            // rewrite all of stored scores
            if (qStart >= 0) {
                for (int i = 0; i < depressionQuestionScores.length; i++) {
                    row[qStart + i] = depressionQuestionScores[i];
                }
            }

            CSVWriter writer = new CSVWriter(new FileWriter(filePath));
            writer.writeAll(csvEntries);
            writer.close();

        } catch (IOException | CsvException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createAndWriteCSV_BDI(String filePath, String idPatient, String caseID, String oldDate, String num_question, String rating) {
        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);

        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter with ';' as separator
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            //List of task summary
            List<String[]> summ = new ArrayList<String[]>();
            summ.add(new String[] { "Patient_ID", "Date", "Case_ID"});
            summ.add(new String[] { idPatient, oldDate, caseID});
            summ.add(new String[]{});
            writer.writeAll(summ);

            // create a List which contains String array
            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[] { "Question number", "Rating" });

            // check if rating is skip and write "-1" instead
            if (rating.equals("skip")) {
                data.add(new String[] {num_question, "-1"});
            } else {
                data.add(new String[] { num_question, rating});
            }

            writer.writeAll(data);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeDataCSV_BDI(String filePath, String num_question, String rating, String[] bdiQuestionScores) {
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> csvEntries = reader.readAll();
            reader.close();

            String[] header = csvEntries.get(0);
            String[] row = csvEntries.size() > 1 ? csvEntries.get(csvEntries.size() - 1) : new String [header.length];

            // find first "Depression_Q1" column
            int qStart = findColumnIndex(header, "BDI_Q1");

            // write users answer into correct column
            if (qStart >= 0) {
                int questionIndex = Integer.parseInt(num_question) -1;
                int targetCol = qStart + questionIndex;
                row[targetCol] = rating.equals("skip") ? "-1" : rating;
            }

            // rewrite all of stored scores
            if (qStart >= 0) {
                for (int i = 0; i < bdiQuestionScores.length; i++) {
                    row[qStart + i] = bdiQuestionScores[i];
                }
            }

            CSVWriter writer = new CSVWriter(new FileWriter(filePath));
            writer.writeAll(csvEntries);
            writer.close();

        } catch (IOException | CsvException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void createAndWriteCSV_PROMIS(String filePath, String idPatient, String caseID, String oldDate, String num_question, String rating) {
        File file = new File(filePath);

        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter with ';' as separator
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            //List of task summary
            List<String[]> summ = new ArrayList<String[]>();
            summ.add(new String[] { "Patient_ID", "Time", "Case_ID"});
            summ.add(new String[] { idPatient, oldDate, caseID});
            summ.add(new String[]{});
            writer.writeAll(summ);

            // create a List which contains String array
            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[] { "Question number", "Rating" });

            // check if rating is skip and write "-1" instead
            if (rating.equals("skip")) {
                data.add(new String[] {num_question, "-1"});
            } else {
                data.add(new String[] { num_question, rating});
            }

            writer.writeAll(data);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeDataCSV_PROMIS(String filePath, String num_question, String rating, String[] promisQuestionScores) {
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> csvEntries = reader.readAll();
            reader.close();

            String[] header = csvEntries.get(0);
            String[] row = csvEntries.size() > 1 ? csvEntries.get(csvEntries.size() - 1) : new String [header.length];

            // find first "Depression_Q1" column
            int qStart = findColumnIndex(header, "PROMIS_Q1");

            // write users answer into correct column
            if (qStart >= 0) {
                int questionIndex = Integer.parseInt(num_question) -1;
                int targetCol = qStart + questionIndex;
                row[targetCol] = rating.equals("skip") ? "-1" : rating;
            }

            // rewrite all of stored scores
            if (qStart >= 0) {
                for (int i = 0; i < promisQuestionScores.length; i++) {
                    row[qStart + i] = promisQuestionScores[i];
                }
            }

            CSVWriter writer = new CSVWriter(new FileWriter(filePath));
            writer.writeAll(csvEntries);
            writer.close();

        } catch (IOException | CsvException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createAndWriteCSV_QOL(String filePath, String idPatient, String caseID, String oldDate, String qol, String num_question, String rating)
    {

        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);


        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter with ';' as separator
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);


            //List of task summary
            List<String[]> summ = new ArrayList<String[]>();
            summ.add(new String[] { "Patient_ID", "Date", "Case_ID"});
            summ.add(new String[] { idPatient, oldDate, caseID});
            summ.add(new String[]{});
            writer.writeAll(summ);


            // create a List which contains String array
            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[] {"QOL", "Question number", "Rating" });

            // check if rating is skip and write "-1" instead
            if (rating.equals("skip")) {
                data.add(new String[] {num_question, "-1"});
            } else {
                data.add(new String[] { num_question, rating});
            }

            writer.writeAll(data);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeDataCSV_QOL(String filePath, String qol, String num_question, String rating) {

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // create CSVReader with FileReader and custom CSVParser
            CSVReader reader  = new CSVReaderBuilder(new FileReader(filePath)).withCSVParser(csvParser).build();

            List<String[]> csvEntries = reader.readAll();
            String[] row;

            if (csvEntries.isEmpty()) {
                row = new String[Integer.parseInt(num_question)];
                for (int i = 0; i < row.length; i++) {
                    row[i] = "0";
                }

                csvEntries.add(row);
            } else {
                row = csvEntries.get(csvEntries.size() - 1);
                if (row.length < Integer.parseInt(num_question)) {
                    String[] newRow = new String[Integer.parseInt(num_question)];
                    for (int i = 0; i < newRow.length; i++) {
                        newRow[i] = "0";
                    }

                    csvEntries.add(newRow);
                    row = newRow;
                }
            }

            int currentQuestionColumnIndex = Integer.parseInt(num_question) -1;
            if (rating.equals("skip")) {
                row[currentQuestionColumnIndex] = "-1";
            } else {
                row[currentQuestionColumnIndex] = rating;
            }

            // FileWriter outputfile = new FileWriter(filePath, true);
            CSVWriter writer = new CSVWriter(new FileWriter(filePath));
            writer.writeAll(csvEntries);

            writer.close();

            reader.close();
        }
        catch (IOException | CsvException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createAndWriteCSV_sleep(String filePath, String idPatient, String caseID, String oldDate, String num_question, String rating) {
        File file = new File(filePath);

        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);


            // create CSVWriter with ';' as separator
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            //List of task summary
            List<String[]> summ = new ArrayList<String[]>();
            summ.add(new String[] { "Patient_ID", "Date", "Case_ID"});
            summ.add(new String[] { idPatient, oldDate, caseID});
            summ.add(new String[]{});
            writer.writeAll(summ);

            // create a List which contains String array
            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[] { "Question number", "Rating" });

            // check if rating is skip and write "-1" instead
            if (rating.equals("skip")) {
                data.add(new String[] {num_question, "-1"});
            } else {
                data.add(new String[] { num_question, rating});
            }

            writer.writeAll(data);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeDataCSV_sleep(String filePath, String num_question, String rating, String[] sleepQuestionScores) {
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> csvEntries = reader.readAll();
            reader.close();

            String[] header = csvEntries.get(0);
            String[] row = csvEntries.size() > 1 ? csvEntries.get(csvEntries.size() - 1) : new String [header.length];

            // find first "Depression_Q1" column
            int qStart = findColumnIndex(header, "Sleep_Q1");

            // write users answer into correct column
            if (qStart >= 0) {
                int questionIndex = Integer.parseInt(num_question) -1;
                int targetCol = qStart + questionIndex;
                row[targetCol] = rating.equals("skip") ? "-1" : rating;
            }

            // rewrite all of stored scores
            if (qStart >= 0) {
                for (int i = 0; i < sleepQuestionScores.length; i++) {
                    row[qStart + i] = sleepQuestionScores[i];
                }
            }

            CSVWriter writer = new CSVWriter(new FileWriter(filePath));
            writer.writeAll(csvEntries);
            writer.close();


        } catch (IOException | CsvException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createAndWriteCSV_FSMC(String filePath, String idPatient, String caseID, String oldDate, String num_question, String rating) {
        File file = new File(filePath);

        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter with ';' as separator
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            //List of task summary
            List<String[]> summ = new ArrayList<String[]>();
            summ.add(new String[] { "Patient_ID", "Date", "Case_ID"});
            summ.add(new String[] { idPatient, oldDate, caseID});
            summ.add(new String[]{});
            writer.writeAll(summ);

            // create a List which contains String array
            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[] { "Question number", "Rating" });

            // check if rating is skip and write "-1" instead
            if (rating.equals("skip")) {
                data.add(new String[] {num_question, "-1"});
            } else {
                data.add(new String[] { num_question, rating});
            }

            writer.writeAll(data);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeDataCSV_FSMC(String filePath, String num_question, String rating, String[] fsmcQuestionScores) {
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(filePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> csvEntries = reader.readAll();
            reader.close();

            String[] header = csvEntries.get(0);
            String[] row = csvEntries.size() > 1 ? csvEntries.get(csvEntries.size() - 1) : new String [header.length];

            // find first "Depression_Q1" column
            int qStart = findColumnIndex(header, "FSMC_Q1");

            // write users answer into correct column
            if (qStart >= 0) {
                int questionIndex = Integer.parseInt(num_question) -1;
                int targetCol = qStart + questionIndex;
                row[targetCol] = rating.equals("skip") ? "-1" : rating;
            }

            // rewrite all of stored scores
            if (qStart >= 0) {
                for (int i = 0; i < fsmcQuestionScores.length; i++) {
                    row[qStart + i] = fsmcQuestionScores[i];
                }
            }

            CSVWriter writer = new CSVWriter(new FileWriter(filePath));
            writer.writeAll(csvEntries);
            writer.close();

        } catch (IOException | CsvException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void modifyCSVInfos_FCSM(String csvFilePath, String done, String  score, boolean skip, boolean skip_questionnaire, int numberQuestion, int skipped_question, String[] fsmcQuestionScores, String rating){

        try  {

            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> rows = reader.readAll();
            reader.close();
            if (rows.size() < 2) return;

            String[] header = rows.get(0);
            String[] row = rows.get(1);

            // locate standard columns by header name
            int colDone = findColumnIndex(header, "FSMC");
            int colScoreFsmc = findColumnIndex(header, "Score_fsmc");
            int colAnsFsmc = findColumnIndex(header, "Question_ans_fsmc");
            int colSkipFsmc = findColumnIndex(header, "Skipped_question_fsmc");
            int qStart = findColumnIndex(header, "FSMC_Q1");
            int maxQ = fsmcQuestionScores.length;

            // 3) Bound the current question index
            int currentQ = Math.max(1, Math.min(numberQuestion, maxQ));

            // 4) Write the newest answer
            row[qStart + (currentQ - 1)] = skip ? "-1" : rating;


            // 5) Re‐write the full in-memory array to keep everything in sync
            for (int i = 0; i < maxQ; i++) {
                row[qStart + i] = fsmcQuestionScores[i];
            }

            // 6) Compute the “next” pointer (0 when done)
            boolean finished = "done".equals(done);
            int nextQ = finished ? 0 : Math.min(currentQ + 1, maxQ);

            // update the summary fields
            row[colDone] = done;
            row[colScoreFsmc] = score;
            row[colAnsFsmc] = String.valueOf(numberQuestion);

            if (skip) row[colSkipFsmc] = String.valueOf(skipped_question);

            if (skip_questionnaire) {
                row[colAnsFsmc] = "0";
                row[colSkipFsmc] = "0";
            }

            // write back the CSV
            try(CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
                writer.writeAll(rows);
            }

            // keep in-memory flag in sync
            InfoFile.fsmc = done;

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reinit_questionnaire_FSMC(Context context) {
        String csvFilePath = FileManager.getInfoFilename(context);

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> rows = reader.readAll();
            reader.close();
            if (rows.size() < 2) return;


            String[] header = rows.get(0);
            String[] row = rows.get(1);

            // find four summary columns by name
            int colDone = findColumnIndex(header, "FSMC");
            int colScore = findColumnIndex(header, "Score_fsmc");
            int colAns = findColumnIndex(header, "Question_ans_fsmc");
            int colSkip = findColumnIndex(header, "Skipped_question_fsmc");

            // find first per-question column
            int qStart = findColumnIndex(header, "FSMC_Q1");

            if (colDone < 0 || colScore < 0 || colAns < 0 || colSkip < 0 || qStart < 0) {
                Log.e("WriteCSV", "reinit_questionnaire_FSMC: columns not found in CSV file");
                return;
            }

            // reset summary flags
            row[colDone] = "null";
            row[colScore] = "0";
            row[colAns] = "0";
            row[colSkip] = "0";

            // zero out every FSMC_Q# column
            int numQs = 20;
            for (int i = 0; i < numQs; i++) {
                row[qStart + i] = "0";
            }

            // write it back
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
                writer.writeAll(rows);
            }

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifyCSVInfos_Promis(String csvFilePath, String categorie, int numberQuestion, int skipped_question, String done, String  score, boolean skip, boolean skip_questionnaire, String[] promisQuestionScores, String rating){
        try {

            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> rows = reader.readAll();
            reader.close();
            if (rows.size() < 2) return;

            // find header row
            String[] header = rows.get(0);
            String[] row = rows.get(1);

            // locate standard columns by header name
            int colDone = findColumnIndex(header,"PROMIS");
            int colScorePhsyical = findColumnIndex(header,"Score_promis_physical");
            int colScoreMental = findColumnIndex(header,"Score_promis_mental");
            int colAns = findColumnIndex(header, "Question_ans_promis");
            int colSkip = findColumnIndex(header, "Skipped_question_promis");
            int qStart = findColumnIndex(header,"PROMIS_Q1");
            int maxQ = promisQuestionScores.length;

            // 3) Bound the current question index
            int currentQ = Math.max(1, Math.min(numberQuestion, maxQ));

            // 4) Write the newest answer
            row[qStart + (currentQ - 1)] = skip ? "-1" : rating;

            // 5) Re‐write the full in-memory array to keep everything in sync
            for (int i = 0; i < maxQ; i++) {
                row[qStart + i] = promisQuestionScores[i];
            }

            // 6) Compute the “next” pointer (0 when done)
            boolean finished = "done".equals(done);
            int nextQ = finished ? 0 : Math.min(currentQ + 1, maxQ);

            // update summary fields
            row[colDone] = done;
            row[colAns] = String.valueOf(numberQuestion);
            if ("physical".equals(categorie)) {
                row[colScorePhsyical] = score;

            } else if ("mental".equals(categorie)) {
                row[colScoreMental] = score;
            }

            if (skip) row[colSkip] = String.valueOf(skipped_question);

            if (skip_questionnaire) {
                row[colAns] = "0";
                row[colSkip] = "0";
            }

            // Write back the CSV
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
                writer.writeAll(rows);
            }

            // keep in-memory flag in sync
            InfoFile.promis = done;

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reinit_questionnaire_Promis(Context context){
        String csvFilePath = FileManager.getInfoFilename(context);
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> rows = reader.readAll();
            reader.close();
            if (rows.size() < 2) return;

            String[] header = rows.get(0);
            String[] row = rows.get(1);

            // find four summary columns by name
            int colDone = findColumnIndex(header, "PROMIS");
            int colScorePhysical = findColumnIndex(header, "Score_promis_physical");
            int colScoreMental = findColumnIndex(header, "Score_promis_mental");
            int colAns = findColumnIndex(header, "Question_ans_promis");
            int colSkip = findColumnIndex(header, "Skipped_question_promis");

            // find first per-question column
            int qStart = findColumnIndex(header, "PROMIS_Q1");

            if (colDone < 0 || colScorePhysical < 0 || colScoreMental < 0 || colAns < 0 || colSkip < 0 || qStart < 0) {
                Log.e("WriteCSV", "reinit_questionnaire_Promis: columns not found in CSV file");
                return;
            }

            // reset summary flags
            row[colDone] = "null";
            row[colScorePhysical] = "0";
            row[colScoreMental] = "0";
            row[colAns] = "0";
            row[colSkip] = "0";

            // zero out every PROMIS_Q# column
            int numQs = 10;
            for (int i = 0; i < numQs; i++) {
                row[qStart + i] = "0";
            }

            // write it back
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
                writer.writeAll(rows);
            }

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifyCSVGeneralQOL(Context context, int skipped_question, String done, String  score, String answered, boolean skip, String[] qolQuestionScores){

        String csvFilePath = FileManager.getInfoFilename(context);

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> rows = reader.readAll();
            reader.close();
            if (rows.size() < 2) return;

            String[] header = rows.get(0);
            String[] row = rows.get(1);

            // locate standard columns by header name
            int colDone = findColumnIndex(header, "QOL");
            int colScoreQOL = findColumnIndex(header, "Score_qol");
            int colAnsQOL = findColumnIndex(header, "Question_ans_qol");
            int colSkipQOL = findColumnIndex(header, "Skipped_question_qol");
            int qStart = findColumnIndex(header, "QOL_Q1");
            int maxQ = qolQuestionScores.length;

            // parse current question
            int currentQ = 1;
            try { currentQ = Integer.parseInt(answered); }
            catch (NumberFormatException e) { currentQ = 1; }

            // write just answered
            if (qStart >= 0 && currentQ >= 1 && currentQ <= maxQ) {
                int thisCol = qStart + (currentQ -1);
                row[thisCol] = skip ? "-1" : qolQuestionScores[currentQ - 1];
            }

            // rewrite entire array back
            if (qStart >= 0) {
                for (int i = 0; i < maxQ; i++) {
                    row[qStart + i] = qolQuestionScores[i];
                }
            }

            // compute NEXT question index
            boolean finished = done.equals("done");
            int nextQ = finished ? 0 : Math.min(currentQ + 1, maxQ);

            // update summary fields
            row[colDone] = done;
            row[colScoreQOL] = score;
            row[colAnsQOL] = String.valueOf(nextQ);

            if(skip) row[colSkipQOL] = String.valueOf(skipped_question);

            try(CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
                writer.writeAll(rows);
            }

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifyCSVInfos_QQL(Context context, int numberQuestion, int skipped_question_qol, String done, String  score, String qol, boolean skip, boolean skip_questionnaire, String[] qolXQuestionScores, String rating){

        String csvFilePath = FileManager.getInfoFilename(context);

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> rows = reader.readAll();
            reader.close();

            if (rows.size() < 2) return;

            String[] header = rows.get(0);
            String[] row = rows.get(1);

            // build exact header names for QOL
            String upper = qol.toUpperCase(); // e.g. "QOL3"
            String colDoneName = upper;
            String colScoreName = "Score" + qol;
            String colAnsName = "Question_ans" + qol;
            String colSkipName = "Skipped_question" + qol;
            String qPrefix = upper + "_Q"; // "QOL3_Q"

            // find them dynamically
            int colDone = findColumnIndex(header, colDoneName);
            int colScore = findColumnIndex(header, colScoreName);
            int colAns = findColumnIndex(header, colAnsName);
            int colSkip = findColumnIndex(header, colSkipName);
            int qStart = findColumnIndex(header, qPrefix + "1");

            int maxQ = qolXQuestionScores.length;

            if (colDone < 0 || colScore < 0 || colAns < 0 || colSkip < 0 || qStart < 0) {
                Log.e("TEST", "One or more columns not found in CSV file");
                return;
            }

            // incoming numberQuestion is the one we just answered
            int currentQ = Math.max(1, Math.min(numberQuestion, maxQ));

            // write single new answer into proper cell
            row[qStart + (currentQ - 1)] = skip ? "-1" : rating;
            // rewrite entire stored array
            for (int i = 0; i < maxQ; i++) {
                row[qStart + i] = qolXQuestionScores[i];
            }

            // compute next point
            boolean finished = done.equals("done");
            int nextQ = finished ? 0 : Math.min(currentQ + 1, maxQ);

            // summary
            row[colDone] = done;
            row[colScore] = score;

            if (!skip_questionnaire) {
                row[colAns] = String.valueOf(numberQuestion);
            }

            if (skip && !skip_questionnaire) {
                row[colSkip] = String.valueOf(skipped_question_qol);
            }

            if (skip_questionnaire) {
                row[colAns] = "0";
                row[colSkip] = "0";
            }

            // per-question scores
            for (int i = 0; i < qolXQuestionScores.length; i++) {
                row[qStart + i] = qolXQuestionScores[i];
            }

            // write back
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
                writer.writeAll(rows);
            }

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reinit_questionnaire_QQL(Context context) {
        String csvFilePath = FileManager.getInfoFilename(context);

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> rows = reader.readAll();
            reader.close();
            if (rows.size() < 2) return;

            String[] header = rows.get(0);
            String[] row = rows.get(1);

            // find four summary columns by name
            int colDone = findColumnIndex(header, "QOL");
            int colScore = findColumnIndex(header, "Score_qol");
            int colAns = findColumnIndex(header, "Question_ans_qol");
            int colSkip = findColumnIndex(header, "Skipped_question_qol");

            // find first per-question column
            int qStart = findColumnIndex(header, "QOL_Q1");

            if (colDone < 0 || colScore < 0 || colAns < 0 || colSkip < 0 || qStart < 0) {
                Log.e("WriteCSV", "reinit_questionnaire_QQL: columns not found in CSV file");
                return;
            }

            // reset summary flags
            row[colDone] = "null";
            row[colScore] = "0";
            row[colAns] = "0";
            row[colSkip] = "0";

            // zero out every QOL_Q# column
            for (int i = 0; i < 10; i++) {
                row[qStart + i] = "0";
            }

            // reset each of the 10 sub-scales
            for (int n = 1; n <= 10; n++) {
                String base = "QOL" + n;
                String lowBase = base.toLowerCase();

                int sDone = findColumnIndex(header, base);
                int sScore = findColumnIndex(header, "Score_" + lowBase);
                int sAns = findColumnIndex(header, "Question_ans_" + lowBase);
                int sSkip = findColumnIndex(header, "Skipped_question_" + lowBase);
                int sStart = findColumnIndex(header, base + "_Q1");
                int count = QOL_SUB_COUNTS[n-1];

                if (sDone >= 0) row[sDone] = "null";
                if (sScore >= 0) row[sScore] = "0";
                if (sAns >= 0) row[sAns] = "0";
                if (sSkip >= 0) row[sSkip] = "0";

                if (sStart >= 0) {
                    for (int i = 0; i < count; i++) {
                        row[sStart + i] = "0";
                    }
                }
            }

            // write it back
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
                writer.writeAll(rows);
            }

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifyCSVInfos_Sleep(Context context, int numberQuestion, int skipped_question, String done, String  score, boolean skip, boolean skip_questionnaire, String[] sleepQuestionScores, String rating){

        String csvFilePath = FileManager.getInfoFilename(context);
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> rows = reader.readAll();
            reader.close();
            if (rows.size() < 2) return;

            String[] header = rows.get(0);
            String[] row = rows.get(1);

            // locate standard columns by header name
            int colDone = findColumnIndex(header, "Sleep");
            int colScoreSleep = findColumnIndex(header, "Score_sleep");
            int colAnsSleep = findColumnIndex(header, "Question_ans_sleep");
            int colSkipSleep = findColumnIndex(header, "Skipped_question_sleep");
            int qStart = findColumnIndex(header, "Sleep_Q1");
            int maxQ = sleepQuestionScores.length;

            // 3) Bound the current question index
            int currentQ = Math.max(1, Math.min(numberQuestion, maxQ));

            // 4) Write the newest answer
            row[qStart + (currentQ - 1)] = skip ? "-1" : rating;

            // 5) Re‐write the full in-memory array to keep everything in sync
            for (int i = 0; i < maxQ; i++) {
                row[qStart + i] = sleepQuestionScores[i];
            }

            // 6) Compute the “next” pointer (0 when done)
            boolean finished = "done".equals(done);
            int nextQ = finished ? 0 : Math.min(currentQ + 1, maxQ);

            // update the summary fields
            row[colDone] = done;
            row[colScoreSleep] = score;
            row[colAnsSleep] = String.valueOf(numberQuestion);

            if (skip) row[colSkipSleep] = String.valueOf(skipped_question);
            if (skip_questionnaire) {
                row[colAnsSleep] = "0";
                row[colSkipSleep] = "0";
            }

            // write back the CSV
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
                writer.writeAll(rows);
            }

            // keep in memory flag in sync
            InfoFile.sleep = done;

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reinit_questionnaire_Sleep(Context context) {
        String csvFilePath = FileManager.getInfoFilename(context);

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> rows = reader.readAll();
            reader.close();
            if (rows.size() < 2) return;

            String[] header = rows.get(0);
            String[] row = rows.get(1);

            // find four summary columns by name
            int colDone = findColumnIndex(header, "Sleep");
            int colScore = findColumnIndex(header, "Score_sleep");
            int colAns = findColumnIndex(header, "Question_ans_sleep");
            int colSkip = findColumnIndex(header, "Skipped_question_sleep");

            // find first per-question column
            int qStart = findColumnIndex(header, "Sleep_Q1");

            if (colDone < 0 || colScore < 0 || colAns < 0 || colSkip < 0 || qStart < 0) {
                Log.e("WriteCSV", "reinit_questionnaire_Sleep: columns not found in CSV file");
                return;
            }

            // reset the summary flags
            row[colDone] = "null";
            row[colScore] = "0";
            row[colAns] = "0";
            row[colSkip] = "0";

            // zero out every Sleep_Q# column
            int numQs = 8;
            for (int i = 0; i < numQs; i++) {
                row[qStart + i] = "0";
            }

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifyCSVInfos_BDI(Context context, int numberQuestion, int skipped_question, String done, String  score, boolean skip, boolean skip_questionnaire, String[] bdiQuestionScores, String rating) {

        String csvFilePath = FileManager.getInfoFilename(context);
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> rows = reader.readAll();
            reader.close();
            if (rows.size() < 2) return;

            String[] header = rows.get(0);
            String[] row = rows.get(1);

            // locate standard columns by header name
            int colDone = findColumnIndex(header, "BDI_II");
            int colScoreBDI = findColumnIndex(header, "Score_bdi" );
            int colAnsBDI = findColumnIndex(header, "Question_ans_bdi");
            int colSkipBDI = findColumnIndex(header, "Skipped_question_bdi");
            int qStart = findColumnIndex(header, "BDI_Q1" );
            int maxQ = bdiQuestionScores.length;

            // 3) Bound the current question index
            int currentQ = Math.max(1, Math.min(numberQuestion, maxQ));

            // 4) Write the newest answer
            row[qStart + (currentQ - 1)] = skip ? "-1" : rating;

            // 5) Re‐write the full in-memory array to keep everything in sync
            for (int i = 0; i < maxQ; i++) {
                row[qStart + i] = bdiQuestionScores[i];
            }

            // 6) Compute the “next” pointer (0 when done)
            boolean finished = "done".equals(done);
            int nextQ = finished ? 0 : Math.min(currentQ + 1, maxQ);

            // update the summary fields
            row[colDone] = done;
            row[colScoreBDI] = score;
            row[colAnsBDI] = String.valueOf(numberQuestion);

            if (skip) row[colSkipBDI] = String.valueOf(skipped_question);
            if (skip_questionnaire) {
                row[colAnsBDI] = "0";
                row[colSkipBDI] = "0";
            }

            // write back the CSV
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
                writer.writeAll(rows);
            }

            // keep in memory flag in sync
            InfoFile.bdi = done;

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reinit_questionnaire_BDI(Context context){
        String csvFilePath = FileManager.getInfoFilename(context);

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> rows = reader.readAll();
            reader.close();
            if (rows.size() < 2) return;

            String[] header = rows.get(0);
            String[] row = rows.get(1);

            // find four summary columns by name
            int colDone = findColumnIndex(header, "BDI_II");
            int colScore = findColumnIndex(header, "Score_bdi");
            int colAns = findColumnIndex(header, "Question_ans_bdi");
            int colSkip = findColumnIndex(header, "Skipped_question_bdi");

            // find first per-question column
            int qStart = findColumnIndex(header, "BDI_Q1");

            if (colDone <0 || colScore < 0 || colAns < 0 || colSkip < 0 || qStart < 0) {
                Log.e("WriteCSV", "reinit_questionnaire_BDI: columns not found in CSV file");
                return;
            }

            // reset summmary flags
            row[colDone] = "null";
            row[colScore] = "0";
            row[colAns] = "0";
            row[colSkip] = "0";

            // zero out every BDI_Q# column
            int numQs = 21;
            for (int i = 0; i < numQs; i++) {
                row[qStart + i] = "0";
            }

            // write it back
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
                writer.writeAll(rows);
            }

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifyCSVInfos_Depression(Context context, int numberQuestion, int skipped_question, String done, String  score, String category, boolean skip, boolean skip_questionnaire, String[] depressionQuestionScores, String rating){

        String csvFilePath = FileManager.getInfoFilename(context);

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();
            List<String[]> rows = reader.readAll();
            reader.close();
            if (rows.size() < 2) return;

            String[] header = rows.get(0);
            String[] row = rows.get(1);

            // locate standard columns by header name
            int colDone = findColumnIndex(header, "Depression_Anxiety");
            int colScoreDep = findColumnIndex(header, "Score_depression");
            int colScoreAnx = findColumnIndex(header, "Score_anxiety");
            int colAns = findColumnIndex(header, "Question_ans_dep");
            int colSkipDep = findColumnIndex(header, "Skipped_question_dep");
            int colSkipAnx = findColumnIndex(header, "Skipped_question_anx");
            int qStart = findColumnIndex(header, "Depression_Q1");
            int maxQ = depressionQuestionScores.length;

            // 3) Bound the current question index
            int currentQ = Math.max(1, Math.min(numberQuestion, maxQ));

            // 4) Write the newest answer
            row[qStart + (currentQ - 1)] = skip ? "-1" : rating;

            // 5) Re‐write the full in-memory array to keep everything in sync
            for (int i = 0; i < maxQ; i++) {
                row[qStart + i] = depressionQuestionScores[i];
            }

            // 6) Compute the “next” pointer (0 when done)
            boolean finished = "done".equals(done);
            int nextQ = finished ? 0 : Math.min(currentQ + 1, maxQ);

            // update the summary fields
            row[colDone] = done;
            row[colScoreDep] = score;
            row[colScoreAnx] = score;
            row[colAns] = String.valueOf(numberQuestion);

            if (skip) {
                row[colSkipDep] = String.valueOf(skipped_question);
                row[colSkipAnx] = String.valueOf(skipped_question);
            }
            if (skip_questionnaire) {
                row[colAns] = "0";
                row[colSkipDep] = "0";
                row[colSkipAnx] = "0";
            }

            // Write back the CSV
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
                writer.writeAll(rows);
            }

            // keep in-memory flag in sync
            InfoFile.depression = done;
        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reinit_questionnaire_Depression(Context context){
        String csvFilePath = FileManager.getInfoFilename(context);
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> rows = reader.readAll();
            reader.close();
            if (rows.size() < 2) return;

            String[] header = rows.get(0);
            String[] row = rows.get(1);

            // find four summary columns by name
            int colDone = findColumnIndex(header, "Depression_Anxiety");
            int colScoreDep = findColumnIndex(header, "Score_depression");
            int colScoreAnx = findColumnIndex(header, "Score_anxiety");
            int colAns = findColumnIndex(header, "Question_ans_dep");
            int colSkipDep = findColumnIndex(header, "Skipped_question_dep");
            int colSkipAnx = findColumnIndex(header, "Skipped_question_anx");

            // find first per-question column
            int qStart = findColumnIndex(header, "Depression_Q1");

            if (colDone < 0 || colScoreDep < 0 || colScoreAnx < 0 || colAns < 0 || colSkipDep < 0 || colSkipAnx < 0 || qStart < 0) {
                Log.e("WriteCSV", "reinit_questionnaire_Depression: columns not found in CSV file");
                return;
            }

            // reset summary flags
            row[colDone] = "null";
            row[colScoreDep] = "0";
            row[colScoreAnx] = "0";
            row[colAns] = "0";

            // zero out every Depression_Q# column
            int numQs = 14;
            for (int i = 0; i < numQs; i++) {
                row[qStart + i] = "0";
            }

            // Write it back
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
                writer.writeAll(rows);
            }

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void reinit_questionnaire_Fatigue(Context context){
        String csvFilePath = FileManager.getInfoFilename(context);

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> rows = reader.readAll();
            reader.close();
            if (rows.size() < 2) return;

            String[] header = rows.get(0);
            String[] row = rows.get(1);

            // find four summary columns by name
            int colDone = findColumnIndex(header, "Fatigue");
            int colScore = findColumnIndex(header, "Score_fatigue");
            int colAns = findColumnIndex(header, "Question_ans_fatigue");
            int colSkip = findColumnIndex(header, "Skipped_question_fatigue");

            // find first per-question column
            int qStart = findColumnIndex(header, "Fatigue_Q1");

            if (colDone < 0 || colScore < 0 || colAns < 0 || colSkip < 0 || qStart < 0) {
                Log.e("WriteCSV", "reinit_questionnaire_Fatigue: columns not found in CSV file");
                return;
            }

            // reset summary flags
            row[colDone] = "null";
            row[colScore] = "0";
            row[colAns] = "0";
            row[colSkip] = "0";

            // zero out every Fatigue_Q# column
            int numQs = 9;
            for (int i = 0; i < numQs; i++) {
                row[qStart + i] = "0";
            }

            // write it back
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
                writer.writeAll(rows);
            }

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifyCSVInfos_Fatigue(Context context, int numberQuestion, int skipped_question, String done, String  score, boolean skip, boolean skip_questionnaire, String[] fatigueQuestionScores, String rating){

        Log.d("TEST", "modifyCSVInfos_Fatigue() - Start - numberQuestion: " + numberQuestion + ", skipped_question: " + skipped_question + ", done: " + done + ", score: " + score + ", skip: " + skip + ", skip_questionnaire: " + skip_questionnaire + ", fatigueQuestionScores: " + Arrays.toString(fatigueQuestionScores));

        String csvFilePath = FileManager.getInfoFilename(context);
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> rows = reader.readAll();
            reader.close();
            if (rows.size() < 2) return;

            String[] header = rows.get(0);
            String[] row = rows.get(1);

            // locate standard columns by header name
            int colDone = findColumnIndex(header, "Fatigue");
            int colScore = findColumnIndex(header, "Score_fatigue");
            int colAns = findColumnIndex(header, "Question_ans_fatigue");
            int colSkip = findColumnIndex(header, "Skipped_question_fatigue");
            int qStart = findColumnIndex(header, "Fatigue_Q1");
            int maxQ = fatigueQuestionScores.length;

            // 3) Bound the current question index
            int currentQ = Math.max(1, Math.min(numberQuestion, maxQ));

            // 4) Write the newest answer
            row[qStart + (currentQ - 1)] = skip ? "-1" : rating;

            // 5) Re‐write the full in-memory array to keep everything in sync
            for (int i = 0; i < maxQ; i++) {
                row[qStart + i] = fatigueQuestionScores[i];
            }

            // 6) Compute the “next” pointer (0 when done)
            boolean finished = "done".equals(done);
            int nextQ = finished ? 0 : Math.min(currentQ + 1, maxQ);

            // update summary fields
            row[colDone] = done;
            row[colScore] = score;
            row[colAns] = String.valueOf(numberQuestion);

            if (skip) row[colSkip] = String.valueOf(skipped_question);
            if (skip_questionnaire) {
                row[colAns] = "0";
                row[colSkip] = "0";
            }

            // write back the CSV
            try( CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
                writer.writeAll(rows);
            }

            // keep in memory flag in sync
            InfoFile.fatigue = done;

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean checkFileName(String outputFileName, String filePath) {
        boolean flag = false;
        File folder = new File(filePath);
        File[] listOfFiles = folder.listFiles();
        String files;

        if(!folder.exists()){
            folder.mkdirs();
            return false;
        }

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                files = listOfFiles[i].getName();
                if (files.equals(outputFileName)) {
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }

}
