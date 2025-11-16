package com.llui.iproms.Model;

public class FirstInfo {

    private String oldDate;
    private String fatigue;
    private String depression;
    private String bdi;
    private String promis;
    private String fsmc;
    private String sleep;

    public FirstInfo(String oldDate, String fatigue, String depression, String bdi, String promis, String fsmc, String sleep) {
        this.oldDate = oldDate;
        this.fatigue = fatigue;
        this.depression = depression;
        this.bdi = bdi;
        this.promis = promis;
        this.fsmc = fsmc;
        this.sleep = sleep;
    }

    public String getOldDate() {
        return oldDate;
    }

    public String getFatigue() {
        return fatigue;
    }

    public String getDepression() {
        return depression;
    }

    public String getBdi() {
        return bdi;
    }

    public String getPromis() {
        return promis;
    }

    public String getFsmc() {
        return fsmc;
    }

    public String getSleep() {
        return sleep;
    }

    public boolean everythingDone(){
        return fatigue.equals("done") && depression.equals("done") && bdi.equals("done") && promis.equals("done") && sleep.equals("done") && fsmc.equals("done");
    }
}
