package com.example.aplicatiemanagementfilme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aplicatiemanagementfilme.asyncTask.AsyncTaskRunner;
import com.example.aplicatiemanagementfilme.asyncTask.Callback;
import com.example.aplicatiemanagementfilme.database.model.UserAccount;
import com.example.aplicatiemanagementfilme.fragments.ViewPagerAdapter;
import com.example.aplicatiemanagementfilme.fragments.WatchListFragment;
import com.example.aplicatiemanagementfilme.network.HttpManager;
import com.example.aplicatiemanagementfilme.database.model.Movie;
import com.example.aplicatiemanagementfilme.util.MovieJsonParser;
import com.example.aplicatiemanagementfilme.util.OmdbMoviesIdParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private TextView tvLoading;
    private ImageView ivLoading;

    public static final String URL_LOADING_GIF = "https://upload.wikimedia.org/wikipedia/commons/b/b1/Loading_icon.gif";
    private final String URL_MOVIES = "https://jsonkeeper.com/b/A4PU";
    private List<Movie> movieList = new ArrayList<>();
    private AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();

    private Intent intent;
    public static UserAccount currentUserAccount;

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializare componente
        initComponents();
        //initViewPagerAdapter();

        // Preluare din HTTP
        getMoviesFromHttp(URL_MOVIES);
    }


    // Functii
    // Initializare componente
    private void initComponents() {
        // Components
        viewPager = findViewById(R.id.viewPager_main);
        fragmentManager = getSupportFragmentManager();
        initLoading();

        // Intent si user curent
        intent = getIntent();
        currentUserAccount = (UserAccount) intent.getSerializableExtra(LogInActivity.USER_INFORMATION_KEY);
    }

    // Setare adapter
    private void initViewPagerAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragmentManager, movieList);
        viewPager.setAdapter(adapter);
    }

    // Preluare filme din HTTP
    private void getMoviesFromHttp(String url) {
        Callable<String> callable = new HttpManager(url);

        asyncTaskRunner.executeAsync(callable, new Callback<String>() {
            @Override
            public void runResultOnUiThread(String result) {
                movieList.addAll(MovieJsonParser.fromJson(result));
                Collections.shuffle(movieList);

                // Initializare swipe view
                finishLoading();
                initComponents();
                initViewPagerAdapter();
            }
        });
    }


    // Initializare loading
    private void initLoading() {
        tvLoading = findViewById(R.id.tv_loading_main);
        ivLoading = findViewById(R.id.iv_loading_main);
        Glide.with(getApplicationContext()).load(URL_LOADING_GIF).into(ivLoading);
    }

    // Terminare loading
    private void finishLoading() {
        tvLoading.setVisibility(View.GONE);
        ivLoading.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Toast.makeText(getBaseContext(),
                    getString(R.string.toast_press_back_main),
                    Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

}