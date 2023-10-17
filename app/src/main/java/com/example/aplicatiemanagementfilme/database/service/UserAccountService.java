package com.example.aplicatiemanagementfilme.database.service;

import android.content.Context;
import android.content.Intent;

import androidx.room.ColumnInfo;

import com.example.aplicatiemanagementfilme.asyncTask.AsyncTaskRunner;
import com.example.aplicatiemanagementfilme.asyncTask.Callback;
import com.example.aplicatiemanagementfilme.database.DatabaseManager;
import com.example.aplicatiemanagementfilme.database.dao.MovieDao;
import com.example.aplicatiemanagementfilme.database.dao.UserAccountDao;
import com.example.aplicatiemanagementfilme.database.dao.WatchListDao;
import com.example.aplicatiemanagementfilme.database.model.UserAccount;
import com.example.aplicatiemanagementfilme.fragments.WatchListFragment;

import java.util.List;
import java.util.concurrent.Callable;

public class UserAccountService {
    private UserAccountDao userAccountDao;
    private WatchListDao watchListDao;
    private MovieDao movieDao;
    private AsyncTaskRunner asyncTaskRunner;

    public UserAccountService(Context context) {
        userAccountDao = DatabaseManager.getInstance(context).getUserAccountDao();
        watchListDao = DatabaseManager.getInstance(context).getWatchListDao();
        movieDao = DatabaseManager.getInstance(context).getMovieDao();
        asyncTaskRunner = new AsyncTaskRunner();
    }

    // Metode
    // Insert
    public void insert(UserAccount userAccount, Callback<UserAccount> callback) {
        Callable<UserAccount> callable = new Callable<UserAccount>() {
            @Override
            public UserAccount call() {
                if (userAccount == null) {
                    return null;
                }
                long id = userAccountDao.insert(userAccount);
                if (id == -1) {
                    return null;
                }

                userAccount.setId(id);
                return userAccount;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }

    // Get pt username si email
    public void getUsersByUsernameOrEmail(String username, String email,
                                          Callback<List<UserAccount>> callback) {
        Callable<List<UserAccount>> callable = new Callable<List<UserAccount>>() {
            @Override
            public List<UserAccount> call() {
                return userAccountDao.getUsersByUsernameOrEmail(username, email);
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }


    // Get pt username
    public void getUsersByUsername(String username, Callback<List<UserAccount>> callback) {
        Callable<List<UserAccount>> callable = new Callable<List<UserAccount>>() {
            @Override
            public List<UserAccount> call() {
                return userAccountDao.getUsersByUsername(username);
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }

    // Delete user
    public void delete(UserAccount userAccount, Callback<Integer> callback) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() {
                if (userAccount == null) {
                    return -1;
                }

                for (int i = 0; i < WatchListFragment.watchListArray.size(); i++) {
                    movieDao.deleteMoviesByWatchListId(WatchListFragment.watchListArray.get(i).getId());
                }
                watchListDao.deleteByUserAccountId(userAccount.getId());
                return userAccountDao.delete(userAccount);
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }

    // Update user
    public void update(UserAccount userAccount, Callback<UserAccount> callback) {
        Callable<UserAccount> callable = new Callable<UserAccount>() {
            @Override
            public UserAccount call() {
                if (userAccount == null) {
                    return null;
                }

                int count = userAccountDao.update(userAccount);
                if (count != 1) {
                    return null;
                }

                return userAccount;
            }
        };

        asyncTaskRunner.executeAsync(callable, callback);
    }
}
