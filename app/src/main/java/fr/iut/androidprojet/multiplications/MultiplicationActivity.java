package fr.iut.androidprojet.multiplications;

import android.content.Intent;
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

public class MultiplicationActivity extends AppCompatActivity {
    private User currentUser;
    private LinearLayout multiplicationContainer;
    private TextView textProgress, btnBack, btnNext;
    private EditText answerField;
    private List<int[]> multiplications = new ArrayList<>();
    private List<String> userAnswers = new ArrayList<>();
    private int currentIndex = 0;
    private final int total = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplications);

        int userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Erreur : Aucun utilisateur sélectionné", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        multiplicationContainer = findViewById(R.id.multiplicationContainer);
        textProgress = findViewById(R.id.textProgress);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnValidate);

        generateMultiplications();
        loadUserFromDatabase(userId);

        btnBack.setOnClickListener(v -> {
            if (currentIndex == 0) {
                finish();
            } else {
                saveCurrentAnswer();
                currentIndex--;
                displayCurrentMultiplication();
            }
        });

        btnNext.setOnClickListener(v -> {
            saveCurrentAnswer();
            if (currentIndex == total - 1) {
                launchCorrection();
            } else {
                currentIndex++;
                displayCurrentMultiplication();
            }
        });

        displayCurrentMultiplication();
    }

    private void generateMultiplications() {
        Random random = new Random();
        for (int i = 0; i < total; i++) {
            int a = random.nextInt(10) + 1;
            int b = random.nextInt(10) + 1;
            multiplications.add(new int[]{a, b});
            userAnswers.add("");
        }
    }

    private void displayCurrentMultiplication() {
        multiplicationContainer.removeAllViews();
        int[] current = multiplications.get(currentIndex);
        String previousAnswer = userAnswers.get(currentIndex);
        textProgress.setText((currentIndex + 1) + "/" + total);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);

        TextView left = new TextView(this);
        left.setText(String.valueOf(current[0]));
        left.setTextSize(24);

        TextView sign = new TextView(this);
        sign.setText(" × ");
        sign.setTextSize(24);

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
        row.addView(sign);
        row.addView(right);
        row.addView(equals);
        row.addView(answerField);
        multiplicationContainer.addView(row);

        btnBack.setText(currentIndex == 0 ? "⏮️ - Retour" : "⬅️ - Précédent");
        btnNext.setText(currentIndex == total - 1 ? "Valider - ✅" : "Suivant - ➡️");
    }

    private void saveCurrentAnswer() {
        String answer = answerField.getText().toString().trim();
        userAnswers.set(currentIndex, answer);
    }

    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < total; i++) {
            try {
                int expected = multiplications.get(i)[0] * multiplications.get(i)[1];
                int input = Integer.parseInt(userAnswers.get(i));
                if (input == expected) score++;
            } catch (Exception ignored) {}
        }
        return score;
    }

    private void launchCorrection() {
        int finalScore = calculateScore();
        class UpdateTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .userDao().addScoreToUser(currentUser.getId(), finalScore);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Intent intent = new Intent(MultiplicationActivity.this, MultiplicationCorrectionActivity.class);
                intent.putExtra("user_id", currentUser.getId());
                intent.putExtra("score", finalScore);
                intent.putExtra("total", total);
                for (int i = 0; i < total; i++) {
                    intent.putExtra("a" + i, multiplications.get(i)[0]);
                    intent.putExtra("b" + i, multiplications.get(i)[1]);
                    intent.putExtra("answer" + i, userAnswers.get(i));
                }
                startActivity(intent);
                finish();
            }
        }
        new UpdateTask().execute();
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
                    Toast.makeText(MultiplicationActivity.this, "Utilisateur introuvable", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    currentUser = user;
                }
            }
        }
        new GetUserTask().execute();
    }

}