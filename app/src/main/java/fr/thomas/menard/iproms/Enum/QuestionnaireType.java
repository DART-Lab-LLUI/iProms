package fr.thomas.menard.iproms.Enum;

import fr.thomas.menard.iproms.FileWriter.AbstractQuestionnaire;
import fr.thomas.menard.iproms.FileWriter.BDIQuestionnaire;
import fr.thomas.menard.iproms.FileWriter.DepressionAnxietyQuestionnaire;
import fr.thomas.menard.iproms.FileWriter.FSMCQuestionnaire;
import fr.thomas.menard.iproms.FileWriter.FatigueQuestionnaire;
import fr.thomas.menard.iproms.FileWriter.PromisQuestionnaire;
import fr.thomas.menard.iproms.FileWriter.SleepQuestionnaire;

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