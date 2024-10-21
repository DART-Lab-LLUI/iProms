package fr.thomas.menard.iproms.Views;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import fr.thomas.menard.iproms.Utils.WriteCSV;
import fr.thomas.menard.iproms.databinding.ActivityIntroductionBinding;

public class IntroductionActivity extends BaseActivity {

    private ActivityIntroductionBinding binding;
    private String langue, oldDate;

    private WriteCSV writeCSVClass;
    private String fatigue;
    private String depression;
    private String bdi;
    private String promis;
    private String fsmc;
    private String sleep;
    private String type;


    private void initAttributes() {
        writeCSVClass = WriteCSV.getInstance(this);
    }

    private void listenBtnConfirm() {
        binding.btnConfirmIntro.setOnClickListener(v -> {
            navigateToNextActivity(MainActivity.class);
        });
    }


    private void retrieveInfos() {

        String csvFilePath = getExternalFilesDir(null).getAbsolutePath() + "/" + patientInfo.getPatientId() + "/first/infos.csv";

        Log.d("TEST", "path " + csvFilePath);
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
            int promisIndex = 17;
            int qolColumnIndex = 21;
            int sleepIndex = 66;
            int FSMCIndex = 70;

            List<String[]> csvEntries = reader.readAll();
            String[] firstRow = csvEntries.get(1);

            oldDate = firstRow[2];
            fatigue = firstRow[fatigueColumnIndex];
            depression = firstRow[depressionColumnIndex];
            bdi = firstRow[bdiIndex];
            promis = firstRow[promisIndex];
            String qol = firstRow[qolColumnIndex]; //???
            sleep = firstRow[sleepIndex];
            fsmc = firstRow[FSMCIndex];

            reader.close();
        } catch (IOException | CsvException e) {
            Log.d("TEST", "infos " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean suptwoWeeks() {
        // Parse the timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        Date fileDate = null;
        try {
            fileDate = sdf.parse(oldDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Calculate the difference between the current date and the file date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, +14); // Two weeks ago
        Date twoWeeksAgo = calendar.getTime();

        // Return true if the file date is before two weeks ago
        return fileDate == null || fileDate.before(twoWeeksAgo);
    }

    private String checkTypeScreening() {
        retrieveInfos();
        Log.d("TEST", "test " + fatigue + depression + bdi + promis + sleep + fsmc);
        if (fatigue.equals("done") && depression.equals("done") && bdi.equals("done") && promis.equals("done") && sleep.equals("done") && fsmc.equals("done")) {
                return "second";
            } else if (!suptwoWeeks()) {
                return "second";
            }
         else {
            return "first";
        }
    }

    private void checkUser() {

        String csv_path_PID = getExternalFilesDir(null).getAbsolutePath() + "/" + patientInfo.getPatientId() + "/";

        File folder = new File(csv_path_PID);

        // Check if the folder exists
        if (!folder.exists() || !folder.isDirectory()) {
            type = "first";
            // If the folder does not exist, create it
            if (folder.mkdirs()) {
                File f2 = new File(csv_path_PID + "/first");
                if (!f2.exists() || !f2.isDirectory()) {
                    if(f2.mkdirs()){
                        createInfoCsv(csv_path_PID + "/first/infos.csv");
                    }
                }
            }
        } else {
            if(checkTypeScreening().equals("first")){
                type = "first";
            } else{
                type = "second";
                File f2 = new File(csv_path_PID + "/second");
                if (!f2.exists() || !f2.isDirectory()) {
                    if(f2.mkdirs()){
                        createInfoCsv(csv_path_PID + "/second/infos.csv");
                    }
                }
            }
        }
    }

    private void createInfoCsv(String path){
        writeCSVClass.createAndWriteInfos(path,
                patientInfo.getPatientId(),
                patientInfo.getCaseId(),
                patientInfo.getDate(),
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

    @Override
    public void init() {
        initAttributes();
        checkUser();
    }

    @Override
    public void listenBtn() {
        listenBtnConfirm();
    }

    @Override
    public void setBinding() {
        binding = ActivityIntroductionBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }

    @Override
    public void prepareIntent(Intent intent) {
        intent.putExtra("langue", langue);
        intent.putExtra("type",type);
    }

    @Override
    public void processReceivedIntent(Intent intent) {
        langue = intent.getStringExtra("langue");
    }
}



