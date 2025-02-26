package fr.thomas.menard.iproms.Views;

import static fr.thomas.menard.iproms.App.MyApplication.setType;

import android.view.LayoutInflater;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.thomas.menard.iproms.App.MyApplication;
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

    private boolean suptwoWeeks(String oldDate) {
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

    private Type checkTypeScreening() {
        ReadCSV.retrieveInfos(this);

        if (InfoFile.everythingDone()) {
                return Type.SECOND;
            } else if (!suptwoWeeks(InfoFile.oldDate)) {
                return Type.SECOND;
            }
         else {
            return Type.FIRST;
        }
    }

    private void checkUser() {
        WriteCSV writeCSVClass = WriteCSV.getInstance(this);
        setType(Type.FIRST); // Save Type persistently

        // Check if the folder exists
        if (!FileManager.isInfoFileExist(this)) {
            writeCSVClass.initInfos(FileManager.getInfoFilename(this), this);
        } else {
            if (!checkTypeScreening().equals(Type.FIRST)) {
                setType(Type.SECOND);

                if (!FileManager.isInfoFileExist(this)) {
                    writeCSVClass.initInfos(FileManager.getInfoFilename(this), this);
                }
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



