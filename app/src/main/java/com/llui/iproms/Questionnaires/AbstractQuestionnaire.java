package com.llui.iproms.Questionnaires;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.llui.iproms.Enum.QuestionnaireStatus;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.llui.iproms.Model.Patient;

public abstract class AbstractQuestionnaire extends ViewModel {
    protected Patient patient = Patient.getPatient();
    protected Map<Integer, Integer> quesEntries = new LinkedHashMap<>();
    protected int[] questionIds;
    protected int currentQuestion = 0;
    protected QuestionnaireStatus progressStatus;
    protected int[] answerOptions;

    public AbstractQuestionnaire(int[] questionIds) {
        this.questionIds = questionIds;
        this.progressStatus = QuestionnaireStatus.NOT_STARTED;
        initQuestionnaireEntries();
    }

    protected void initQuestionnaireEntries() {
        for (int resId : questionIds) {
            quesEntries.put(resId, 0);
        }
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public int getCurrentQuestionId(){
        return questionIds[currentQuestion];
    }

    public boolean isLastQuestion(){
        return currentQuestion == questionIds.length-1;
    }

    protected void updateStatus(){
        if(isLastQuestion()){
            progressStatus = QuestionnaireStatus.COMPLETED;
        } else {
            progressStatus = QuestionnaireStatus.IN_PROGRESS;
        }
    }

    public boolean isQuestionnaireDone() {
        return progressStatus.equals(QuestionnaireStatus.COMPLETED);
    }

    public void skipQuestionnaire(Context context){
        progressStatus = QuestionnaireStatus.SKIPPED;
        updateCSV(context);
    }

    public QuestionnaireStatus getProgressStatus() {
        return progressStatus;
    }

    public int getQuestionIdsLength(){
        return questionIds.length;
    }

    public String[] getAnswerOptions(Context context){
        return context.getResources().getStringArray(answerOptions[currentQuestion]);
    }

    public String[] getAnswerOptions(Context context, int currentQuestion){
        return context.getResources().getStringArray(answerOptions[currentQuestion]);
    }

    public abstract void skipQuestion(Context context);

    public abstract void startNextQuestion(Context context, int rating);

    protected abstract void updateCSV(Context context);

    public abstract void readCSV(Context context);

    public abstract File getQuestionnaireFile(Context context);

    protected String getQuestionText(Context context, int resId) {
        return context.getString(resId);
    }

    // CSV Writer
    protected CSVWriter getCSVWriter(File file){
        try {
            FileWriter outputfile = new FileWriter(file, false);
            return new CSVWriter(outputfile, ',',
                    CSVWriter.DEFAULT_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected List<String[]> readExistingCSVFile(File file){
        try {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
            CSVReader reader = new CSVReaderBuilder(new FileReader(file))
                    .withCSVParser(csvParser)
                    .build();
            List<String[]> csvData = reader.readAll();
            reader.close();

            return csvData;
        } catch (IOException | CsvException e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
