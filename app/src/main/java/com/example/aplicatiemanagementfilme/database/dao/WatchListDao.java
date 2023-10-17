package com.example.aplicatiemanagementfilme.database.dao;

import android.widget.ListView;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.aplicatiemanagementfilme.database.model.WatchList;

import java.util.List;

@Dao
public interface WatchListDao {

    // Operatii
    // Insert Watch list
    @Insert
    long insert(WatchList watchList);

    // Select all pentru un user, dupa user id
    @Query("SELECT * from watchLists WHERE userAccountId = (:userAccountId)")
    List<WatchList> getWatchListsByUserAccountId(long userAccountId);

    // Update pentru incrementarea numarului de filme dintr un watch list
    @Query("UPDATE watchLists SET movieCount = movieCount + 1 WHERE id=(:watchListId)")
    int updateMovieCountPlus(long watchListId);

    // Update pentru decrementarea numarului de filme dintr un watch list
    @Query("UPDATE watchLists SET movieCount = movieCount - 1 WHERE id=(:watchListId)")
    int updateMovieCountMinus(long watchListId);

    // Stergere watch list
    @Delete
    int delete(WatchList watchList);

    // Editare nume watch list
    @Query("UPDATE watchLists SET wlName = (:newName) WHERE id=(:wlId)")
    int updateWLnameByWLid(String newName, long wlId);

    // Delete by userAccountId
    @Query("DELETE from watchLists WHERE userAccountId=(:userAccountId)")
    int deleteByUserAccountId(long userAccountId);
}
