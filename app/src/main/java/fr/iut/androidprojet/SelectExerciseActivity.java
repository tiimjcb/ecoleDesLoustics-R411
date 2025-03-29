package fr.iut.androidprojet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import fr.iut.androidprojet.database.DatabaseClient;
import fr.iut.androidprojet.quizzFrancais.FrancaisSplashScreenActivity;
import fr.iut.androidprojet.calcul.CalculActivity;

public class SelectExerciseActivity extends AppCompatActivity {

    private TextView textSelectedUser, textUserScore, btnBackToUsers;
    private LinearLayout cardAdditions, cardMultiplications, cardFrancais;

    private int userId;
    private boolean isAnonymous;
    private String firstName, lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_exercise);

        textSelectedUser = findViewById(R.id.textSelectedUser);
        textUserScore = findViewById(R.id.textUserScore);
        btnBackToUsers = findViewById(R.id.btnBackToUsers);
        cardAdditions = findViewById(R.id.cardAdditions);
        cardMultiplications = findViewById(R.id.cardMultiply);
        cardFrancais = findViewById(R.id.cardFrancais);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1);
        firstName = sharedPreferences.getString("user_first_name", "Inconnu");
        lastName = sharedPreferences.getString("user_last_name", "Utilisateur");
        isAnonymous = sharedPreferences.getBoolean("is_anonymous", false);

        if (isAnonymous || userId == -1) {
            textSelectedUser.setText("Mode Anonyme");
            textUserScore.setText("Score non disponible");
        } else {
            textSelectedUser.setText("Bonjour " + firstName + " " + lastName + "!");
            loadUserScore();
        }

        cardAdditions.setOnClickListener(v -> {
            Intent intent = new Intent(this, CalculActivity.class);
            intent.putExtra("operation", "addition");
            startActivity(intent);
        });

        cardMultiplications.setOnClickListener(v -> {
            Intent intent = new Intent(this, CalculActivity.class);
            intent.putExtra("operation", "multiplication");
            startActivity(intent);
        });

        cardFrancais.setOnClickListener(v -> {
            startActivity(new Intent(this, FrancaisSplashScreenActivity.class));
        });

        btnBackToUsers.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isAnonymous && userId != -1) {
            loadUserScore();
        }
    }

    private void loadUserScore() {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                return DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .userDao()
                        .getUserScore(userId);
            }

            @Override
            protected void onPostExecute(Integer score) {
                textUserScore.setText("Score : " + score);
            }
        }.execute();
    }
}