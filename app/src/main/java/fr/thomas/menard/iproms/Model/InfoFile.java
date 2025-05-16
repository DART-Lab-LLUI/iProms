package fr.thomas.menard.iproms.Model;

public class InfoFile {

    public static String patientId;
    public static String caseId;
    public static String oldDate;

    public static String sleep, fsmc;
    public static String questionAnsFatigue, questionAnsDep, questionAnsQol, questionAnsBDI, questionAnsPROMIS;
    public static String avg_score_fatigue, avg_score_depression, avg_score_anxiety, avg_score_qol, avg_score_PROMIS_physical, avg_score_PROMIS_mental;

    public static String lastQuestionFatigue, lastQuestionDep, skipped_question_anx, skippedQuestionQOL, skipped_question_bdi, skipped_question_promis;

    public static String fatigue, depression, qol, bdi, promis;
    public static String[] fatigueQuestionScores = new String[9];
    public static String[] depressionQuestionScores = new String[14];
    public static String[] bdiQuestionScores = new String[21];
    public static String[] promisQuestionScores = new String[10];

    public static String questionAnsQOL1, questionAnsQOL2,questionAnsQOL3,questionAnsQOL4,questionAnsQOL5,questionAnsQOL6,questionAnsQOL7,questionAnsQOL8,questionAnsQOL9,questionAnsQOL10;
    public static String scoreQOL1, scoreQOL2,scoreQOL3,scoreQOL4,scoreQOL5,scoreQOL6,scoreQOL7,scoreQOL8,scoreQOL9,scoreQOL10, score_bdi;
    public static String qol1, qol2, qol3, qol4, qol5, qol6, qol7, qol8, qol9, qol10;
    public static String skippedQuestionQOL1, skippedQuestionQOL2, skippedQuestionQOL3,skippedQuestionQOL4,skippedQuestionQOL5,skippedQuestionQOL6,skippedQuestionQOL7,skippedQuestionQOL8,skippedQuestionQOL9,skippedQuestionQOL10;
    public static String[] qolQuestionScores = new String[10];
    public static String[] qol1QuestionScores = new String[8];
    public static String[] qol2QuestionScores = new String[8];
    public static String[] qol3QuestionScores = new String[8];
    public static String[] qol4QuestionScores = new String[8];
    public static String[] qol5QuestionScores = new String[8];
    public static String[] qol6QuestionScores = new String[8];
    public static String[] qol7QuestionScores = new String[9];
    public static String[] qol8QuestionScores = new String[8];
    public static String[] qol9QuestionScores = new String[8];
    public static String[] qol10QuestionScores = new String[8];

    public static String scoreFSMC, questionAnsFCSM, skipped_question_fsmc;
    public static String[] fsmcQuestionScores = new String[20];

    public static String score_sleep, questionAnsSleep, skipped_question_sleep;
    public static String[] sleepQuestionScores = new String[8];

//    public static boolean redo_questionnaire;
//    public static boolean restart_fatigue =false, restart_dep = false, restart_promis, restart_qol1 = false, restart_qol2= false, restart_qol3= false, restart_qol4= false,
//            restart_qol5= false, restart_qol6= false, restart_qol7= false, restart_qol8= false, restart_qol9= false,restart_qol10= false;

    public static boolean everythingDone(){
        // compare literals to fields - calling "done".equals is null safe
        return "done".equals(fatigue) &&
                "done".equals(depression) &&
                "done".equals(bdi) &&
                "done".equals(promis) &&
                "done".equals(sleep) &&
                "done".equals(fsmc) &&
                "done".equals(qol);
    }
}
