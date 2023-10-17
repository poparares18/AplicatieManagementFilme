package com.example.aplicatiemanagementfilme;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aplicatiemanagementfilme.asyncTask.Callback;
import com.example.aplicatiemanagementfilme.database.model.Movie;
import com.example.aplicatiemanagementfilme.database.model.WatchList;
import com.example.aplicatiemanagementfilme.database.service.MovieService;
import com.example.aplicatiemanagementfilme.fragments.MovieBrowserFragment;
import com.example.aplicatiemanagementfilme.fragments.WatchListFragment;
import com.example.aplicatiemanagementfilme.util.MovieListViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class WatchListDetailsActivity extends AppCompatActivity {

    public static final int POSITION_REQUEST_CODE = 20;
    // Components
    private TextView tv_wl_title;
    private ListView lvMovies;

    private List<Movie> movieList = new ArrayList<>();

    // Intent
    private Intent intent;
    // Watch list primit
    private WatchList watchListFromIntent;
    // Service
    private MovieService movieService;
    // Pozitie wl
    private int positionWL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_list_details);

        // Initializare componenete
        initComponents();

        // Preluare filme din baza de date
        getMoviesFromDB();

        // Construire pagina cu elementele din wathc list
        buildPageFromWatchLsit();

        // Adaugare eveniment onClick pe elemente list view
        lvMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), MovieDetailsActivity.class);
                intent.putExtra(MovieBrowserFragment.MOVIE_DETAILS_KEY, movieList.get(position));
                intent.putExtra(MovieBrowserFragment.ADD_MOVIE_VISIBILITY_KEY, false);
                intent.putExtra(MovieBrowserFragment.MOVIE_POSITION_KEY, position);
                startActivityForResult(intent, POSITION_REQUEST_CODE);
            }
        });
    }


    // Functii
    // Initializare componenete
    private void initComponents() {
        // Compoennete
        tv_wl_title = findViewById(R.id.tv_title_wl_act);
        lvMovies = findViewById(R.id.lv_wl_act);

        // Intent
        intent = getIntent();

        // Pozitie wl
        positionWL = (int) intent.getSerializableExtra(WatchListFragment.WATCH_LIST_POSITION_KEY);

        // Service
        movieService = new MovieService(getApplicationContext());

        // Watch list primit
        watchListFromIntent = (WatchList) intent
                .getSerializableExtra(WatchListFragment.WATCH_LIST_INFORMATION_KEY);
    }


    // construire pagina cu elementele din watch list
    private void buildPageFromWatchLsit() {
        tv_wl_title.setText(watchListFromIntent.getWlName());
        MovieListViewAdapter adapter = new MovieListViewAdapter(getApplicationContext(),
                R.layout.lv_row_movie_browser, movieList, getLayoutInflater());
        lvMovies.setAdapter(adapter);
    }


    // Preluare filme din db
    private void getMoviesFromDB() {
        movieService.getMoviesByWatchListId(watchListFromIntent.getId(), new Callback<List<Movie>>() {
            @Override
            public void runResultOnUiThread(List<Movie> result) {
                movieList.clear();
                movieList.addAll(result);

                // Notificare adapter
                notifyInternalAdapter();
            }
        });
    }


    // Notificare schimbare adapter
    private void notifyInternalAdapter() {
        ArrayAdapter adapter = (ArrayAdapter) lvMovies.getAdapter();
        adapter.notifyDataSetChanged();
    }

    // Primirea pozitiei pentru a scote filmul din lista
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == POSITION_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Notify adapter pt lista filme
            int position = (int) data.getSerializableExtra(MovieBrowserFragment.MOVIE_POSITION_KEY);
            movieList.remove(position);
            notifyInternalAdapter();

            // Notify adapter pt watch list
            watchListFromIntent.setMovieCount(watchListFromIntent.getMovieCount() - 1);

            WatchListFragment.watchListArray.set(positionWL, watchListFromIntent);
            WatchListFragment.notifyInternalAdapter();
        }
    }
}