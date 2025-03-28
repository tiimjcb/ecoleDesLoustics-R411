package fr.iut.androidprojet.quizzFrancais;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import fr.iut.androidprojet.R;
import fr.iut.androidprojet.SelectExerciseActivity;
import fr.iut.androidprojet.database.DatabaseClient;
import fr.iut.androidprojet.model.User;

public class FrancaisQuizResultatActivity extends AppCompatActivity {

    private TextView textScore;
    private TextView btnBackToExercises, btnRetryQuiz;
    private int score;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultat_quiz_fr);

        textScore = findViewById(R.id.textScore);
        btnBackToExercises = findViewById(R.id.btnBackToExercises);
        btnRetryQuiz = findViewById(R.id.btnRetryQuiz);

        score = getIntent().getIntExtra("score", 0);
        int userId = getIntent().getIntExtra("user_id", -1);
        loadUserFromDatabase(userId);

        textScore.setText("Score : " + score + "/40");

        btnBackToExercises.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectExerciseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnRetryQuiz.setOnClickListener(v -> {
            Intent retryIntent = new Intent(this, FrancaisQuizActivity.class);
            retryIntent.putExtra("user_id", userId);
            startActivity(retryIntent);
            finish();
        });
    }

    private void updateUserScore() {
        if (currentUser.getId() == -1) return;

        class UpdateScoreTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .userDao()
                        .addScoreToUser(currentUser.getId(), score);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                Toast.makeText(FrancaisQuizResultatActivity.this,
                        "+" + score + " points ajoutés au score !",
                        Toast.LENGTH_SHORT).show();
            }
        }

        new UpdateScoreTask().execute();
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
                    Toast.makeText(FrancaisQuizResultatActivity.this, "Utilisateur introuvable", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    currentUser = user;
                    updateUserScore();
                }
            }
        }
        new GetUserTask().execute();
    }
}
