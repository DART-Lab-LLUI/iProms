package fr.thomas.menard.iproms.Views;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import fr.thomas.menard.iproms.App.MyApplication;
import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.LocaleHelper;
import fr.thomas.menard.iproms.databinding.ActivityIdentificationBinding;

public class IdentificationActivity extends BaseActivity {

    private ActivityIdentificationBinding binding;
    private String diagnosis;
    private Context context;

    private void initAttributes(){
        diagnosis  ="";
        Resources resources = getResources();
        binding.txtWelcome.setText(resources.getString(R.string.welcome));
        binding.txtSide.setText(resources.getString(R.string.diagnosis));
    }

    private void initClinicIdSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.clinic_names, R.layout.item_spinner
        );

        adapter.setDropDownViewResource(R.layout.item_spinner);
        binding.clinicIdSpinner.setAdapter(adapter);
        binding.clinicIdSpinner.setSelection(1);
    }

    private void listenBtnConfirm(){
        binding.btnConfirm.setOnClickListener(v -> {
            String patientID = binding.APatientTxtIdPatient.getText().toString().trim();
            String caseID = binding.APatientTxtIdCase.getText().toString().trim();

            if(patientID.length()!=7 || caseID.length()!=7){
                Toast.makeText(this, "Please, write a correct PID & FID", Toast.LENGTH_SHORT).show();
            } else if (diagnosis.isEmpty()) {
                Toast.makeText(this, "Please select a diagnosis", Toast.LENGTH_SHORT).show();
            }else{
                if(diagnosis.equals("Others")){
                    diagnosis = binding.editOtherDiagnosis.getText().toString();
                }

//                int clinicId = binding.clinicIdSpinner.getSelectedItemPosition();
                int clinicId = 3;
                patientInfo.setPatientData(patientID, caseID, diagnosis, clinicId);
                navigateToNextActivity(IntroductionActivity.class);
            }
        });
    }

    private void listenDiagnosis(){
        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rdBtnParki) {
                diagnosis = "Parkinson";
                binding.tilOther.setVisibility(View.GONE);
            } else if (checkedId == R.id.rdBtnStroke) {
                diagnosis = "Stroke";
                binding.tilOther.setVisibility(View.GONE);
            } else if (checkedId == R.id.rdBtnMS) {
                diagnosis = "MS";
                binding.tilOther.setVisibility(View.GONE);
            }else if (checkedId == R.id.rdBtnOther) {
                diagnosis = "Others";
                binding.tilOther.setVisibility(View.VISIBLE);
            } else {
                diagnosis = "";
            }
        });
    }

    @Override
    public void init() {
        initAttributes();
        initClinicIdSpinner();
    }

    @Override
    public void listenBtn() {
        listenBtnConfirm();
        listenDiagnosis();
    }

    @Override
    public void setBinding() {
        binding = ActivityIdentificationBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }
}