package com.llui.iproms.Questionnaires;

import android.content.Context;
import android.util.Log;

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
import java.util.List;

import com.llui.iproms.Model.Patient;
import com.llui.iproms.Utils.FileManager;

public class InfoCycle {
    Patient patient = Patient.getPatient();

    public File getQuestionnaireFile(Context context) {
        return FileManager.getInfoFile(context);
    }

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

    public void writeInfo(Context context) {
        File file = getQuestionnaireFile(context);
        Log.d("TESTSSS", file.getAbsolutePath());
        List<String[]> csvData = new ArrayList<>();

        try {
            CSVWriter writer = getCSVWriter(file);
            csvData.add(new String[]{"Patient_ID", "Case_ID", "ClinicId", "Date"});
            csvData.add(new String[]{
                    patient.getPatientId(),
                    patient.getCaseId(),
                    patient.getClinicIdtoString(),
                    patient.getDate(),
            });

            writer.writeAll(csvData);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    /**
     * Reads the 1stDate and 2ndDate from the CSV file.
     * @return String[]{firstDate, secondDate} or null if not found.
     */
    public String readDate(Context context) {
        File file = getQuestionnaireFile(context);

        if (!file.exists()) {
            return null;
        }

        List<String[]> allRows = readExistingCSVFile(file);

        if (allRows.size() < 2) {
            return null;
        }

        String[] data = allRows.get(1);
        if (data.length < 4) {
            return null;
        }

        String firstDate = data[3];

        return firstDate;
    }
}
