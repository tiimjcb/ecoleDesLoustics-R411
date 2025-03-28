package fr.iut.androidprojet.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class QuestionFr {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String question = "";
    @NonNull public String optionA = "";
    @NonNull public String optionB = "";
    @NonNull public String optionC = "";
    @NonNull public String correctAnswer = "";

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }


    public QuestionFr(@NonNull String question, @NonNull String optionA, @NonNull String optionB,
                      @NonNull String optionC, @NonNull String correctAnswer) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
}