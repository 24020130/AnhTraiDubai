package org.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class StringCode {
    public static boolean stringIntersect(String a, String b, int len){
        if(len <= 0 || len > a.length() || len > b.length()){
            return false;
        }
        Set<String> s = new HashSet<>();
        for(int i = 0; i <= a.length() - len; i++){
            s.add(a.substring(i, i + len));
        }
        for(int i = 0; i <=b.length() - len; i++ ){
            if(s.contains(b.substring(i, i + len))){
                return true;
            }
        }
        return false;
    }

    public static int maxRun(String str){
        if (str == null || str.length() == 0) return 0;
        int max = 1;
        int cur = 1;
        for (int i = 1; i < str.length(); i++) {
            if (str.charAt(i) == str.charAt(i - 1)) {
                cur++;
                if (cur > max) max = cur;
            } else {
                cur = 1;
            }
        }
        return max;
    }

    public static String blowup(String str){
        if (str == null || str.length() == 0) return "";
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isDigit(c)) {
                int n = c - '0';
                if (i + 1 < str.length()) {
                    char next = str.charAt(i + 1);
                    for (int j = 0; j < n; j++) res.append(next);
                }
            } else {
                res.append(c);
            }
        }
        return res.toString();
    }
}