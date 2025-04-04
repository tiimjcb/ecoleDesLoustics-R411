package fr.iut.androidprojet.quizzFrancais;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import fr.iut.androidprojet.R;
import fr.iut.androidprojet.SelectExerciseActivity;
import fr.iut.androidprojet.database.DatabaseClient;
import fr.iut.androidprojet.model.User;

public class FrancaisSplashScreenActivity extends AppCompatActivity {

    private User currentUser;
    private boolean isAnonymous = false;
    private int userId = -1;

    private TextView btnStart, btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_fr_splashscreen);

        // Récupération depuis les prefs
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        isAnonymous = prefs.getBoolean("is_anonymous", false);

        btnStart = findViewById(R.id.btnStart);
        btnReturn = findViewById(R.id.btnReturn);

        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(this, FrancaisQuizActivity.class);
            startActivity(intent);
            finish();
        });

        btnReturn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectExerciseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        if (!isAnonymous && userId != -1) {
            loadUserFromDatabase(userId);
        }
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
                    Toast.makeText(FrancaisSplashScreenActivity.this, "Utilisateur introuvable", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    currentUser = user;
                }
            }
        }
        new GetUserTask().execute();
    }
}