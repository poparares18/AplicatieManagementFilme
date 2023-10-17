package com.example.aplicatiemanagementfilme.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.aplicatiemanagementfilme.database.model.Movie;
import com.example.aplicatiemanagementfilme.database.model.UserAccount;

import java.util.List;

@Dao
public interface MovieDao {

    // Operatii
    // Insert movie
    @Insert
    long insert(Movie movie);

    // Gel all movies by watch list id
    @Query("SELECT * from movies where watchListId=(:watchListId)")
    List<Movie> getMoviesByWatchListId(long watchListId);

    // Delete
    @Delete
    int delete(Movie movie);

    // Delete all by watch list id
    @Query("DELETE from movies where watchListId=(:watchListId)")
    int deleteMoviesByWatchListId(long watchListId);
}
