package fr.thomas.menard.iproms.Views;

import static fr.thomas.menard.iproms.App.MyApplication.setType;

import android.util.Log;
import android.view.LayoutInflater;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.thomas.menard.iproms.Enum.Type;
import fr.thomas.menard.iproms.Model.InfoFile;
import fr.thomas.menard.iproms.Utils.FileManager;
import fr.thomas.menard.iproms.Utils.ReadCSV;
import fr.thomas.menard.iproms.Utils.WriteCSV;
import fr.thomas.menard.iproms.databinding.ActivityIntroductionBinding;

public class IntroductionActivity extends BaseActivity {

    private ActivityIntroductionBinding binding;

    private void listenBtnConfirm() {
        binding.btnConfirmIntro.setOnClickListener(v -> {
            navigateToNextActivity(MainActivity.class);
        });
    }

    // checkUser() -> rely solely on isCycleExpired; without using checkTypeScreening
    private void checkUser() {
        // load current CSV data into InfoFile; ensures that all static fields in InfoFile = set from existing CSV file
        ReadCSV.retrieveInfos(this);

        // check and reset cycle if needed
        resetCycleIfNeeded();

        WriteCSV writeCSVClass = WriteCSV.getInstance(this);

        // Check if the folder exists; if NOT -> initialize it
        if (!FileManager.isInfoFileExist(this)) {
            writeCSVClass.initInfos(FileManager.getInfoFilename(this), this);
        }

        // decide type solely based on expiration and completion

        // if cycle expired -> start new cycle: FIRST
        if (isCycleExpired(InfoFile.oldDate)) {
            setType(Type.FIRST);
        }

        // if cycle not expired but all questionnaires done: SECOND
        else if (InfoFile.everythingDone(this)) {
            setType(Type.SECOND);
        }

        // otherwise, default to FIRST
        else {
            setType(Type.FIRST);
        }
    }

    private boolean isCycleExpired (String oldDate) {
        // if oldDate is null or empty -> file = new (or decide on appropriate behavior)
        if (oldDate == null || oldDate.isEmpty()) {
            Log.d("ResetCycle", "Date is null or empty; assuming file is new");
            // prevent code from trying to parse null value
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        Date fileDate = null;
        try {
            fileDate = sdf.parse(oldDate);
        } catch (ParseException e) {
            e.printStackTrace();

            // if parsing fails, decide on default behavior - here we return false
            return false;
        }

        // substract 14 days from the current date to set a two-weeks threshold (as patients might leave before one-month threshold)
        Calendar calendar = Calendar.getInstance();

        // temporarily reduce threshold -> simulate and test reset logic
        //calendar.add(Calendar.MINUTE, -1); // one minute threshold for testing

        calendar.add(Calendar.DAY_OF_YEAR, -14); // adjust threshold if needed
        Date thresholdDate = calendar.getTime();

        Log.d("ResetCycle", "Parsed fileDate = " + fileDate +
                ", parsed fileDate = " + fileDate +
                ", thresholdDate = " + thresholdDate);
        // return true if the file date is before the threshold
        return fileDate.before(thresholdDate);
    }

    private void resetCycleIfNeeded() {
        Log.d("ResetCycle", "resetCycleIfNeeded: InfoFile.oldDate = " + InfoFile.oldDate);
        if (isCycleExpired(InfoFile.oldDate)) {
            String currentFilePath = FileManager.getInfoFilename(this);
            String archivedFilePath = FileManager.getArchivedFilename(this, InfoFile.oldDate);
            File currentFile = new File(currentFilePath);
            Log.d("ResetCycle", "Attempting to archive CSV from " + currentFilePath + " to " + archivedFilePath);
            if (currentFile.renameTo(new File(archivedFilePath))) {
                // successfully archived old CSV; create new one
                WriteCSV.getInstance(this).initInfos(currentFilePath, this);
                Log.d("ResetCycle", "CSV archived to " + archivedFilePath + " and new cycle started.");
            } else {
                Log.d("ResetCycle", "Failed to archive the current CSV file");
            }
        }
    }

    @Override
    public void init() {
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
}



