package fr.iut.androidprojet.quizzFrancais;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.iut.androidprojet.R;
import fr.iut.androidprojet.database.DatabaseClient;
import fr.iut.androidprojet.model.QuestionFr;
import fr.iut.androidprojet.model.User;
import fr.iut.androidprojet.multiplications.MultiplicationActivity;

public class FrancaisQuizActivity extends AppCompatActivity {

    private TextView textQuestion, textProgress, textTimer, textFeedback;
    private TextView btnAnswerA, btnAnswerB, btnAnswerC, btnQuit;
    private ImageView heart1, heart2, heart3;

    private User currentUser;

    private List<QuestionFr> questions;
    private int currentQuestionIndex = 0;
    private int remainingLives = 3;

    private CountDownTimer timer;
    private long elapsedTime = 0;
    private boolean isWaiting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_fr);

        initViews();
        loadQuestions();
        setupListeners();

        int userId = getIntent().getIntExtra("user_id", -1);

        loadUserFromDatabase(userId);

    }

    private void initViews() {
        textQuestion = findViewById(R.id.textQuestion);
        textProgress = findViewById(R.id.textQuestionProgress);
        textTimer = findViewById(R.id.textTimer);
        textFeedback = findViewById(R.id.textFeedback);
        btnAnswerA = findViewById(R.id.btnAnswerA);
        btnAnswerB = findViewById(R.id.btnAnswerB);
        btnAnswerC = findViewById(R.id.btnAnswerC);
        btnQuit = findViewById(R.id.btnQuit);
        heart1 = findViewById(R.id.heart1);
        heart2 = findViewById(R.id.heart2);
        heart3 = findViewById(R.id.heart3);
    }

    private void loadQuestions() {
        new Thread(() -> {
            questions = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .questionFrDao()
                    .getAllQuestions();

            runOnUiThread(() -> {
                if (questions.isEmpty()) {
                    Toast.makeText(this, "Aucune question disponible", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    startTimer();
                    showQuestion();
                }
            });
        }).start();
    }

    private void setupListeners() {
        btnAnswerA.setOnClickListener(v -> handleAnswer(btnAnswerA.getText().toString()));
        btnAnswerB.setOnClickListener(v -> handleAnswer(btnAnswerB.getText().toString()));
        btnAnswerC.setOnClickListener(v -> handleAnswer(btnAnswerC.getText().toString()));
        btnQuit.setOnClickListener(v -> finish());
    }

    private void handleAnswer(String selectedAnswer) {
        if (isWaiting) return;

        QuestionFr q = questions.get(currentQuestionIndex);
        if (selectedAnswer.equals(q.getCorrectAnswer())) {
            textFeedback.setText("Bonne réponse ! Question suivante..");
            textFeedback.setTextColor(ContextCompat.getColor(this, R.color.green));

            isWaiting = true;
            new Handler().postDelayed(() -> {
                textFeedback.setText("");
                nextQuestion();
                isWaiting = false;
            }, 1000);
        } else {
            textFeedback.setText("Mauvaise réponse !");
            textFeedback.setTextColor(ContextCompat.getColor(this, R.color.red));
            remainingLives--;
            updateHearts();
            if (remainingLives <= 0) {
                goToGameOver();
            }
        }
    }

    private void showQuestion() {
        QuestionFr q = questions.get(currentQuestionIndex);
        textProgress.setText("Question " + (currentQuestionIndex + 1) + "/" + questions.size());
        textQuestion.setText(q.getQuestion());
        btnAnswerA.setText(q.getOptionA());
        btnAnswerB.setText(q.getOptionB());
        btnAnswerC.setText(q.getOptionC());
        textFeedback.setText("");
    }

    private void nextQuestion() {
        if (currentQuestionIndex == questions.size() - 1) {
            goToGameOver();
        } else {
            currentQuestionIndex++;
            showQuestion();
        }
    }

    private void updateHearts() {
        ImageView[] hearts = {heart1, heart2, heart3};
        for (int i = 0; i < 3; i++) {
            hearts[i].setImageResource(i < remainingLives ?
                    R.drawable.ic_filled_heart : R.drawable.ic_outlined_heart);
        }
    }

    private void startTimer() {
        timer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                elapsedTime++;
                String formatted = String.format("%02d:%02d",
                        TimeUnit.SECONDS.toMinutes(elapsedTime),
                        elapsedTime % 60);
                textTimer.setText(formatted);
            }

            @Override
            public void onFinish() {}
        }.start();
    }

    private void goToGameOver() {
        if (timer != null) timer.cancel();
        Intent intent = new Intent(this, FrancaisQuizResultatActivity.class);
        intent.putExtra("score", currentQuestionIndex);
        intent.putExtra("user_id", currentUser.getId());
        startActivity(intent);
        finish();
    }

    private void loadUserFromDatabase(int userId) {
        class GetUserTask extends AsyncTask<Void, Void, User> {
            @Override
            protected User doInBackground(Void... voids) {
                return DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase().userDao().getUserById(userId);
            }

            @Override
            protected void onPostExecute(User user) {
                if (user == null) {
                    Toast.makeText(FrancaisQuizActivity.this, "Utilisateur introuvable", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    currentUser = user;
                }
            }
        }
        new GetUserTask().execute();
    }

}