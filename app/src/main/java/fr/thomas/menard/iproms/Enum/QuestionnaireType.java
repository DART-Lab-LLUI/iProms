package fr.thomas.menard.iproms.Enum;

import fr.thomas.menard.iproms.FileWriter.AbstractQuestionnaire;
import fr.thomas.menard.iproms.FileWriter.FatigueQuestionnaire;

public enum QuestionnaireType {
    FATIGUE(FatigueQuestionnaire.class);
//    SLEEP(SleepQuestionnaire.class),
//    PAIN(PainQuestionnaire.class);

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