package fr.iut.androidprojet.additions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.iut.androidprojet.R;
import fr.iut.androidprojet.database.DatabaseClient;
import fr.iut.androidprojet.model.User;

public class AdditionActivity extends AppCompatActivity {
    private User currentUser;
    private boolean isAnonymous = false;
    private int userId = -1;

    private LinearLayout additionContainer;
    private TextView textProgress;
    private TextView btnBack, btnNext;
    private EditText answerField;
    private List<int[]> additions = new ArrayList<>();
    private List<String> userAnswers = new ArrayList<>();
    private int currentIndex = 0;
    private final int totalAdditions = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additions);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        isAnonymous = prefs.getBoolean("is_anonymous", false);

        additionContainer = findViewById(R.id.additionContainer);
        textProgress = findViewById(R.id.textProgress);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnValidate);

        generateAdditions();

        if (!isAnonymous && userId != -1) {
            loadUserFromDatabase(userId);
        }

        btnBack.setOnClickListener(v -> {
            if (currentIndex == 0) {
                finish();
            } else {
                saveCurrentAnswer();
                currentIndex--;
                displayCurrentAddition();
            }
        });

        btnNext.setOnClickListener(v -> {
            saveCurrentAnswer();
            if (currentIndex == totalAdditions - 1) {
                launchCorrectionActivity();
            } else {
                currentIndex++;
                displayCurrentAddition();
            }
        });

        displayCurrentAddition();
    }

    private void generateAdditions() {
        Random random = new Random();
        for (int i = 0; i < totalAdditions; i++) {
            int a = random.nextInt(30) + 1;
            int b = random.nextInt(30) + 1;
            additions.add(new int[]{a, b});
            userAnswers.add("");
        }
    }

    private void displayCurrentAddition() {
        additionContainer.removeAllViews();

        int[] current = additions.get(currentIndex);
        String previousAnswer = userAnswers.get(currentIndex);

        textProgress.setText((currentIndex + 1) + "/" + totalAdditions);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);

        TextView left = new TextView(this);
        left.setText(String.valueOf(current[0]));
        left.setTextSize(24);

        TextView plus = new TextView(this);
        plus.setText(" + ");
        plus.setTextSize(24);

        TextView right = new TextView(this);
        right.setText(String.valueOf(current[1]));
        right.setTextSize(24);

        TextView equals = new TextView(this);
        equals.setText(" = ");
        equals.setTextSize(24);

        answerField = new EditText(this);
        answerField.setText(previousAnswer);
        answerField.setTextSize(24);
        answerField.setEms(3);

        row.addView(left);
        row.addView(plus);
        row.addView(right);
        row.addView(equals);
        row.addView(answerField);

        additionContainer.addView(row);

        btnBack.setText(currentIndex == 0 ? "⏮️ - Retour" : "⬅️ - Précédent");
        btnNext.setText(currentIndex == totalAdditions - 1 ? "Valider - ✅" : "Suivant - ➡️");
    }

    private void saveCurrentAnswer() {
        String answer = answerField.getText().toString().trim();
        userAnswers.set(currentIndex, answer);
    }

    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < totalAdditions; i++) {
            try {
                int expected = additions.get(i)[0] + additions.get(i)[1];
                int userInput = Integer.parseInt(userAnswers.get(i));
                if (userInput == expected) score++;
            } catch (Exception ignored) {}
        }
        return score;
    }

    private void launchCorrectionActivity() {
        int finalScore = calculateScore();

        if (!isAnonymous && currentUser != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    DatabaseClient.getInstance(getApplicationContext())
                            .getAppDatabase()
                            .userDao()
                            .addScoreToUser(currentUser.getId(), finalScore);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    goToCorrection(finalScore);
                }
            }.execute();
        } else {
            // juste naviguer
            goToCorrection(finalScore);
        }
    }

    private void goToCorrection(int finalScore) {
        Intent intent = new Intent(AdditionActivity.this, AdditionCorrectionActivity.class);
        intent.putExtra("score", finalScore);
        intent.putExtra("total", totalAdditions);

        for (int i = 0; i < totalAdditions; i++) {
            intent.putExtra("a" + i, additions.get(i)[0]);
            intent.putExtra("b" + i, additions.get(i)[1]);
            intent.putExtra("answer" + i, userAnswers.get(i));
        }

        startActivity(intent);
        finish();
    }

    private void loadUserFromDatabase(int userId) {
        class GetUserTask extends AsyncTask<Void, Void, User> {
            @Override
            protected User doInBackground(Void... voids) {
                return DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .userDao()
                        .getUserById(userId);
            }

            @Override
            protected void onPostExecute(User user) {
                if (user == null) {
                    Toast.makeText(AdditionActivity.this, "Erreur : Utilisateur introuvable", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    currentUser = user;
                }
            }
        }

        new GetUserTask().execute();
    }
}