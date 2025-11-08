package fr.thomas.menard.iproms.Views;

import android.util.Log;
import android.view.LayoutInflater;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.thomas.menard.iproms.FileWriter.InfoCycle;
import fr.thomas.menard.iproms.Utils.FileManager;
import fr.thomas.menard.iproms.databinding.ActivityIntroductionBinding;

public class IntroductionActivity extends BaseActivity {

    private ActivityIntroductionBinding binding;
    private static final String TAG = "IntroductionActivity";
    private InfoCycle infoCycle = new InfoCycle();

    @Override
    public void init() {
        resetCycleIfNeeded();
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

    private void listenBtnConfirm() {
        binding.btnConfirmIntro.setOnClickListener(v -> navigateToNextActivity(MainActivity.class));
    }

    /**
     * Checks if the given oldDate is older than 14 days.
     */
    private boolean isCycleExpired(String oldDate) {
        if (oldDate == null || oldDate.isEmpty()) {
            Log.d(TAG, "oldDate is null or empty; treating as new cycle");
            infoCycle.writeInfo(this);
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

        try {
            Date fileDate = sdf.parse(oldDate);
            Calendar calendar = Calendar.getInstance();

            //  Change this line for testing
            calendar.add(Calendar.DAY_OF_YEAR, -14);  // original 14-day logic
//            calendar.add(Calendar.MINUTE, -5); // ⚡ 5-minute threshold for quick testing

            Date thresholdDate = calendar.getTime();

            Log.d(TAG, "Parsed fileDate = " + fileDate + ", Threshold = " + thresholdDate);

            return fileDate.before(thresholdDate);
        } catch (ParseException e) {
            Log.e(TAG, "Failed to parse oldDate: " + oldDate, e);
            return false;
        }
    }

    /**
     * Moves all questionnaire files to an archive folder if the cycle is expired,
     * and initializes a new InfoCycle file with the new date.
     */
    private void resetCycleIfNeeded() {
        String oldDate = infoCycle.readDate(this);

        if (!isCycleExpired(oldDate)) {
            Log.d(TAG, "Cycle not expired (<= 14 days). No reset needed.");
            return;
        }

        Log.d(TAG, "Cycle expired. Archiving files and starting new cycle.");

        // --- Prepare archive folder path ---
        File baseFolder = FileManager.getSessionFolder(this);
        File archiveFolder = new File(baseFolder, "Archive/" + oldDate);
        if (!archiveFolder.exists() && !archiveFolder.mkdirs()) {
            Log.e(TAG, "Failed to create archive folder: " + archiveFolder.getAbsolutePath());
            return;
        }

        // --- Move all CSV files to archive folder ---
        File[] files = baseFolder.listFiles((dir, name) -> name.endsWith(".csv"));
        if (files != null) {
            for (File file : files) {
                File destFile = new File(archiveFolder, file.getName());
                boolean success = file.renameTo(destFile);
                Log.d(TAG, (success ? "Archived: " : "Failed to archive: ") + file.getName());
            }
        }

        infoCycle.writeInfo(this);
    }
}
