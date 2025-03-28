package fr.iut.androidprojet.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import fr.iut.androidprojet.dao.UserDao;
import fr.iut.androidprojet.dao.QuestionFrDao;
import fr.iut.androidprojet.model.User;
import fr.iut.androidprojet.model.QuestionFr;

@Database(entities = {User.class, QuestionFr.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract QuestionFrDao questionFrDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE User ADD COLUMN score INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `QuestionFr` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`question` TEXT NOT NULL, " +
                    "`optionA` TEXT NOT NULL, " +
                    "`optionB` TEXT NOT NULL, " +
                    "`optionC` TEXT NOT NULL, " +
                    "`correctAnswer` TEXT NOT NULL)");
        }
    };
}