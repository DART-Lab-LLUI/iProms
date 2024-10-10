package fr.thomas.menard.iproms.Utils;

import android.app.Application;

public class tScore extends Application {
    private static double[][] scoreTable_ability_in_social_roles;
    private static double[][] scoreTable_cognitive_function;

    private static double[][] scoreTable_emotional_behavioral;
    private static double[][] scoreTable_sleep;

    private static double[][] scoreTable_satisfaction_social_roles;

    private static double[][] scoreTable_positve_affect;

    private static double[][] scoreTable_stigma;

    private static double[][] scoreTable_upper_function;

    private static double[][] scoreTable_lower_function;


    private static double[][] scoreTable_promis_physical;


    private static double[][] scoreTable_promis_mental;




    public static double[][] getScoreTable_ability_in_social_roles() {
        return scoreTable_ability_in_social_roles;
    }
    public static double[][] getScoreTable_cognitive_function() {
        return scoreTable_cognitive_function;
    }

    public static double[][] getScoreTable_emotional_behavioral() {
        return scoreTable_emotional_behavioral;
    }

    public static double[][] getScoreTable_sleep() {
        return scoreTable_sleep;
    }

    public static double[][] getScoreTable_satisfaction_social_roles() {
        return scoreTable_satisfaction_social_roles;
    }

    public static double[][] getScoreTable_positve_affect() {
        return scoreTable_positve_affect;
    }

    public static double[][] getScoreTable_stigma() {
        return scoreTable_stigma;
    }

    public static double[][] getScoreTable_upper_function() {
        return scoreTable_upper_function;
    }

    public static double[][] getScoreTable_lower_function() {
        return scoreTable_lower_function;
    }

    public static double[][] getScoreTable_promis_physical() {
        return scoreTable_promis_physical;
    }
    public static double[][] getScoreTable_promis_mental() {
        return scoreTable_promis_mental;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the scoreTable here
        scoreTable_emotional_behavioral = initializeScoreTable_emotional_behavioral();

        scoreTable_ability_in_social_roles = initializeScoreTable_ability_in_social_roles();
        scoreTable_cognitive_function = initializeScoreTable_cognitive_function();
        scoreTable_sleep = initializeScoreTable_sleep();
        scoreTable_satisfaction_social_roles = initializeScoreTable_satisfaction_social_roles();
        scoreTable_positve_affect = initializeScoreTable_positve_affect();
        scoreTable_stigma = initializeScoreTable_stigma();
        scoreTable_upper_function = initializeScoreTable_upper_function();
        scoreTable_lower_function = initializeScoreTable_lower_function();
        scoreTable_promis_physical = initializeScoreTable_promis_physical();
        scoreTable_promis_mental = initializeScoreTable_promis_mental();
    }


    private double[][] initializeScoreTable_promis_physical() {
        return new double[][]{
                {4, 16.2, 4.8},
                {5, 19.9, 4.8},
                {6, 23.5, 4.8},
                {7, 26.7, 4.8},
                {8, 29.6, 4.8},
                {9, 32.4, 4.8},
                {10, 34.9, 4.8},
                {11, 37.4, 4.8},
                {12, 39.8, 4.8},
                {13, 42.3, 4.8},
                {14, 44.9, 4.8},
                {15, 47.7, 4.8},
                {16, 50.8, 4.8},
                {17, 54.1, 4.8},
                {18, 57.7, 4.8},
                {19, 61.9, 4.8},
                {20, 67.7, 4.8},

        };
    }

    private double[][] initializeScoreTable_promis_mental() {
        return new double[][]{
                {4, 21.2, 4.8},
                {5, 25.1, 4.8},
                {6, 28.4, 4.8},
                {7, 31.3, 4.8},
                {8, 33.8, 4.8},
                {9, 36.3, 4.8},
                {10, 38.8, 4.8},
                {11, 41.1, 4.8},
                {12, 43.5, 4.8},
                {13, 45.8, 4.8},
                {14, 48.3, 4.8},
                {15, 50.8, 4.8},
                {16, 53.3, 4.8},
                {17, 56.0, 4.8},
                {18, 59.0, 4.8},
                {19, 62.5, 4.8},
                {20, 67.6, 4.8},


        };
    }


    private double[][] initializeScoreTable_lower_function() {
        return new double[][]{
                {8, 16.5, 3.0},
                {9, 19.2, 2.8},
                {10, 21.1, 2.6},
                {11, 22.6, 2.4},
                {12, 23.9, 2.3},
                {13, 25.1, 2.3},
                {14, 26.2, 2.2},
                {15, 27.2, 2.2},
                {16, 28.1, 2.1},
                {17, 29.0, 2.1},
                {18, 29.9, 2.1},
                {19, 30.7, 2.1},
                {20, 31.5, 2.1},
                {21, 32.2, 2.1},
                {22, 33.0, 2.1},
                {23, 33.7, 2.0},
                {24, 34.5, 2.1},
                {25, 35.2, 2.1},
                {26, 36.0, 2.1},
                {27, 36.7, 2.1},
                {28, 37.5, 2.1},
                {29, 38.3, 2.1},
                {30, 39.1, 2.2},
                {31, 39.9, 2.2},
                {32, 40.8, 2.3},
                {33, 41.7, 2.4},
                {34, 42.8, 2.5},
                {35, 43.9, 2.6},
                {36, 45.2, 2.9},
                {37, 46.7, 3.1},
                {38, 48.6, 3.3},
                {39, 51.2, 3.8},
                {40, 58.6, 6.4}
        };
    }

    private double[][] initializeScoreTable_upper_function() {
        return new double[][]{
                {8, 12.8, 2.0},
                {9, 13.7, 2.3},
                {10, 14.7, 2.4},
                {11, 15.8, 2.5},
                {12, 16.9, 2.4},
                {13, 18.0, 2.4},
                {14, 19.0, 2.3},
                {15, 19.9, 2.2},
                {16, 20.8, 2.1},
                {17, 21.6, 2.1},
                {18, 22.4, 2.1},
                {19, 23.1, 2.0},
                {20, 23.9, 2.0},
                {21, 24.6, 2.0},
                {22, 25.3, 2.0},
                {23, 26.0, 2.0},
                {24, 26.7, 2.0},
                {25, 27.3, 2.0},
                {26, 28.0, 2.0},
                {27, 28.7, 2.0},
                {28, 29.5, 2.0},
                {29, 30.2, 2.1},
                {30, 30.9, 2.1},
                {31, 31.7, 2.1},
                {32, 32.6, 2.2},
                {33, 33.5, 2.3},
                {34, 34.5, 2.4},
                {35, 35.6, 2.7},
                {36, 37.1, 3.2},
                {37, 39.3, 4.2},
                {38, 41.2, 4.5},
                {39, 43.7, 4.7},
                {40, 53.8, 7.8}
        };
    }

    private double[][] initializeScoreTable_stigma() {

        return new double[][]{
                {8, 39.2, 5.8},
                {9, 45.7, 3.3},
                {10, 47.6, 3.0},
                {11, 49.3, 2.6},
                {12, 50.6, 2.4},
                {13, 51.7, 2.2},
                {14, 52.8, 2.1},
                {15, 53.7, 2.0},
                {16, 54.6, 2.0},
                {17, 55.4, 2.0},
                {18, 56.2, 1.9},
                {19, 57.0, 1.9},
                {20, 57.8, 1.9},
                {21, 58.5, 1.9},
                {22, 59.3, 1.9},
                {23, 60.1, 1.9},
                {24, 60.8, 1.9},
                {25, 61.6, 1.9},
                {26, 62.4, 1.9},
                {27, 63.2, 1.9},
                {28, 64.0, 1.9},
                {29, 64.8, 1.9},
                {30, 65.7, 2.0},
                {31, 66.6, 2.0},
                {32, 67.5, 2.0},
                {33, 68.5, 2.1},
                {34, 69.6, 2.1},
                {35, 70.8, 2.2},
                {36, 72.2, 2.3},
                {37, 73.7, 2.4},
                {38, 75.6, 2.6},
                {39, 78.1, 3.0},
                {40, 81.5, 3.5}
        };
    }

    private double[][] initializeScoreTable_positve_affect() {
        return new double[][]{
                {9, 26.3, 3.9},
                {10, 30.2, 2.3},
                {11, 32.0, 1.8},
                {12, 33.3, 1.6},
                {13, 34.3, 1.5},
                {14, 35.3, 1.4},
                {15, 36.1, 1.4},
                {16, 37.0, 1.4},
                {17, 37.8, 1.4},
                {18, 38.6, 1.4},
                {19, 39.4, 1.4},
                {20, 40.2, 1.4},
                {21, 41.0, 1.4},
                {22, 41.8, 1.4},
                {23, 42.7, 1.4},
                {24, 43.5, 1.4},
                {25, 44.4, 1.5},
                {26, 45.3, 1.5},
                {27, 46.3, 1.5},
                {28, 47.2, 1.5},
                {29, 48.1, 1.5},
                {30, 49.0, 1.4},
                {31, 49.9, 1.4},
                {32, 50.7, 1.4},
                {33, 51.5, 1.4},
                {34, 52.3, 1.4},
                {35, 53.2, 1.4},
                {36, 54.0, 1.4},
                {37, 54.9, 1.4},
                {38, 55.8, 1.5},
                {39, 56.8, 1.5},
                {40, 57.8, 1.5},
                {41, 58.8, 1.5},
                {42, 59.9, 1.6},
                {43, 61.3, 1.9},
                {44, 63.3, 2.4},
                {45, 68.0, 4.5}
        };
    }

    private double[][] initializeScoreTable_satisfaction_social_roles() {
        return new double[][]{
                {8, 28.4, 4.1},
                {9, 32.6, 2.3},
                {10, 34.0, 2.0},
                {11, 35.1, 1.8},
                {12, 35.9, 1.7},
                {13, 36.7, 1.5},
                {14, 37.4, 1.5},
                {15, 38.0, 1.4},
                {16, 38.6, 1.4},
                {17, 39.1, 1.4},
                {18, 39.7, 1.3},
                {19, 40.2, 1.3},
                {20, 40.7, 1.3},
                {21, 41.2, 1.3},
                {22, 41.7, 1.3},
                {23, 42.2, 1.3},
                {24, 42.7, 1.3},
                {25, 43.2, 1.3},
                {26, 43.7, 1.3},
                {27, 44.2, 1.3},
                {28, 44.7, 1.3},
                {29, 45.2, 1.3},
                {30, 45.8, 1.4},
                {31, 46.3, 1.4},
                {32, 46.9, 1.4},
                {33, 47.5, 1.4},
                {34, 48.2, 1.5},
                {35, 48.9, 1.5},
                {36, 49.8, 1.6},
                {37, 50.7, 1.8},
                {38, 52.0, 2.1},
                {39, 53.7, 2.5},
                {40, 60.5, 5.7}
        };
    }

    private double[][] initializeScoreTable_sleep() {
        return new double[][]{
                {8, 32.0, 5.9},
                {9, 36.3, 5.0},
                {10, 39.1, 4.7},
                {11, 41.7, 4.4},
                {12, 43.8, 4.2},
                {13, 45.6, 4.0},
                {14, 47.3, 3.9},
                {15, 48.9, 3.8},
                {16, 50.4, 3.7},
                {17, 51.8, 3.6},
                {18, 53.1, 3.6},
                {19, 54.4, 3.5},
                {20, 55.6, 3.5},
                {21, 56.8, 3.5},
                {22, 58.0, 3.4},
                {23, 59.2, 3.4},
                {24, 60.4, 3.4},
                {25, 61.6, 3.4},
                {26, 62.8, 3.4},
                {27, 63.9, 3.4},
                {28, 65.1, 3.4},
                {29, 66.4, 3.4},
                {30, 67.6, 3.5},
                {31, 68.9, 3.5},
                {32, 70.3, 3.5},
                {33, 71.7, 3.6},
                {34, 73.2, 3.6},
                {35, 74.7, 3.7},
                {36, 76.4, 3.8},
                {37, 78.2, 3.9},
                {38, 80.2, 3.9},
                {39, 82.2, 3.8},
                {40, 84.2, 3.5}
        };
    }

    private double[][] initializeScoreTable_emotional_behavioral(){
        return new double[][]{
                {8, 32.2, 4.9},
                {9, 37.2, 3.5},
                {10, 39.9, 3.0},
                {11, 42.0, 2.7},
                {12, 43.7, 2.6},
                {13, 45.3, 2.5},
                {14, 46.7, 2.4},
                {15, 48.1, 2.4},
                {16, 49.4, 2.4},
                {17, 50.7, 2.4},
                {18, 52.0, 2.4},
                {19, 53.2, 2.4},
                {20, 54.5, 2.4},
                {21, 55.8, 2.4},
                {22, 57.0, 2.4},
                {23, 58.3, 2.4},
                {24, 59.6, 2.4},
                {25, 60.8, 2.4},
                {26, 62.1, 2.4},
                {27, 63.3, 2.4},
                {28, 64.5, 2.4},
                {29, 65.8, 2.4},
                {30, 66.9, 2.3},
                {31, 68.1, 2.3},
                {32, 69.3, 2.3},
                {33, 70.6, 2.4},
                {34, 71.8, 2.4},
                {35, 73.1, 2.4},
                {36, 74.5, 2.5},
                {37, 76.0, 2.7},
                {38, 77.7, 2.8},
                {39, 79.8, 3.1},
                {40, 82.6, 3.3}
        };
    }

    private double[][] initializeScoreTable_ability_in_social_roles() {
        return new double[][]{
                {8, 24.1, 3.8},
                {9, 27.7, 2.5},
                {10, 29.5, 2.1},
                {11, 30.8, 1.8},
                {12, 31.8, 1.6},
                {13, 32.7, 1.5},
                {14, 33.5, 1.5},
                {15, 34.3, 1.4},
                {16, 35, 1.4},
                {17, 35.7, 1.4},
                {18, 36.4, 1.4},
                {19, 37.1, 1.4},
                {20, 37.8, 1.4},
                {21, 38.5, 1.4},
                {22, 39.2, 1.4},
                {23, 39.9, 1.4},
                {24, 40.6, 1.4},
                {25, 41.3, 1.4},
                {26, 42, 1.4},
                {27, 42.7, 1.4},
                {28, 43.4, 1.4},
                {29, 44, 1.4},
                {30, 44.7, 1.4},
                {31, 45.4, 1.4},
                {32, 46.1, 1.4},
                {33, 46.8, 1.4},
                {34, 47.5, 1.5},
                {35, 48.3, 1.5},
                {36, 49.2, 1.6},
                {37, 50.2, 1.8},
                {38, 51.6, 2.2},
                {39, 53.4, 2.6},
                {40, 60.2, 5.8}
        };
    }


    private double[][] initializeScoreTable_cognitive_function(){
        return new double[][]{
                {8, 17.3, 4.3},
                {9, 20.4, 3.8},
                {10, 22.6, 3.5},
                {11, 24.4, 3.3},
                {12, 25.9, 3.1},
                {13, 27.3, 3.0},
                {14, 28.6, 2.9},
                {15, 29.8, 2.8},
                {16, 30.9, 2.7},
                {17, 32.0, 2.7},
                {18, 33.0, 2.6},
                {19, 34.0, 2.6},
                {20, 35.0, 2.6},
                {21, 36.0, 2.6},
                {22, 37.0, 2.6},
                {23, 37.9, 2.6},
                {24, 38.9, 2.6},
                {25, 39.9, 2.6},
                {26, 40.9, 2.6},
                {27, 41.9, 2.6},
                {28, 42.9, 2.6},
                {29, 43.9, 2.7},
                {30, 44.9, 2.7},
                {31, 46.0, 2.7},
                {32, 47.1, 2.7},
                {33, 48.3, 2.8},
                {34, 49.6, 2.8},
                {35, 50.9, 2.9},
                {36, 52.4, 3.1},
                {37, 54.2, 3.3},
                {38, 56.3, 3.7},
                {39, 59.0, 4.2},
                {40, 64.2, 5.7}
        };
    }

}
