package com.example.aplicatiemanagementfilme.util;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
    private static final String FORMAT_DATE = "yyyy-MM-dd";
    private static final String FORMAT_DATE_OMDB = "dd MMM yyyy";
    private final SimpleDateFormat formatter;
    private final SimpleDateFormat formatterOmdb;

    public DateConverter() {
        formatter = new SimpleDateFormat(FORMAT_DATE, Locale.US);
        formatterOmdb = new SimpleDateFormat(FORMAT_DATE_OMDB, Locale.US);
    }


    public Date toDateFromOmdbStringDate(String omdbDateString) {
        try {
            Date omdbDate = formatterOmdb.parse(omdbDateString);
            String correctDate = formatter.format(omdbDate);
            return toDate(correctDate);
        }
        catch (ParseException e){
            return null;
        }
    }


    @TypeConverter
    public Date toDate(String value) {
        try {
            return formatter.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    @TypeConverter
    public String toString(Date value) {
        if (value == null) {
            return null;
        }
        return formatter.format(value);
    }
}
