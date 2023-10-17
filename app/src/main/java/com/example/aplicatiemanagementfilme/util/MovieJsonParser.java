package com.example.aplicatiemanagementfilme.util;

import com.example.aplicatiemanagementfilme.database.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieJsonParser {

    public static List<Movie> fromJson(String json) {
        List<Movie> movieList = new ArrayList<>();
        DateConverter dateConverter = new DateConverter();

        try {
            JSONArray moviesJsonArray = new JSONArray(json);

            for (int i = 0; i < moviesJsonArray.length(); i++) {
                JSONObject movieJson = moviesJsonArray.getJSONObject(i);

                // Titlu
                String title = movieJson.getString("title");
                String originalTitle = movieJson.getString("originalTitle");
                if (!originalTitle.equals("")) {
                    title = originalTitle;
                }

                // Gen-uri
                List<String> genres = new ArrayList<>();
                JSONArray genresJson = movieJson.getJSONArray("genres");
                for (int j = 0; j < genresJson.length(); j++) {
                    String genre = genresJson.getString(j);
                    genres.add(genre);
                }

                // Data lansarii
                Date releaseDate = dateConverter.toDate(movieJson.getString("releaseDate"));

                // Poveste
                String storyline = movieJson.getString("storyline");

                // Actori
                List<String> actors = new ArrayList<>();
                JSONArray actorsJson = movieJson.getJSONArray("actors");
                for (int j = 0; j < actorsJson.length(); j++) {
                    String actor = actorsJson.getString(j);
                    actors.add(actor);
                }

                // IMDB rating
                float imdbRating = (float) movieJson.getDouble("imdbRating");

                // Poster URL
                String posterUrl = movieJson.getString("posterurl");

                // Adaugare
                Movie movie = new Movie(title, releaseDate, genres, storyline,
                        actors, imdbRating, posterUrl);
                movieList.add(movie);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return movieList;
    }
}
