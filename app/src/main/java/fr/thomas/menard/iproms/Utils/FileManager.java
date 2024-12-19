package fr.thomas.menard.iproms.Utils;

import android.content.Context;

import java.io.File;

import fr.thomas.menard.iproms.Model.MyApplication;
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
        File folder = createFolder(getSessionFolder(context), MyApplication.type.getType());
        String filename = "infos.csv";
        return new File(folder, filename);
    }

    public static String getInfoFilename(Context context) {
        return getFilename(getInfoFile(context));
    }

    public static boolean isInfoFileExist(Context context){
        return isFileExists(getInfoFile(context));
    }

    public static File getResultBDIFile(Context context  ){
        File folder = createFolder(getSessionFolder(context),   MyApplication.type.getType());
        String filename = "result_bdi.csv";
        return new File(folder, filename);
    }

    public static String getResultBDIFFilename(Context context) {
        return getFilename(getResultBDIFile(context));
    }

    public static File getFSMCFile(Context context  ){
        File folder = createFolder(getSessionFolder(context),   MyApplication.type.getType());
        String filename = Patient.getPatient().getPatientId() + "_FSMC.csv";
        return new File(folder, filename);
    }

    public static String getFSMCFilename(Context context  ) {
        return getFilename(getFSMCFile(context));
    }

    public static boolean isFSMCFileExist(Context context  ){
        return isFileExists(getFSMCFile(context));
    }

    public static File getFSMCResultFile(Context context  ){
        File folder = createFolder(getSessionFolder(context),   MyApplication.type.getType());
        String filename = Patient.getPatient().getPatientId() + "_result_fsmc.csv";
        return new File(folder, filename);
    }

    public static boolean isFSMCResultFileExist(Context context  ){
        return isFileExists(getFSMCResultFile(context));
    }

    public static File getSleepResultFile(Context context  ){
        File folder = createFolder(getSessionFolder(context),   MyApplication.type.getType());
        String filename = Patient.getPatient().getPatientId() + "_result_sleep.csv";
        return new File(folder, filename);
    }

    public static boolean isSleepResultFileExist(Context context  ){
        return isFileExists(getSleepResultFile(context));
    }

    public static File getBDIResultFile(Context context  ){
        File folder = createFolder(getSessionFolder(context),   MyApplication.type.getType());
        String filename = Patient.getPatient().getPatientId() + "_result_bdi.csv";
        return new File(folder, filename);
    }

    public static boolean isBDIResultFileExist(Context context  ){
        return isFileExists(getBDIResultFile(context));
    }

    public static File getPromisFile(Context context  ){
        File folder = createFolder(getSessionFolder(context),   MyApplication.type.getType());
        String filename = Patient.getPatient().getPatientId() + "_Promis.csv";
        return new File(folder, filename);
    }

    public static String getPromisFilename(Context context){
        return getFilename(getPromisFile(context));
    }

    public static boolean isPromisFileExist(Context context  ){
        return isFileExists(getPromisFile(context));
    }

    public static File getQQLFile(Context context  ){
        File folder = createFolder(getSessionFolder(context),   MyApplication.type.getType());
        String filename = Patient.getPatient().getPatientId() + "_QQL.csv";
        return new File(folder, filename);
    }

    public static String getQQLFilename(Context context){
        return getFilename(getQQLFile(context));
    }

    public static boolean isQQLFileExist(Context context  ){
        return isFileExists(getQQLFile(context));
    }

    public static File getESSFile(Context context  ){
        File folder = createFolder(getSessionFolder(context),   MyApplication.type.getType());
        String filename = Patient.getPatient().getPatientId() + "_ESS.csv";
        return new File(folder, filename);
    }

    public static String getESSFilename(Context context){
        return getFilename(getESSFile(context));
    }

    public static boolean isESSFileExist(Context context  ){
        return isFileExists(getESSFile(context));
    }

    public static File getResultFile(Context context  ){
        File folder = createFolder(getSessionFolder(context),   MyApplication.type.getType());
        String filename = Patient.getPatient().getPatientId() + "_result.csv";
        return new File(folder, filename);
    }

    public static String getResultFilename(Context context){
        return getFilename(getResultFile(context));
    }

    public static boolean isResultFileExist(Context context  ){
        return isFileExists(getResultFile(context));
    }

    public static File getBDIFile(Context context  ){
        File folder = createFolder(getSessionFolder(context),   MyApplication.type.getType());
        String filename = Patient.getPatient().getPatientId() + "_BDI.csv";
        return new File(folder, filename);
    }

    public static String getBDIFilename(Context context){
        return getFilename(getBDIFile(context));
    }

    public static boolean isBDIFileExist(Context context  ){
        return isFileExists(getBDIFile(context));
    }

    public static File getHADSFile(Context context  ){
        File folder = createFolder(getSessionFolder(context),   MyApplication.type.getType());
        String filename = Patient.getPatient().getPatientId() + "_HADS.csv";
        return new File(folder, filename);
    }

    public static String getHADSFilename(Context context){
        return getFilename(getHADSFile(context));
    }

    public static boolean isHADSFileExist(Context context  ){
        return isFileExists(getHADSFile(context));
    }

    public static File getFSSFile(Context context  ){
        File folder = createFolder(getSessionFolder(context),   MyApplication.type.getType());
        String filename = Patient.getPatient().getPatientId() + "_FSS.csv";
        return new File(folder, filename);
    }

    public static String getFSSFilename(Context context){
        return getFilename(getFSSFile(context));
    }

    public static boolean isFSSFileExist(Context context  ){
        return isFileExists(getFSSFile(context));
    }
}
