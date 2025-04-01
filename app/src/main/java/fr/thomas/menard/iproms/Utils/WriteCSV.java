package fr.thomas.menard.iproms.Utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.thomas.menard.iproms.Model.Patient;

public class WriteCSV extends ViewModel {

    public static WriteCSV getInstance(@NonNull ViewModelStoreOwner owner) {

        return new ViewModelProvider(owner, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(WriteCSV.class);
    }

    public void initInfos(String path, Context context){
        Patient patientInfo = Patient.getPatient();

        // generate current timestamp in desired format
        String currentTimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        // pass currentTimestamp instead of patientInfo.getDate(context)
        createAndWriteInfos(path,
                patientInfo.getPatientId(context),
                patientInfo.getCaseId(context),
                currentTimestamp, // updated timestamp for this new cycle -> used as "Time" field
                "null", "0", "0", "0",
                "null", "0", "0", "0", "0", "0",
                "null", "0", "0", "0",
                "null", "0","0", "0", "0",
                "null", "0", "0", "0",
                "null", "0", "0", "0",
                "null", "0", "0", "0",
                "null", "0", "0", "0",
                "null", "0", "0", "0",
                "null", "0", "0", "0",
                "null", "0", "0", "0",
                "null", "0", "0", "0",
                "null", "0", "0", "0",
                "null", "0", "0", "0",
                "null", "0", "0", "0",
                "null", "0", "0", "0",
                "null", "0", "0", "0"
        );
    }


    public void createAndWriteInfos(String filePath, String patientID, String caseId, String time,
                                    String fatigue, String avg_score_fatigue, String question_ans_fatigue,String last_question_fatigue,
                                    String depression, String avg_score_dep, String score_anxiety, String question_ans_dep,String last_question_depression, String question_skipped_anx,
                                    String bdi, String avg_score_bdi, String question_ans_bdi,String last_question_bdi,
                                    String promis, String avg_score_promis_physical, String avg_score_promis_mental, String question_ans_promis,String last_question_promis,
                                    String qol, String avg_score_qol, String question_ans_qol, String skip_question_qol,
                                    String qol1, String score_qol1, String question_ans_qol1, String skip_question_qol1,
                                    String qol2, String score_qol2, String question_ans_qol2, String skip_question_qol2,
                                    String qol3, String score_qol3, String question_ans_qol3, String skip_question_qol3,
                                    String qol4, String score_qol4, String question_ans_qol4, String skip_question_qol4,
                                    String qol5, String score_qol5, String question_ans_qol5, String skip_question_qol5,
                                    String qol6, String score_qol6, String question_ans_qol6, String skip_question_qol6,
                                    String qol7, String score_qol7, String question_ans_qol7, String skip_question_qol7,
                                    String qol8, String score_qol8, String question_ans_qol8, String skip_question_qol8,
                                    String qol9, String score_qol9, String question_ans_qol9, String skip_question_qol9,
                                    String qol10, String score_qol10, String question_ans_qol10, String skip_question_qol10,
                                    String sleep, String score_Sleep, String question_ans_sleep, String skip_question_sleep,
                                    String FSMC, String score_fsmc, String question_ans_fsmc, String skip_question_fsmc) {


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
            summ.add(new String[] { "Patient_ID", "Case_ID", "Time", "Fatigue", "Score_fatigue", "Question_ans_fatigue", "Skipped_question_fatigue",
                    "Depression_Anxiety", "Score_depression", "Score_anxiety", "Question_ans_dep", "Skipped_question_dep","Skipped_question_anx",
                    "BDI_II", "Score_bdi", "Question_ans_bdi", "Skipped_question_bdi",
                    "PROMIS", "Score_promis_physical", "Score_promis_mental", "Question_ans_promis", "Skipped_question_promis",
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
                    "QOL10","Score_qol10", "Question_ans_qol10", "Skipped_question_qol10",
                    "Sleep","Score_sleep", "Question_ans_sleep", "Skipped_question_sleep",
                    "FSMC", "Score_fsmc", "Question_ans_fsmc", "Skipped_question_fsmc"});
            summ.add(new String[] { patientID, caseId, time, fatigue, avg_score_fatigue,
                    question_ans_fatigue,last_question_fatigue,
                    depression, avg_score_dep, score_anxiety, question_ans_dep, last_question_depression,question_skipped_anx,
                    bdi, avg_score_bdi, question_ans_bdi, last_question_bdi,
                    promis, avg_score_promis_physical, avg_score_promis_mental, question_ans_promis, last_question_promis,
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
                    qol10, score_qol10, question_ans_qol10, skip_question_qol10,
                    sleep, score_Sleep, question_ans_sleep, skip_question_sleep,
                    FSMC, score_fsmc, question_ans_fsmc, skip_question_fsmc});
            summ.add(new String[]{});
            writer.writeAll(summ);

            // closing writer connection
            writer.close();

        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void createAndWriteSleepResult(String filePath, String patientID, String caseId, String time,
                                          String score_Sleep) {


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
            summ.add(new String[] { "Patient_ID", "Case_ID", "Time", "score_sleep"});
            summ.add(new String[] { patientID, caseId, time, score_Sleep});
            summ.add(new String[]{});
            writer.writeAll(summ);

            // closing writer connection
            writer.close();

        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createAndWriteFSMCResult(String filePath, String patientID, String caseId, String time,
                                         String score_fsmc) {


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
            summ.add(new String[] { "Patient_ID", "Case_ID", "Time", "score_fsmc"});
            summ.add(new String[] { patientID, caseId, time, score_fsmc});
            summ.add(new String[]{});
            writer.writeAll(summ);

            // closing writer connection
            writer.close();

        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createAndWriteBDIResult(String filePath, String patientID, String caseId, String time,
                                        String score_bdi) {


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
            summ.add(new String[] { "Patient_ID", "Case_ID", "Time", "score_bdi"});
            summ.add(new String[] { patientID, caseId, time, score_bdi});
            summ.add(new String[]{});
            writer.writeAll(summ);

            // closing writer connection
            writer.close();

        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void createAndWriteResult(String filePath, String patientID, String caseId, String time,
                                     String avg_score_fatigue,
                                     String avg_score_dep, String score_anxiety,
                                     String scoreBDI,
                                     String score_physical,
                                     String score_mental,
                                     String score_qol1,
                                     String score_qol2,
                                     String score_qol3,
                                     String score_qol4,
                                     String score_qol5,
                                     String score_qol7,
                                     String score_qol8,
                                     String score_qol9,
                                     String score_qol10) {


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
            summ.add(new String[] { "Patient_ID", "Case_ID", "Time", "Score_fatigue","Score_depression", "Score_anxiety","Score_BDI",
                    "score_promis_physical",
                    "score_promis_mental",
                    "score_qol1",
                    "score_qol2",
                    "score_qol3",
                    "score_qol4",
                    "score_qol5",
                    "score_qol7",
                    "score_qol8",
                    "score_qol9",
                    "score_qol10"});
            summ.add(new String[] { patientID, caseId, time, avg_score_fatigue, avg_score_dep, score_anxiety,scoreBDI,
                    score_physical, score_mental,
                    score_qol1,
                    score_qol2,
                    score_qol3,
                    score_qol4,
                    score_qol5,
                    score_qol7,
                    score_qol8,
                    score_qol9,
                    score_qol10});
            summ.add(new String[]{});
            writer.writeAll(summ);

            // closing writer connection
            writer.close();

        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    public void createAndWriteCSV_fatigue(String filePath,String idPatient,String caseID, String date, String num_question, String rating)
    {

        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);


        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter with ';' as separator
            CSVWriter writer = new CSVWriter(outputfile, ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);


            //List of task summary
            List<String[]> summ = new ArrayList<String[]>();
            summ.add(new String[] { "Patient_ID", "Time", "Case_ID"});
            summ.add(new String[] { idPatient, date, caseID});
            summ.add(new String[]{});
            writer.writeAll(summ);


            // create a List which contains String array
            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[] { "Question number", "Rating" });
            data.add(new String[] { num_question, rating});
            writer.writeAll(data);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeDataCSV_fatigue(String filePath, String num_question, String rating)
    {

        try {
            FileWriter outputfile = new FileWriter(filePath, true);

            CSVWriter writer = new CSVWriter(outputfile, ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            String [] data = (num_question +";"+ rating).split(";");

            writer.writeNext(data);


            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void createAndWriteCSV_QOL(String filePath,String idPatient,String caseID, String date, String qol, String num_question, String rating)
    {

        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);


        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter with ';' as separator
            CSVWriter writer = new CSVWriter(outputfile, ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);


            //List of task summary
            List<String[]> summ = new ArrayList<String[]>();
            summ.add(new String[] { "Patient_ID", "Time", "Case_ID"});
            summ.add(new String[] { idPatient, date, caseID});
            summ.add(new String[]{});
            writer.writeAll(summ);


            // create a List which contains String array
            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[] {"QOL", "Question number", "Rating" });
            data.add(new String[] { qol, num_question, rating});
            writer.writeAll(data);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void writeDataCSV_QOL(String filePath, String qol, String num_question, String rating)
    {

        try {
            FileWriter outputfile = new FileWriter(filePath, true);

            CSVWriter writer = new CSVWriter(outputfile, ';',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            String [] data = (qol +";"+num_question +";"+ rating).split(";");

            writer.writeNext(data);


            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void modifyCSVInfos_FCSM(String csvFilePath, String done, String  score, boolean skip, boolean skip_questionnaire, int numberQuestion, int skipped_question){
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            int qolColumnIndex = 70;

            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[qolColumnIndex] = done;
            row[qolColumnIndex+1] = score;

            row[qolColumnIndex + 2] = String.valueOf(numberQuestion + 1);

            if(skip)
                row[qolColumnIndex+3] = String.valueOf(skipped_question + 1);

            if(skip_questionnaire){
                row[qolColumnIndex] = done;
                row[qolColumnIndex+1] = score;
                row[qolColumnIndex + 2] = "0";
                row[qolColumnIndex + 3] = "0";
            }


            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            writer.writeAll(csvEntries);
            writer.close();


            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifyCSVInfos_Promis(String csvFilePath, String categorie, int numberQuestion, int skipped_question, String done, String  score, boolean skip, boolean skip_questionnaire){
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            int qolColumnIndex = 17;


            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[qolColumnIndex] = done;
            if(categorie.equals("physical"))
                row[qolColumnIndex+1] = score;
            else if (categorie.equals("mental")) {
                row[qolColumnIndex+2] = score;
            }
            if(!skip_questionnaire)
                row[qolColumnIndex + 3] = String.valueOf(numberQuestion +1);

            if(skip && !skip_questionnaire)
                row[qolColumnIndex+4] = String.valueOf(skipped_question + 1);

            if(skip_questionnaire){
                row[qolColumnIndex + 3] = "0";
                row[qolColumnIndex + 4] = "0";
            }


            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            writer.writeAll(csvEntries);
            writer.close();


            reader.close();

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

            int qolColumnIndex = 17;


            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[qolColumnIndex] = "null";
            row[qolColumnIndex+1] = "0";
            row[qolColumnIndex+2] = "0";
            row[qolColumnIndex + 3] = "0";
            row[qolColumnIndex + 4] = "0";


            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            writer.writeAll(csvEntries);
            writer.close();


            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void modifyCSVGeneralQOL(Context context, int skipped_question, String done, String  score, String answered, boolean skip){

        String csvFilePath = FileManager.getInfoFilename(context);
        int qolColumnIndex = 21;

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[qolColumnIndex] = done;
            row[qolColumnIndex+1] = score;
            row[qolColumnIndex + 2] = answered;
            if(skip)
                row[qolColumnIndex+3] = String.valueOf(skipped_question + 1);


            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            writer.writeAll(csvEntries);
            writer.close();


            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifyCSVInfos_QQL(Context context, int numberQuestion, int skipped_question_qol, String done, String  score, String qol, boolean skip, boolean skip_questionnaire){

        String csvFilePath = FileManager.getInfoFilename(context);

        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();
            int qolColumnIndex = 17;

            switch (qol) {
                case "qol1":
                    qolColumnIndex += 4;
                    break;
                case "qol2":
                    qolColumnIndex += 8;
                    break;
                case "qol3":
                    qolColumnIndex += 12;
                    break;
                case "qol4":
                    qolColumnIndex += 16;
                    break;
                case "qol5":
                    qolColumnIndex += 20;
                    break;
                case "qol6":
                    qolColumnIndex += 24;
                    break;
                case "qol7":
                    qolColumnIndex += 28;
                    break;
                case "qol8":
                    qolColumnIndex += 32;
                    break;
                case "qol9":
                    qolColumnIndex += 36;
                    break;
                case "qol10":
                    qolColumnIndex += 40;
                    break;
            }
            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[qolColumnIndex] = done;
            row[qolColumnIndex+1] = score;

            if(!skip_questionnaire) {
                row[qolColumnIndex + 2] = String.valueOf(numberQuestion + 1);
            }

            if(skip && !skip_questionnaire)
                row[qolColumnIndex+3] = String.valueOf(skipped_question_qol + 1);


            if(skip_questionnaire){
                row[qolColumnIndex + 2] = "0";
                row[qolColumnIndex+3] = "0";
            }
            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            writer.writeAll(csvEntries);
            writer.close();

            Log.d("TEST", "row " + row[qolColumnIndex]+ row[qolColumnIndex+1]);


            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void modifyCSVInfos_Sleep(Context context, int numberQuestion, int skipped_question, String done, String  score, boolean skip, boolean skip_questionnaire){

        String csvFilePath = FileManager.getInfoFilename(context);
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            int qolColumnIndex = 66;

            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[qolColumnIndex] = done;
            row[qolColumnIndex+1] = score;
            row[qolColumnIndex + 2] = String.valueOf(numberQuestion + 1);

            if(skip)
                row[qolColumnIndex+3] = String.valueOf(skipped_question + 1);

            if(skip_questionnaire)
            {
                row[qolColumnIndex+1] = score;
                row[qolColumnIndex + 2] = "0";
                row[qolColumnIndex+3] = "0";
            }

            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            writer.writeAll(csvEntries);
            writer.close();


            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifyCSVInfos_BDI(Context context, int numberQuestion, int skipped_question, String done, String  score, boolean skip, boolean skip_questionnaire) {

        String csvFilePath = FileManager.getInfoFilename(context);
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            int qolColumnIndex = 13;

            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[qolColumnIndex] = done;
            row[qolColumnIndex + 1] = score;
            row[qolColumnIndex + 2] = String.valueOf(numberQuestion);

            if (skip)
                row[qolColumnIndex + 3] = String.valueOf(skipped_question + 1);

            if (skip_questionnaire) {
                row[qolColumnIndex + 1] = "0";
                row[qolColumnIndex + 2] = "0";
                row[qolColumnIndex + 3] = "0";
            }

            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            writer.writeAll(csvEntries);
            writer.close();


            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifyCSVInfos_Depression(Context context, int numberQuestion, int skipped_question, String done, String  score, String category, boolean skip, boolean skip_questionnaire){

        String csvFilePath = FileManager.getInfoFilename(context);
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            // Read the header to get column indices
            int depressionColumnIndex = 7;

            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[depressionColumnIndex] = done;
            if(category.equals("depression"))
                row[depressionColumnIndex+1] = score;
            else if (category.equals("skip")) {
                row[depressionColumnIndex+1] = "0";
                row[depressionColumnIndex+2] = "0";
                row[depressionColumnIndex + 3] = "0";
                row[depressionColumnIndex+4] = "0";
                row[depressionColumnIndex+5] = "0";

            }else {
                row[depressionColumnIndex + 2] = score;
            }

            if(!skip_questionnaire)
                row[depressionColumnIndex + 3] = String.valueOf(numberQuestion + 1);

            if(skip && !skip_questionnaire){
                if(category.equals("depression"))
                    row[depressionColumnIndex+4] = String.valueOf(skipped_question + 1);
                else
                    row[depressionColumnIndex+5] = String.valueOf(skipped_question + 1);


            }


            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            writer.writeAll(csvEntries);
            writer.close();


            reader.close();

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

            int qolColumnIndex = 7;


            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[qolColumnIndex] = "null";
            row[qolColumnIndex+1] = "0";
            row[qolColumnIndex + 2] = "0";
            row[qolColumnIndex + 3] = "0";
            row[qolColumnIndex + 4] = "0";
            row[qolColumnIndex + 5] = "0";


            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            writer.writeAll(csvEntries);
            writer.close();


            reader.close();

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

            int qolColumnIndex = 3;


            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[qolColumnIndex] = "null";
            row[qolColumnIndex+1] = "0";
            row[qolColumnIndex + 2] = "0";
            row[qolColumnIndex + 3] = "0";


            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            writer.writeAll(csvEntries);
            writer.close();


            reader.close();

        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifyCSVInfos_Fatigue(Context context, int numberQuestion, int skipped_question, String done, String  score, boolean skip, boolean skip_questionnaire){

        String csvFilePath = FileManager.getInfoFilename(context);
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();

            // Create a CSVReader with FileReader and custom CSVParser
            CSVReader reader = new CSVReaderBuilder(new FileReader(csvFilePath))
                    .withCSVParser(csvParser)
                    .build();

            int qolColumnIndex = 3;


            List<String[]> csvEntries = reader.readAll();
            String[] row = csvEntries.get(1);
            row[qolColumnIndex] = done;
            row[qolColumnIndex+1] = score;
            if(!skip_questionnaire)
                row[qolColumnIndex + 2] = String.valueOf(numberQuestion +1);

            if(skip && !skip_questionnaire)
                row[qolColumnIndex+3] = String.valueOf(skipped_question + 1);

            if(skip_questionnaire){
                row[qolColumnIndex + 2] = "0";
                row[qolColumnIndex + 3] = "0";
            }


            CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
            writer.writeAll(csvEntries);
            writer.close();


            reader.close();

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
