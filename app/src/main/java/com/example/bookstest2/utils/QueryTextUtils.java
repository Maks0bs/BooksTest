package com.example.bookstest2.utils;

public class QueryTextUtils {
    public static String prepareQueryForSubmission(String s){

        String t = s;

        int i = 0;
        while(i < t.length()){
            if (t.charAt(i) == ' '){
                t = t.substring(0, i) + "%20" + t.substring(i + 1);
            }
            i++;
        }

        return t;
    }
}
