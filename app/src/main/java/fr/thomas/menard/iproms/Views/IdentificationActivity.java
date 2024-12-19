package fr.thomas.menard.iproms.Views;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import fr.thomas.menard.iproms.Model.MyApplication;
import fr.thomas.menard.iproms.R;
import fr.thomas.menard.iproms.Utils.LocaleHelper;
import fr.thomas.menard.iproms.databinding.ActivityIdentificationBinding;

public class IdentificationActivity extends BaseActivity {

    private ActivityIdentificationBinding binding;
    private String diagnosis;
    private Context context;

    private void initAttributes(){
        diagnosis  ="";
        context = LocaleHelper.setLocale(IdentificationActivity.this, MyApplication.language.getLanguage());
        Resources resources = context.getResources();
        binding.txtWelcome.setText(resources.getString(R.string.welcome));
        binding.txtSide.setText(resources.getString(R.string.diagnosis));
    }

    private void listenBtnConfirm(){
        binding.btnConfirm.setOnClickListener(v -> {
            String patientID = binding.APatientTxtIdPatient.getText().toString().trim();
            String caseID = binding.APatientTxtIdCase.getText().toString().trim();

            if(patientID.length()!=7 || caseID.length()!=7){
                Toast.makeText(context, "Please, write a correct PID & FID", Toast.LENGTH_SHORT).show();
            } else if (diagnosis.isEmpty()) {
                Toast.makeText(context, "Please select a diagnosis", Toast.LENGTH_SHORT).show();
            }else{
                if(diagnosis.equals("Others")){
                    diagnosis = binding.editOtherDiagnosis.getText().toString();
                }

                patientInfo.setPatientData(patientID, caseID, diagnosis,1, context);
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