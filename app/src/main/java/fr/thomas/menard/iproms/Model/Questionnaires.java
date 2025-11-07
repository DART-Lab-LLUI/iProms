package fr.thomas.menard.iproms.Model;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.thomas.menard.iproms.Enum.QuestionnaireStatus;
import fr.thomas.menard.iproms.Enum.QuestionnaireType;
import fr.thomas.menard.iproms.FileWriter.AbstractQuestionnaire;

public class Questionnaires {

    // --- Registry of all questionnaires ---
    private static final Map<QuestionnaireType, AbstractQuestionnaire> questionnaireMap = new HashMap<>();

    private Questionnaires() {}

    /**
     * Returns a questionnaire instance by type.
     * @param context Android context
     * @param type QuestionnaireType enum
     * @return AbstractQuestionnaire instance
     */
    public static AbstractQuestionnaire get(Context context, QuestionnaireType type) {
        return questionnaireMap.computeIfAbsent(type, key -> {
            AbstractQuestionnaire questionnaire = key.createInstance();
            questionnaire.readCSV(context);
            return questionnaire;
        });
    }

    /**
     * Returns all available questionnaires.
     */
    public static Map<QuestionnaireType, AbstractQuestionnaire> getAll() {
        return new HashMap<>(questionnaireMap);
    }

    /**
     * Returns a list of questionnaires that are already done (progressStatus == COMPLETED)
     */
    public static List<QuestionnaireType> getCompletedQuestionnaires() {
        List<QuestionnaireType> done = new ArrayList<>();
        for (Map.Entry<QuestionnaireType, AbstractQuestionnaire> entry : questionnaireMap.entrySet()) {
            if (entry.getValue().getProgressStatus() == QuestionnaireStatus.COMPLETED) {
                done.add(entry.getKey());
            }
        }
        return done;
    }

    /**
     * Optionally set a new questionnaire instance
     */
    public static void setNewQuestionnaire(QuestionnaireType type, AbstractQuestionnaire questionnaire) {
        questionnaireMap.put(type, questionnaire);
    }
}
