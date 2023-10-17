package com.example.aplicatiemanagementfilme.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aplicatiemanagementfilme.MovieDetailsActivity;
import com.example.aplicatiemanagementfilme.R;
import com.example.aplicatiemanagementfilme.asyncTask.AsyncTaskRunner;
import com.example.aplicatiemanagementfilme.asyncTask.Callback;
import com.example.aplicatiemanagementfilme.database.model.Movie;
import com.example.aplicatiemanagementfilme.network.HttpManager;
import com.example.aplicatiemanagementfilme.util.MovieListViewAdapter;
import com.example.aplicatiemanagementfilme.util.OmdbMoviesIdParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class MovieBrowserFragment extends Fragment {

    private ListView lvMovies;
    private TextInputEditText tietSearch;
    private FloatingActionButton fabSearch;

    private static final String MOVIE_LIST_BROWSER_KEY = "MOVIE_LIST_BROWSER_KEY";
    public static final String MOVIE_DETAILS_KEY = "MOVIE_DETAILS_KEY";
    public static final String ADD_MOVIE_VISIBILITY_KEY = "ADD_MOVIE_VISIBILITY_KEY";
    public static final String MOVIE_POSITION_KEY = "MOVIE_POSITION_KEY";

    private List<Movie> movieList;
    private List<Movie> movieListFromOmdb = new ArrayList<>();

    private AsyncTaskRunner asyncTaskRunner;
    private String urlOmdb = "https://www.omdbapi.com/?s=REPLACE&apikey=e142d467";

    private MovieListViewAdapter adapter;
    private MovieListViewAdapter omdbAdapter;
    private int currentAdapter = 0;


    // Constructor
    public MovieBrowserFragment() {
        // Required empty public constructor
    }

    // New instance
    public static MovieBrowserFragment newInstance(List<Movie> movieList) {
        MovieBrowserFragment fragment = new MovieBrowserFragment();
        Bundle args = new Bundle();
        args.putSerializable(MOVIE_LIST_BROWSER_KEY, (Serializable) movieList);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_browser, container, false);


        // Initializare componente
        initComponents(view);

        // Adaugare eveniment on click pe fab search
        fabSearch.setOnClickListener(onClickFabSearchListener());

        // Adaugare eveniment pe schimbare text tiet search
        tietSearch.addTextChangedListener(onTextChangedSearchListener());

        // Adaugare eveniment click pe element din lista
        lvMovies.setOnItemClickListener(onClickLvMoviesElementListener());

        return view;
    }



    // Functii
    // Initializare componenete
    private void initComponents(View view) {
        lvMovies = view.findViewById(R.id.lv_movieBrowser);
        tietSearch = view.findViewById(R.id.tiet_search_movieBrowser);
        fabSearch = view.findViewById(R.id.fab_search_movieBrowser);

        asyncTaskRunner = new AsyncTaskRunner();

        if (getArguments() != null) {
            movieList = (List<Movie>) getArguments().getSerializable(MOVIE_LIST_BROWSER_KEY);
        }

        if (getContext() != null) {
            // Creare adapter pentru ListView
            adapter = new MovieListViewAdapter(getContext().getApplicationContext(),
                    R.layout.lv_row_movie_browser, movieList, getLayoutInflater());
            omdbAdapter = new MovieListViewAdapter(getContext().getApplicationContext(),
                    R.layout.lv_row_movie_browser, movieListFromOmdb, getLayoutInflater());

            lvMovies.setAdapter(adapter);
        }
    }


    // Functie on click pe element al unui list view ( movie browser list view )
    private AdapterView.OnItemClickListener onClickLvMoviesElementListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), MovieDetailsActivity.class);
                if (currentAdapter == 0) {
                    intent.putExtra(MOVIE_DETAILS_KEY, movieList.get(position));
                } else {
                    intent.putExtra(MOVIE_DETAILS_KEY, movieListFromOmdb.get(position));
                }
                intent.putExtra(MOVIE_POSITION_KEY, position);
                intent.putExtra(ADD_MOVIE_VISIBILITY_KEY, true);
                startActivity(intent);
            }
        };
    }


    // Fucntie on click fab search
    private View.OnClickListener onClickFabSearchListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    String search = tietSearch.getText().toString().trim();
                    search = search.replace(" ", "+");
                    urlOmdb = urlOmdb.replace("REPLACE", search);

                    // Inchidrere astatura
                    closeKeyboard();

                    getOmdbSearchFromHttp(urlOmdb);
                    urlOmdb = urlOmdb.replace(search, "REPLACE");
                }
            }
        };
    }

    // Functie validare
    public boolean validate() {
        if (tietSearch.getText().equals("") || tietSearch.getText().toString()
                .replace(" ", "").length() <= 2) {
            Toast.makeText(getContext(),
                    getString(R.string.toast_search_movieBrowser),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    // Preluare search omdb din http
    private void getOmdbSearchFromHttp(String urlOmdb) {
        Callable<String> callable = new HttpManager(urlOmdb);
        asyncTaskRunner.executeAsync(callable, new Callback<String>() {
            @Override
            public void runResultOnUiThread(String result) {

                List<String> omdbMovieIdList = getIdListFromJson(result);
                if (omdbMovieIdList.size() == 0) {
                    Toast.makeText(getContext(),
                            getString(R.string.toast_movieNotFound_movieBrowser),
                            Toast.LENGTH_SHORT).show();
                } else {
                    getMoviesFromHttpByImdbId(omdbMovieIdList);
                }
            }
        });
    }

    // Preluare lista filme prin omdb api dupa imdb id-urile lor
    private void getMoviesFromHttpByImdbId(List<String> omdbMovieIdList) {
        String urlOmdb = "https://www.omdbapi.com/?i=REPLACE&plot=full&apikey=e142d467";
        movieListFromOmdb.clear();
        adapterToOmdbAdapter();

        for (int i = 0; i < omdbMovieIdList.size(); i++) {
            urlOmdb = urlOmdb.replace("REPLACE", omdbMovieIdList.get(i));

            Callable<String> callable = new HttpManager(urlOmdb);
            asyncTaskRunner.executeAsync(callable, callbackAdaugareFilmeOmdbInLista());

            urlOmdb = urlOmdb.replace(omdbMovieIdList.get(i), "REPLACE");
        }
    }

    // callback adaugare filme omdb in lista
    private Callback<String> callbackAdaugareFilmeOmdbInLista() {
        return new Callback<String>() {
            @Override
            public void runResultOnUiThread(String result) {

                Movie movie = OmdbMoviesIdParser.getMovieFromOmdbById(result);
                movieListFromOmdb.add(movie);
                //Toast.makeText(getContext(), movieListFromOmdb.toString(), Toast.LENGTH_SHORT).show();
                notifyInternalAdapter();

            }
        };
    }


    // Parsare jsonOmdb by search pentru movie id
    private List<String> getIdListFromJson(String json) {
        try {
            List<String> stringList = new ArrayList<>();
            JSONObject jsonResponse = new JSONObject(json);
            String respone = jsonResponse.getString("Response");

            if (!respone.equals("False")) {
                JSONArray omdbMovieArrayJ = jsonResponse.getJSONArray("Search");
                for (int i = 0; i < omdbMovieArrayJ.length(); i++) {
                    String omdbMovieId = omdbMovieArrayJ.getJSONObject(i).getString("imdbID");
                    stringList.add(omdbMovieId);
                }
            }

            return stringList;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    // Schimbare adaptor
    // Notificare schimbare adapter
    public void notifyInternalAdapter() {
        ArrayAdapter adapter = (ArrayAdapter) lvMovies.getAdapter();
        adapter.notifyDataSetChanged();
    }

    // Schimbare adaptor la omdbAdapter
    public void adapterToOmdbAdapter() {
        lvMovies.setAdapter(omdbAdapter);
        currentAdapter = 1;
    }

    // Schimbare omdbAdapter la adapter
    public void omdbAdapterToAdapter() {
        lvMovies.setAdapter(adapter);
        currentAdapter = 0;
    }


    // On text changed listener tiet search
    private TextWatcher onTextChangedSearchListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tietSearch.getText().toString().equals("")) {
                    // Inchidrere astatura
                    closeKeyboard();

                    omdbAdapterToAdapter();
                    notifyInternalAdapter();
                }
            }
        };
    }


    // Inchidere tastatura
    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(getContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}