package fr.thomas.menard.iproms.Enum;

public enum Type {

    FIRST ("first"),
    SECOND ("second");

    private final String type;

    Type(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
