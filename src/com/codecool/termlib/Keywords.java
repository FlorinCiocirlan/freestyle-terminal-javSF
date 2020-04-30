package com.codecool.termlib;

import java.util.ArrayList;
import java.util.Arrays;

public class Keywords {


    public static ArrayList<String> stringToArray(String input) {
        String[] words = input.toUpperCase().split(" ");
        return new ArrayList<>(Arrays.asList(words));
    }

    static ArrayList<String> retainCommonWithBase(String strings) {
        ArrayList<String> attr = new ArrayList<>();
        ArrayList<Attribute> attributeList = new ArrayList<>(Arrays.asList(Attribute.values()));
        for(Attribute at : attributeList){
            attr.add(at.toString());
        }
        ArrayList<String> userInput = stringToArray(strings);
         userInput.retainAll(attr);
         userInput.add("test");
         return userInput;

    }
}

