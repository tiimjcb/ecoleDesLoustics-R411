package fr.iut.androidprojet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import fr.iut.androidprojet.additions.AdditionActivity;
import fr.iut.androidprojet.database.DatabaseClient;
import fr.iut.androidprojet.multiplications.MultiplicationActivity;
import fr.iut.androidprojet.quizzFrancais.FrancaisSplashScreenActivity;

public class SelectExerciseActivity extends AppCompatActivity {

    private TextView textSelectedUser, textUserScore, btnBackToUsers;
    private String firstName, lastName;
    private int userId;
    private LinearLayout cardAdditions, cardMultiplications, cardFrancais;

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
        firstName = sharedPreferences.getString("user_first_name", "Inconnu");
        lastName = sharedPreferences.getString("user_last_name", "Utilisateur");
        userId = sharedPreferences.getInt("user_id", -1);

        textSelectedUser.setText("Bonjour " + firstName + " " + lastName +"!");

        loadUserScore();

        cardAdditions.setOnClickListener(v -> {
            Intent intent = new Intent(SelectExerciseActivity.this, AdditionActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        cardMultiplications.setOnClickListener(v -> {
            Intent intent = new Intent(SelectExerciseActivity.this, MultiplicationActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        cardFrancais.setOnClickListener(v -> {
            Intent intent = new Intent(SelectExerciseActivity.this, FrancaisSplashScreenActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        btnBackToUsers.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserScore();
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