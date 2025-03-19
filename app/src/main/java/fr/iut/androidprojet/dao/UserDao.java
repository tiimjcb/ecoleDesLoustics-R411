package fr.iut.androidprojet.dao;

import androidx.room.Dao;
import androidx.room.Delete;
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

    @Delete
    void deleteUser(User user);

    @Query("SELECT score FROM users WHERE id = :userId")
    int getUserScore(int userId);

    @Query("UPDATE users SET score = :score WHERE id = :userId")
    void updateUserScore(int userId, int score);
}