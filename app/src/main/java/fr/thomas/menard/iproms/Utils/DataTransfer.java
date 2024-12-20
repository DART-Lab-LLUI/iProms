package fr.thomas.menard.iproms.Utils;

import static fr.thomas.menard.iproms.BuildConfig.MINIO_HS_ACCESS;
import static fr.thomas.menard.iproms.BuildConfig.MINIO_HS_BUCKET;
import static fr.thomas.menard.iproms.BuildConfig.MINIO_HS_ENDPOINT;
import static fr.thomas.menard.iproms.BuildConfig.MINIO_HS_SECRET;
import static fr.thomas.menard.iproms.BuildConfig.MINIO_VZ_ACCESS;
import static fr.thomas.menard.iproms.BuildConfig.MINIO_VZ_BUCKET;
import static fr.thomas.menard.iproms.BuildConfig.MINIO_VZ_ENDPOINT;
import static fr.thomas.menard.iproms.BuildConfig.MINIO_VZ_SECRET;
import static fr.thomas.menard.iproms.Utils.FileManager.getSessionFolder;

import java.io.File;

import fr.thomas.menard.iproms.Model.Patient;
import fr.thomas.menard.iproms.Views.BaseActivity;

public class DataTransfer {
    private MinioHelper minioHelper;
    private final BaseActivity mainActivity;
    private final Patient patientInfo;

    public DataTransfer(BaseActivity mainActivity) {
        this.patientInfo = Patient.getPatient();
        this.mainActivity = mainActivity;

        switch (patientInfo.getClinicId()){
            case 0:
                this.minioHelper = new MinioHelper(MINIO_VZ_ENDPOINT, MINIO_VZ_ACCESS, MINIO_VZ_SECRET, MINIO_VZ_BUCKET);
                break;
            case 1:
                this.minioHelper = new MinioHelper(MINIO_HS_ENDPOINT, MINIO_HS_ACCESS, MINIO_HS_SECRET, MINIO_HS_BUCKET);
                break;
        }
    }

    public MinioHelper getMinioClient() {
        return minioHelper;
    }

    public void uploadFile(File file){
        try {
            minioHelper.sendFileToMinio(file, uploadStatus -> {
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

    public void uploadAllData(){
        runMinio();
    }

    private void runMinio(){
        try{
            minioHelper.sendFolderToMinio(getSessionFolder(mainActivity), patientInfo, uploadStatus -> {
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
        DebugLogger.debugLog("DATA TRANSFER", "TRY AGAIN");
    }
}
