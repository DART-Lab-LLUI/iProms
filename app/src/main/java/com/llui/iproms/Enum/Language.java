package com.llui.iproms.Enum;

public enum Language {
    GERMAN ("de"),
    ENGLISH ("en");

    private final String language;

    Language(String language){
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public static Language fromCode(String code) {
        for (Language lang : values()) {
            if (lang.language.equals(code)) return lang;
        }
        return ENGLISH;
    }
}
