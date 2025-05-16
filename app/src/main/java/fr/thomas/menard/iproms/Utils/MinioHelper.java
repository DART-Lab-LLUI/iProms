package fr.thomas.menard.iproms.Utils;

import android.content.Context;

import org.checkerframework.checker.units.qual.C;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import fr.thomas.menard.iproms.Model.Patient;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;

public class MinioHelper {
    private static final String APPNAME = "iproms";
    private MinioClient minioClient;
    private final String MINIO_ENDPOINT, MINIO_ACCESS, MINIO_SECRET, MINIO_BUCKET;
    private Context context;

    public MinioHelper(String endpoint, String access, String secret, String bucket, Context context) {

        this.MINIO_ENDPOINT = endpoint;
        this.MINIO_ACCESS = access;
        this.MINIO_SECRET = secret;
        this.MINIO_BUCKET = bucket;
        this.context = context;

        // Create a MinioClient object with the MinIO server URL, access key, and secret key
        try {
            // Initialize the MinIO client
            minioClient = MinioClient.builder()
                    .endpoint(MINIO_ENDPOINT)
                    .credentials(MINIO_ACCESS, MINIO_SECRET)
                    .build();

            if (minioClient != null) {
                DebugLogger.debugLog("MINIOTEST","MinioClient initialized successfully.");
            } else {
                DebugLogger.debugLog("MINIOTEST","Failed to initialize MinioClient.");
            } 

            // Check if bucket exists
            DebugLogger.debugLog("MINIOTEST", "Checking if landingzone bucket exists");
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(MINIO_BUCKET).build());
            if (found) {
                DebugLogger.debugLog("MINIOTEST", "MinIO client built successfully! Bucket exists.");
            } else {
                DebugLogger.debugLog("MINIOTEST", "Bucket not found, but MinIO client is working.");
            }
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            DebugLogger.debugLog( "MINIOTEST", "Error building MinioClient or connecting to MinIO server.");
            DebugLogger.debugLog("MINIOTEST", e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendFileToMinio(File file, MinioUploadCallback callback){
        new Thread(() -> {
            try {
                int[] uploadStatus = {1, 0, 0};
                if(file.exists()){
                    Patient patient = Patient.getPatient();
                    String customMinioPath = APPNAME + "/" + patient.getClinicIdtoString()  + "/" + patient.getCaseId() + "/" + patient.getDate();
                    String objectName = customMinioPath + "/" + file.getName();
                    DebugLogger.debugLog("MINIOTEST", "Uploading: " + objectName);

                    boolean success = uploadFileToMinio(file, objectName);
                    if (success) {
                        uploadStatus[1]++;
                    } else {
                        uploadStatus[2]++;
                    }

                    if (callback != null) {
                        callback.onUploadComplete(uploadStatus);  // Return the upload status
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void sendFolderToMinio(File folder, Patient patient, MinioUploadCallback callback) {
        new Thread(() -> {
            try {
                int[] uploadStatus = {0, 0, 0}; // [totalFiles, successfulUploads, failedUploads]

                if (folder.exists() && folder.isDirectory()) {
                    String customMinioPath = APPNAME + "/" + patient.getClinicIdtoString()  + "/" + patient.getCaseId() + "/" + patient.getDate();

                    // Recursively upload all files and subfolders
                    uploadFolderToMinio(folder, folder.getAbsolutePath(), customMinioPath, uploadStatus);

                    // Generate success message based on the result
                    if (uploadStatus[0] == 0) {
                        DebugLogger.debugLog("MINIOTEST", "Nothing to upload");
                    } else if (uploadStatus[0] == uploadStatus[1]) {
                        DebugLogger.debugLog("MINIOTEST", "All files uploaded successfully.");
                    } else if (uploadStatus[1] > 0) {
                        DebugLogger.debugLog("MINIOTEST", uploadStatus[1] + " out of " + uploadStatus[0] + " files uploaded successfully.");
                     } else {
                        DebugLogger.debugLog("MINIOTEST", "None of the files could be uploaded.");
                    }
                } else {
                    DebugLogger.debugLog("MINIOTEST", "The specified folder does not exist or is not a directory.");
                }

                if (callback != null) {
                    callback.onUploadComplete(uploadStatus);  // Return the upload status
                }

            } catch (Exception e) {
                DebugLogger.debugLog("MINIOTESTT", "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void uploadFolderToMinio(File folder, String rootFolderPath, String customMinioPath, int[] uploadStatus) {
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // Increment total files count
                    uploadStatus[0]++;

                    // Compute the relative path of the file (to simulate folder structure)
                    String relativePath = file.getAbsolutePath().substring(rootFolderPath.length() + 1).replace("\\", "/");

                    // Prepend the custom folder path (iGait/01/{fid}/{time}) to the relative path
                    String objectName = customMinioPath + "/" + relativePath;

                    DebugLogger.debugLog("MINIOTEST", "Uploading: " + objectName);
                    boolean success = uploadFileToMinio(file, objectName);
                    if (success) {
                        uploadStatus[1]++; // Increment successful uploads count
                    } else {
                        uploadStatus[2]++; // Increment failed uploads count
                    }
                } else if (file.isDirectory()) {
                    // Recursively upload files from subdirectories
                    uploadFolderToMinio(file, rootFolderPath, customMinioPath, uploadStatus);
                }
            }
        }
    }

    private boolean uploadFileToMinio(File file, String objectName) {
        try {
            // Convert the file content to a byte array
            byte[] fileContent = Files.readAllBytes(file.toPath());

            // Upload the file to MinIO using the relative path as the object name
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(MINIO_BUCKET)
                            .object(objectName)  // Use the relative path (with folder) as the object name
                            .stream(new ByteArrayInputStream(fileContent), fileContent.length, -1)
                            .contentType("application/octet-stream") // Optionally, adjust content type based on file
                            .build()
            );

            DebugLogger.debugLog("MINIOTEST", "Uploaded: " + objectName);
            return true;
        } catch (Exception e) {
            DebugLogger.debugLog("MINIOTESTT", "Failed to upload file: " + objectName + " Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public interface MinioUploadCallback {
        void onUploadComplete(int[] uploadStatus); // Will return the [totalFiles, successfulUploads, failedUploads]
    }

}