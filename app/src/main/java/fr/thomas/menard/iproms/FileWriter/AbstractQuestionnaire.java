package fr.thomas.menard.iproms.FileWriter;

import android.content.Context;

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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.thomas.menard.iproms.Enum.QuestionnaireStatus;
import fr.thomas.menard.iproms.Model.Patient;
import fr.thomas.menard.iproms.Utils.WriteCSV;

public abstract class AbstractQuestionnaire extends ViewModel {
    protected Patient patient = Patient.getPatient();
    protected Map<Integer, Integer> ques_entries = new LinkedHashMap<>();
    protected int[] questionIds;
    protected int currentQuestion = 0;
    protected QuestionnaireStatus progressStatus;

    public AbstractQuestionnaire(int[] questionIds) {
        this.questionIds = questionIds;
        this.progressStatus = QuestionnaireStatus.NOT_STARTED;
        initQuestionnaireEntries();
    }

    protected void initQuestionnaireEntries() {
        for (int resId : questionIds) {
            ques_entries.put(resId, 0);
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

    public abstract void skipQuestion(Context context);

    public abstract void startNextQuestion(Context context, int rating);

    protected abstract void updateCSV(Context context);

    public abstract void readCSV(Context context);

    public static WriteCSV getInstance(@NonNull ViewModelStoreOwner owner) {
        return new ViewModelProvider(owner, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(WriteCSV.class);
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
