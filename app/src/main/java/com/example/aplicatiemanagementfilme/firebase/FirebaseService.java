package com.example.aplicatiemanagementfilme.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.aplicatiemanagementfilme.asyncTask.Callback;
import com.example.aplicatiemanagementfilme.util.ReviewMovie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseService {
    public static final String REVIEW_MOVIE_TABLE_NAME = "movie_review";

    private DatabaseReference database;
    private static FirebaseService firebaseService;

    private FirebaseService() {
        database = FirebaseDatabase.getInstance().getReference(REVIEW_MOVIE_TABLE_NAME);
    }

    public static FirebaseService getInstance() {
        if (firebaseService == null) {
            synchronized (FirebaseService.class) {
                if (firebaseService == null) {
                    firebaseService = new FirebaseService();
                }
            }
        }
        return firebaseService;
    }


    public void upsert(ReviewMovie reviewMovie) {
        if (reviewMovie == null) {
            return;
        }

        if (reviewMovie.getId() == null || reviewMovie.getId().trim().isEmpty()) {
            String id = database.push().getKey();
            reviewMovie.setId(id);
        }

        database.child(reviewMovie.getId()).setValue(reviewMovie);
    }


    public void attachDataChangeEventListener(final Callback<List<ReviewMovie>> callback) {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ReviewMovie> reviewMovieList = new ArrayList<>();

                //parcurgem lista de subnoduri din cel principal
                for (DataSnapshot data : snapshot.getChildren()) {
                    ReviewMovie reviewMovie = data.getValue(ReviewMovie.class);
                    if (reviewMovie != null) {
                        reviewMovieList.add(reviewMovie);
                    }
                }
                //trimitem lista catre activitatea prin intermediul callback-ului
                callback.runResultOnUiThread(reviewMovieList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("FirebaseService", "Data is not available");
            }
        });
    }
}
