package com.example.aplicatiemanagementfilme.util;

import com.example.aplicatiemanagementfilme.database.model.Movie;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OmdbMoviesIdParser {

    private static DateConverter dateConverter = new DateConverter();

    public static Movie getMovieFromOmdbById(String json) {
        try {

            JSONObject jsonObject = new JSONObject(json);

            // Construire nou film
            // Title
            String title = jsonObject.getString("Title");

            // Release date
            String releaseDateString = jsonObject.getString("Released");
            Date releaseDate = dateConverter.toDateFromOmdbStringDate(releaseDateString);

            // Genres
            String genresString = jsonObject.getString("Genre");
            List<String> genres = stringToStringList(genresString);

            // Story line
            String storyLine = jsonObject.getString("Plot");

            // Actors
            String actorsString = jsonObject.getString("Actors");
            List<String> actors = stringToStringList(actorsString);

            // imdb Rating
            String imdbRatingString = jsonObject.getString("imdbRating");
            float imdbRating = 0;
            if(!imdbRatingString.equals("N/A")){
                imdbRating = Float.valueOf(imdbRatingString);
            }

            // poster Url
            String posterUrl = jsonObject.getString("Poster");

            Movie movie = new Movie(title, releaseDate, genres,
                    storyLine, actors, imdbRating, posterUrl);


            return movie;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private static List<String> stringToStringList(String string) {
        List<String> stringList = new ArrayList<>();

        String[] stringArray = string.split(",");
        for (int i = 0; i < stringArray.length; i++) {
            stringList.add(stringArray[i]);
        }

        return stringList;
    }
}
