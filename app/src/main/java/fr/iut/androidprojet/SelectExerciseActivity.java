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

public class SelectExerciseActivity extends AppCompatActivity {

    private TextView textSelectedUser, textUserScore, btnBackToUsers;
    private String firstName, lastName;
    private int userId;
    private LinearLayout cardAdditions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_exercise);

        textSelectedUser = findViewById(R.id.textSelectedUser);
        textUserScore = findViewById(R.id.textUserScore);
        btnBackToUsers = findViewById(R.id.btnBackToUsers);
        cardAdditions = findViewById(R.id.cardAdditions); // Récupération de la carte des additions

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        firstName = sharedPreferences.getString("user_first_name", "Inconnu");
        lastName = sharedPreferences.getString("user_last_name", "Utilisateur");
        userId = sharedPreferences.getInt("user_id", -1); // On récupère aussi l'ID du user

        textSelectedUser.setText("Utilisateur sélectionné : " + firstName + " " + lastName);

        loadUserScore();

        // Gestion du clic sur la carte des additions
        cardAdditions.setOnClickListener(v -> {
            Intent intent = new Intent(SelectExerciseActivity.this, AdditionActivity.class);
            startActivity(intent);
        });

        btnBackToUsers.setOnClickListener(v -> finish());
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