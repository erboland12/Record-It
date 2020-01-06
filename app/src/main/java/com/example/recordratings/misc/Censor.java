package com.example.recordratings.misc;

import java.util.Map;
import java.util.regex.Pattern;

//Class designed for censoring strings that include expletives.
public class Censor {

    public Censor(){
    }

    public String censorText(String text){
        String finalResult = text;
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
            finalResult = text
                    .replaceAll("\\W*((?i)fuck(?-i))\\W*", " **** ")
                    .replaceAll("\\W*((?i)fuckin(?-i))\\W*", " ****** ")
                    .replaceAll("\\W*((?i)fucking(?-i))\\W*", " ******* ")
                    .replaceAll("\\W*((?i)ass(?-i))\\W*", " *** ")
                    .replaceAll("\\W*((?i)asshole(?-i))\\W*", " ******* ")
                    .replaceAll("\\W*((?i)bitch(?-i))\\W*", " ***** ")
                    .replaceAll("\\W*((?i)fag(?-i))\\W*", " *** ")
                    .replaceAll("\\W*((?i)shit(?-i))\\W*", " **** ")
                    .replaceAll("\\W*((?i)nigger(?-i))\\W*", " ****** ")
                    .replaceAll("\\W*((?i)dyke(?-i))\\W*", " **** ")
                    .replaceAll("\\W*((?i)dike(?-i))\\W*", " **** ")
                    .replaceAll("\\W*((?i)chode(?-i))\\W*", " ***** ")
                    .replaceAll("\\W*((?i)choad(?-i))\\W*", " ***** ")
                    .replaceAll("\\W*((?i)cunt(?-i))\\W*", " **** ")
                    .replaceAll("\\W*((?i)kunt(?-i))\\W*", " **** ")
                    .replaceAll("\\W*((?i)dick(?-i))\\W*", " **** ")
                    .replaceAll("\\W*((?i)cock(?-i))\\W*", " **** ")
                    .replaceAll("\\W*((?i)tits(?-i))\\W*", " **** ")
                    .replaceAll("\\W*((?i)cum(?-i))\\W*", " *** ")
                    .replaceAll("\\W*((?i)coon(?-i))\\W*", " **** ")
                    .replaceAll("\\W*((?i)douche(?-i))\\W*", " ****** ")
                    .replaceAll("\\W*((?i)jizz(?-i))\\W*", " **** ")
                    .replaceAll("\\W*((?i)fuck(?-i))\\W*", " **** ")
                    .replaceAll("\\W*((?i)pussy(?-i))\\W*", " ***** ")
                    .replaceAll("\\W*((?i)retard(?-i))\\W*", " ****** ")
                    .replaceAll("\\W*((?i)twat(?-i))\\W*", " **** ")
                    .replaceAll("\\W*((?i)hoe(?-i))\\W*", " *** ")
                    .replaceAll("\\W*((?i)whore(?-i))\\W*", " ****" );

        }
        return finalResult;
    }
}
