package fr.thomas.menard.iproms.Utils;

import android.content.Context;
import android.util.Log;

import java.io.File;

import fr.thomas.menard.iproms.Model.Patient;

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
        String filename = Patient.getPatient().getPatientId() + "_FSMC.csv";
        return new File(getSessionFolder(context), filename);
    }

    public static File getPromisFile(Context context  ){
        String filename = Patient.getPatient().getPatientId() + "_Promis.csv";
        return new File(getSessionFolder(context), filename);
    }

    public static File getQQLFile(Context context  ){
        String filename = Patient.getPatient().getPatientId() + "_QQL.csv";
        return new File(getSessionFolder(context), filename);
    }

    public static String getQQLFilename(Context context){
        return getFilename(getQQLFile(context));
    }

    public static boolean isQQLFileExist(Context context  ){
        return isFileExists(getQQLFile(context));
    }

    public static File getESSFile(Context context  ){
        String filename = Patient.getPatient().getPatientId() + "_ESS.csv";
        return new File(getSessionFolder(context), filename);
    }

    public static File getResultFile(Context context  ){
        String filename = Patient.getPatient().getPatientId() + "_result.csv";
        return new File(getSessionFolder(context), filename);
    }

    public static File getBDIFile(Context context  ){
        String filename = Patient.getPatient().getPatientId() + "_BDI.csv";
        return new File(getSessionFolder(context), filename);
    }

    public static File getHADSFile(Context context  ){
        String filename = Patient.getPatient().getPatientId() + "_HADS.csv";
        return new File(getSessionFolder(context), filename);
    }

    public static File getFSSFile(Context context  ){
        String filename = Patient.getPatient().getPatientId() + "_FSS.csv";
        return new File(getSessionFolder(context), filename);
    }
}
