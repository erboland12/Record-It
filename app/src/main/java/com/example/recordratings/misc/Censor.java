package com.example.recordratings.misc;

public class Censor {

    public Censor(){

    }

    public String censorText(String text){
        String result = text;
        String finalResult = result;
        if(finalResult.toLowerCase().contains("fuck") ||
           finalResult.toLowerCase().contains(" ass ") ||
           finalResult.toLowerCase().contains("ass ") ||
           finalResult.toLowerCase().contains("asshole") ||
           finalResult.toLowerCase().contains("bitch") ||
           finalResult.toLowerCase().contains("fag") ||
           finalResult.toLowerCase().contains("faggot") ||
           finalResult.toLowerCase().contains("faggit") ||
           finalResult.toLowerCase().contains("fagget") ||
           finalResult.toLowerCase().contains("shit") ||
           finalResult.toLowerCase().contains("nigger") ||
           finalResult.toLowerCase().contains("dyke") ||
           finalResult.toLowerCase().contains("dike") ||
           finalResult.toLowerCase().contains("chode") ||
           finalResult.toLowerCase().contains("choad") ||
           finalResult.toLowerCase().contains("cunt") ||
           finalResult.toLowerCase().contains("kunt") ||
           finalResult.toLowerCase().contains("dick") ||
           finalResult.toLowerCase().contains("tits") ||
           finalResult.toLowerCase().contains(" cum ") ||
           finalResult.toLowerCase().contains("cum ") ||
           finalResult.toLowerCase().contains(" coon ") ||
           finalResult.toLowerCase().contains("coon ") ||
           finalResult.toLowerCase().contains("douche") ||
           finalResult.toLowerCase().contains("jizz") ||
           finalResult.toLowerCase().contains(" poon ") ||
           finalResult.toLowerCase().contains("poon ") ||
           finalResult.toLowerCase().contains("pussy") ||
           finalResult.toLowerCase().contains("retard") ||
           finalResult.toLowerCase().contains("twat") ||
           finalResult.toLowerCase().contains(" hoe ") ||
           finalResult.toLowerCase().contains("hoe ") ||
           finalResult.toLowerCase().contains("whore") ||
           finalResult.toLowerCase().equals("ass") ||
           finalResult.toLowerCase().equals("cum") ||
           finalResult.toLowerCase().equals("coon") ||
           finalResult.toLowerCase().equals("poon") ||
           finalResult.toLowerCase().equals("hoe")){
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
