package fr.iut.androidprojet.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import fr.iut.androidprojet.dao.UserDao;
import fr.iut.androidprojet.model.User;

@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}