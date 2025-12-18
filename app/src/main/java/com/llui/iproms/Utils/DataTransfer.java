package com.llui.iproms.Utils;

import static com.llui.iproms.BuildConfig.MINIO_HS_ACCESS;
import static com.llui.iproms.BuildConfig.MINIO_HS_BUCKET;
import static com.llui.iproms.BuildConfig.MINIO_HS_ENDPOINT;
import static com.llui.iproms.BuildConfig.MINIO_HS_SECRET;
import static com.llui.iproms.BuildConfig.MINIO_VZ_ACCESS;
import static com.llui.iproms.BuildConfig.MINIO_VZ_BUCKET;
import static com.llui.iproms.BuildConfig.MINIO_VZ_ENDPOINT;
import static com.llui.iproms.BuildConfig.MINIO_VZ_SECRET;

import android.app.AlertDialog;

import java.io.File;

import com.llui.iproms.Model.Patient;
import com.llui.iproms.Views.BaseActivity;

public class DataTransfer {
    private MinioHelper minioHelper;
    private final BaseActivity mainActivity;
    private final Patient patientInfo;
    private boolean testing;

    public DataTransfer(BaseActivity mainActivity) {
        this.patientInfo = Patient.getPatient();
        this.mainActivity = mainActivity;

        switch (patientInfo.getClinicId()){
            case 0:
                this.minioHelper = new MinioHelper(MINIO_VZ_ENDPOINT, MINIO_VZ_ACCESS, MINIO_VZ_SECRET, MINIO_VZ_BUCKET, mainActivity);
                break;
            case 1:
                this.minioHelper = new MinioHelper(MINIO_HS_ENDPOINT, MINIO_HS_ACCESS, MINIO_HS_SECRET, MINIO_HS_BUCKET, mainActivity);
                break;
            case 2:
                testing = true;
                break;
        }
    }

    public MinioHelper getMinioClient() {
        return minioHelper;
    }

    public boolean uploadFile(File file){

        if(testing) return true;

        try {
            minioHelper.sendFileToMinio(file, uploadStatus -> mainActivity.runOnUiThread(() -> {
                int totalFiles = uploadStatus[0];
                int successfulUploads = uploadStatus[1];

                if ((totalFiles == 0) || (successfulUploads == totalFiles))  {
                    logoutMessage();
                }else {
                    tryAgainMessage();
                }
            }));
            return true;
        } catch (Exception e){
            tryAgainMessage();
        }
        return false;
    }

    public void uploadAllData(){
        runMinio();
    }

    private void runMinio(){
        if (testing) return;

        try{
            minioHelper.sendFolderToMinio(FileManager.getSessionFolder(mainActivity), patientInfo, uploadStatus -> {
                mainActivity.runOnUiThread(() -> {

                    int totalFiles = uploadStatus[0];
                    int successfulUploads = uploadStatus[1];

                    if ((totalFiles == 0) || (successfulUploads == totalFiles))  {
                        logoutMessage();
                    }else {
                        tryAgainMessage();
                    }
                });
            });
        } catch (Exception e){
            tryAgainMessage();
        }
    }


    private void logoutMessage(){
        DebugLogger.debugLog("DATA TRANSFER", "SUCCESS");
    }

    private void tryAgainMessage() {
        new AlertDialog.Builder(mainActivity)
                .setTitle("Error in Uploading.")
                .setMessage("There is an error in the uploading. Please notify a therapist about that.")
                .setNeutralButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
