package com.llui.iproms.Enum;

import com.llui.iproms.FileWriter.AbstractQuestionnaire;
import com.llui.iproms.FileWriter.BDIQuestionnaire;
import com.llui.iproms.FileWriter.DepressionAnxietyQuestionnaire;
import com.llui.iproms.FileWriter.FSMCQuestionnaire;
import com.llui.iproms.FileWriter.FatigueQuestionnaire;
import com.llui.iproms.FileWriter.PromisQuestionnaire;
import com.llui.iproms.FileWriter.SleepQuestionnaire;

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