package fr.thomas.menard.iproms.Models;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import fr.thomas.menard.iproms.Utils.Utils;

/**
 * Patient class represents a model for storing patient information and measurement sessions.
 * It provides methods to set patient data, manage measurement sessions, and retrieve patient details.
 */
public class Patient {

    private static Patient patient;
    private String patientId, caseId, diagnosis;
    private String date, stopTime;
    private int clinicId;

    private Patient() {
    }

    public static Patient getPatient() {
        if (patient == null) {
            patient = new Patient();
        }

        return patient;
    }

    public void setPatientData(String patientId, String caseId, String diagnosis, int clinicId, Context context){
        if (patient == null) {
            patient = new Patient();
        }

        this.diagnosis = diagnosis;
        this.clinicId = clinicId;
        this.patientId = patientId;
        this.caseId = caseId;
        this.date = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        this.stopTime = null;
    }

    public String getPatientId(){
        return this.patientId;
    }

    public String getClinicIdtoString() {
        if (clinicId >= 0 && clinicId <= 99) {
            return String.format("%02d", clinicId + 1);
        } else {
            throw new IllegalArgumentException("Invalid clinicId: " + clinicId);
        }
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public int getClinicId() {
        return this.clinicId;
    }

    public String getCaseId(){
        return this.caseId;
    }
    public String getDate() { return this.date; }
    public String getPointDate() { return this.date.replace('_', '.');}

    public String getStopTime() {return  this.stopTime;}
    public void setStopTime() {
        this.stopTime = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    }

    public String getFormattedDate(){
        return Utils.changeDateFormatFromYMDToDMY(this.date);
    }

    @NonNull
    @Override
    public String toString() {
        return "PID: " + patientId + "; CID: " + caseId  + " " + getFormattedDate();
    }


}
