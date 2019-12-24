package com.example.recordratings.misc;

public class Censor {

    public Censor(){

    }

    public String censorText(String text){
        String result = text;
        String finalResult = result.toLowerCase();
        if(finalResult.contains("fuck") ||
           finalResult.contains(" ass ") ||
           finalResult.contains("ass ") ||
           finalResult.contains("asshole") ||
           finalResult.contains("bitch") ||
           finalResult.contains("fag") ||
           finalResult.contains("faggot") ||
           finalResult.contains("faggit") ||
           finalResult.contains("fagget") ||
           finalResult.contains("shit") ||
           finalResult.contains("nigger") ||
           finalResult.contains(" dyke ") ||
           finalResult.contains("dyke ") ||
           finalResult.contains(" dike ") ||
           finalResult.contains("dike") ||
           finalResult.contains("chode") ||
           finalResult.contains("choad") ||
           finalResult.contains("cunt") ||
           finalResult.contains("kunt") ||
           finalResult.contains("dick") ||
           finalResult.contains("tits") ||
           finalResult.contains("cum") ||
           finalResult.contains("coon") ||
           finalResult.contains("douche") ||
           finalResult.contains("jizz") ||
           finalResult.contains(" poon ") ||
           finalResult.contains("poon ") ||
           finalResult.contains("pussy") ||
           finalResult.contains("retard") ||
           finalResult.contains("twat") ||
           finalResult.contains(" hoe ") ||
           finalResult.contains("hoe ") ||
           finalResult.contains("whore")){
            finalResult = text.toLowerCase().replace("fuck", "****")
                                            .replace("ass", "***")
                                            .replace(" ass ", " *** ")
                                            .replace("ass ", "*** ")
                                            .replace("asshole", "***hole")
                                            .replace("bitch", "*****")
                                            .replace("fag", "***")
                                            .replace("faggot", "******")
                                            .replace("faggit", "******")
                                            .replace("fagget", "******")
                                            .replace("shit", "****")
                                            .replace("nigger", "******")
                                            .replace("dyke", "****")
                                            .replace("dike", "****")
                                            .replace("chode", "*****")
                                            .replace("choad", "*****")
                                            .replace("cunt", "****")
                                            .replace("kunt", "****")
                                            .replace("dick", "****")
                                            .replace("cock", "****")
                                            .replace("tits", "****")
                                            .replace("cum", "***")
                                            .replace("coon", "****")
                                            .replace("douche", "******")
                                            .replace("jizz", "****")
                                            .replace("pussy", "*****")
                                            .replace("retard", "******")
                                            .replace("twat", "****")
                                            .replace("hoe", "***")
                                            .replace("whore", "*****");
        }
        return finalResult;
    }
}
