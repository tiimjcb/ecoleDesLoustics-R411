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

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserById(int userId);

    @Query("UPDATE users SET score = score + :points WHERE id = :userId")
    void addScoreToUser(int userId, int points);
}
