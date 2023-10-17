package com.example.aplicatiemanagementfilme.util;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

public class StringListConverter {

    @TypeConverter
    public static String toString(List<String> list) {
        try {
            return list.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static List<String> toList(String string) {
        try {
            List<String> stringList = new ArrayList<>();

            string = string.replace("[", "");
            string = string.replace("]", "");
            String[] stringVect = string.split(",");
            for (int i = 0; i < stringVect.length; i++) {
                stringList.add(stringVect[i]);
            }

            return stringList;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
