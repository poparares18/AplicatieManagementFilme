package com.example.aplicatiemanagementfilme.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.aplicatiemanagementfilme.R;
import com.example.aplicatiemanagementfilme.database.model.Movie;

import java.util.List;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {

    private Context context;
    private List<Movie> movieList;
    private LayoutInflater inflater;
    private int resource;
    private DateConverter dateConverter = new DateConverter();

    public MovieListViewAdapter(@NonNull Context context, int resource,
                                @NonNull List<Movie> objects, LayoutInflater inflater) {
        super(context, resource, objects);

        this.context = context;
        this.movieList = objects;
        this.inflater = inflater;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        Movie movie = movieList.get(position);
        if (movie != null) {
            // Imagine
            ImageView ivPoster = view.findViewById(R.id.iv_poster_browserRow);
            Glide.with(view).load(movie.getPosterUrl()).into(ivPoster);

            // Titlu
            TextView tvTitle = view.findViewById(R.id.tv_title_browserRow);
            tvTitle.setText(movie.getTitle());

            // Imdb rating
            TextView tvImdb = view.findViewById(R.id.tv_imdbRating_browserRow);
            tvImdb.setText(String.valueOf(movie.getImdbRating()));

            // Data
            TextView tvDate = view.findViewById(R.id.tv_releaseDate_browserRow);
            tvDate.setText(dateConverter.toString(movie.getReleaseDate()));

            // Gen
            TextView tvGenre = view.findViewById(R.id.tv_genres_browserRow);
            String genresString = "";
            for (int i = 0; i < movie.getGenres().size(); i++) {
                if(i != movie.getGenres().size() - 1) {
                    genresString += movie.getGenres().get(i) + ", ";
                }
                else{
                    genresString += movie.getGenres().get(i);
                }
            }
            tvGenre.setText(genresString);
        }
        return view;
    }
}
