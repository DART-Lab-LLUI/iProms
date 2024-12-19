package fr.thomas.menard.iproms.Views;

import android.view.LayoutInflater;
import fr.thomas.menard.iproms.databinding.ActivityIntroductionFsmcactivityBinding;

public class IntroductionFSMCActivity extends BaseActivity {

    private ActivityIntroductionFsmcactivityBinding binding;

    @Override
    public void init(){
    }

    @Override
    public void listenBtn(){
        binding.btnIntroFSMC.setOnClickListener(v -> {
            navigateToNextActivity(FSMCActivity.class);
        });
    }

    @Override
    public void setBinding() {
        binding = ActivityIntroductionFsmcactivityBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }
}