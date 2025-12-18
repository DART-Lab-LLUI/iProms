package com.llui.iproms.Utils;

import android.util.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler{
    private static final String TAG = "CrashLogger";
    private final Thread.UncaughtExceptionHandler defaultHandler;
    private final String crashLogFilePath;

    public CustomExceptionHandler(String crashLogFilePath) {
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.crashLogFilePath = crashLogFilePath;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        logCrashToFile(throwable);

        // Pass the exception to the default handler (or restart the app here)
        if (defaultHandler != null) {
            defaultHandler.uncaughtException(thread, throwable);
        } else {
            System.exit(2); // Kill the app process
        }
    }

    private void logCrashToFile(Throwable throwable) {
        try (FileWriter writer = new FileWriter(crashLogFilePath, true);
             PrintWriter printWriter = new PrintWriter(writer)) {
            printWriter.println("=== Crash Log Start ===");
            printWriter.println("Thread: " + Thread.currentThread().getName());
            throwable.printStackTrace(printWriter);
            printWriter.println("=== Crash Log End ===");
            Log.e(TAG, "Crash logged to file: " + crashLogFilePath);
        } catch (IOException e) {
            Log.e(TAG, "Error writing crash log to file", e);
        }
    }
}
