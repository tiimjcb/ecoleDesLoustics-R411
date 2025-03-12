package fr.iut.androidprojet.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
import fr.iut.androidprojet.model.User;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();
}