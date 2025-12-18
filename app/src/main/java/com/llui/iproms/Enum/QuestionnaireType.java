package com.llui.iproms.Enum;

import com.llui.iproms.Questionnaires.AbstractQuestionnaire;
import com.llui.iproms.Questionnaires.BDIQuestionnaire;
import com.llui.iproms.Questionnaires.DepressionAnxietyQuestionnaire;
import com.llui.iproms.Questionnaires.FSMCQuestionnaire;
import com.llui.iproms.Questionnaires.FatigueQuestionnaire;
import com.llui.iproms.Questionnaires.PromisQuestionnaire;
import com.llui.iproms.Questionnaires.SleepQuestionnaire;

public enum QuestionnaireType {
    FATIGUE(FatigueQuestionnaire.class),
    DEPRESSIONANXIETY(DepressionAnxietyQuestionnaire.class),
    PROMIS(PromisQuestionnaire.class),
    BDI(BDIQuestionnaire.class),
    SLEEP(SleepQuestionnaire.class),
    FSMC(FSMCQuestionnaire.class);


    private final Class<? extends AbstractQuestionnaire> clazz;

    QuestionnaireType(Class<? extends AbstractQuestionnaire> clazz) {
        this.clazz = clazz;
    }

    public AbstractQuestionnaire createInstance() {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate questionnaire: " + this, e);
        }
    }
}