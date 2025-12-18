package com.llui.iproms.Utils;

import android.content.Context;

import java.io.File;

import com.llui.iproms.Model.Patient;

public class FileManager {

    private static File createFolder(File parent, String child) {
        File directory = new File(parent, child);

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + directory.getAbsolutePath());
            }
        }
        return directory;
    }

    private static String getFilename(File file){
        return file.getAbsolutePath();
    }

    private static boolean isFileExists(File file){
        return file.exists();
    }

    public static File getSessionFolder(Context context) {
        // Get the external files directory specific to this app
        File baseDir = context.getExternalFilesDir(null);

        // Construct the patient-case directory path
        Patient patient = Patient.getPatient();

        // Construct the session path
        DebugLogger.debugLog("SessionFolder", baseDir.getAbsolutePath() + patient.getPatientId());
        return createFolder(baseDir, patient.getPatientId());
    }

    public static File getCrashLogFile(Context context, Patient patient){
        String filename = "crash_log.txt";
        return new File(getSessionFolder(context), filename);
    }

    public static String getCrashLogFilename(Context context, Patient patient){
        return getFilename(getCrashLogFile(context, patient));
    }

    public static File getInfoFile(Context context){
        String filename = "infos.csv";
        return new File(getSessionFolder(context), filename);
    }

    public static File getFSMCFile(Context context  ){
        String filename = "FSMC.csv";
        return new File(getSessionFolder(context), filename);
    }

    public static File getPromisFile(Context context  ){
        String filename = "Promis.csv";
        return new File(getSessionFolder(context), filename);
    }

    public static File getQQLFile(Context context  ){
        String filename = "QQL.csv";
        return new File(getSessionFolder(context), filename);
    }

    public static File getESSFile(Context context  ){
        String filename = "ESS.csv";
        return new File(getSessionFolder(context), filename);
    }

    public static File getBDIFile(Context context  ){
        String filename = "BDI.csv";
        return new File(getSessionFolder(context), filename);
    }

    public static File getHADSFile(Context context  ){
        String filename = "HADS.csv";
        return new File(getSessionFolder(context), filename);
    }

    public static File getFSSFile(Context context  ){
        String filename = "FSS.csv";
        return new File(getSessionFolder(context), filename);
    }
}
