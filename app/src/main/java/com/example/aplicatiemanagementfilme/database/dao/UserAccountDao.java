package com.example.aplicatiemanagementfilme.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.aplicatiemanagementfilme.database.model.UserAccount;

import java.util.List;

@Dao
public interface UserAccountDao {

    // Operatii
    // Insert user
    @Insert
    long insert(UserAccount userAccount);

    // Get pentru testare username si email
    @Query("select * from userAccounts where username LIKE (:username) OR email LIKE (:email)")
    List<UserAccount> getUsersByUsernameOrEmail(String username, String email);

    // Get pentru testare username
    @Query("select * from userAccounts where username LIKE (:username)")
    List<UserAccount> getUsersByUsername(String username);

    // Delete
    @Delete
    int delete(UserAccount userAccount);

    // Update
    @Update
    int update(UserAccount userAccount);

}
