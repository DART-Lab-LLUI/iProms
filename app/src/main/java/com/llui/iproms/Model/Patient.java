package com.llui.iproms.Model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.llui.iproms.App.MyApplication;
import com.llui.iproms.Utils.Utils;

/**
 * Patient class represents a model for storing patient information and measurement sessions.
 * It provides methods to set patient data, manage measurement sessions, and retrieve patient details.
 */
public class Patient {
    private static final String PREFS_NAME = "PatientPrefs";
    private static final String KEY_PATIENT_ID = "patientId";
    private static final String KEY_CASE_ID = "caseId";
    private static final String KEY_DIAGNOSIS = "diagnosis";
    private static final String KEY_CLINIC_ID = "clinicId";
    private static final String KEY_DATE = "date";

    private static Patient patient;
    private String patientId, caseId, diagnosis;
    private String date;
    private int clinicId;

    private Patient() {}

    public static Patient getPatient() {
        if (patient == null) {
            patient = new Patient();
        }
        return patient;
    }

    public void setPatientData(String patientId, String caseId, String diagnosis, int clinicId) {
        if (patient == null) {
            patient = new Patient();
        }

        this.patientId = patientId;
        this.caseId = caseId;
        this.diagnosis = diagnosis;
        this.clinicId = clinicId;
        this.date = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        saveToPreferences();
    }

    // Save patient data to SharedPreferences
    private void saveToPreferences() {
        SharedPreferences prefs = MyApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(KEY_PATIENT_ID, patientId);
        editor.putString(KEY_CASE_ID, caseId);
        editor.putString(KEY_DIAGNOSIS, diagnosis);
        editor.putInt(KEY_CLINIC_ID, clinicId);
        editor.putString(KEY_DATE, date);

        editor.apply(); // Apply changes asynchronously
    }

    // Load patient data from SharedPreferences if needed
    private void loadFromPreferences() {
        SharedPreferences prefs = MyApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (patientId == null) patientId = prefs.getString(KEY_PATIENT_ID, null);
        if (caseId == null) caseId = prefs.getString(KEY_CASE_ID, null);
        if (diagnosis == null) diagnosis = prefs.getString(KEY_DIAGNOSIS, null);
        if (date == null) date = prefs.getString(KEY_DATE, null);
        if (clinicId == 0) clinicId = prefs.getInt(KEY_CLINIC_ID, -1);
    }

    public String getPatientId() {
        if (patientId == null) loadFromPreferences();
        return patientId;
    }

    public String getCaseId() {
        if (caseId == null) loadFromPreferences();
        return caseId;
    }

    public String getDiagnosis() {
        if (diagnosis == null) loadFromPreferences();
        return diagnosis;
    }

    public int getClinicId() {
        if (clinicId == 0) loadFromPreferences();
        return clinicId;
    }

    public String getClinicIdtoString() {
        if (clinicId == 0) loadFromPreferences();

        if (clinicId >= 0 && clinicId <= 99) {
            return (clinicId == 1) ? String.format("%02d", clinicId) : String.format("%02d", clinicId + 1);
        } else {
            throw new IllegalArgumentException("Invalid clinicId: " + clinicId);
        }
    }

    public String getDate() {
        if (date == null) loadFromPreferences();
        return date;
    }

    public String getPointDate() {
        return getDate().replace('_', '.');
    }

    public String getFormattedDate() {
        return Utils.changeDateFormatFromYMDToDMY(getDate());
    }

    @NonNull
    @Override
    public String toString() {
        return "PID: " + getPatientId() + "; CID: " + getCaseId() + " " + getFormattedDate();
    }
}
