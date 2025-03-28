package fr.iut.androidprojet.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import fr.iut.androidprojet.model.QuestionFr;

@Dao
public interface QuestionFrDao {

    @Query("SELECT * FROM QuestionFr ORDER BY id ASC")
    List<QuestionFr> getAllQuestions();

    @Insert
    void insertAll(List<QuestionFr> questions);

    @Insert
    void insert(QuestionFr question);

    @Query("SELECT count(*) FROM QuestionFr")
    int countQuestions();
}