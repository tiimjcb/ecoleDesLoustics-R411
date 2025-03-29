package fr.iut.androidprojet.calcul;

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

public class CalculActivity extends AppCompatActivity {

    private User currentUser;
    private boolean isAnonymous = false;
    private int userId = -1;

    private String mode = "addition";

    private LinearLayout containerCalcul;
    private TextView textTitreCalcul, textProgress, btnBack, btnNext;
    private EditText answerField;

    private final int total = 10;
    private int currentIndex = 0;

    private final List<int[]> calculs = new ArrayList<>();
    private final List<String> userAnswers = new ArrayList<>();
    private final List<String> operationModes = new ArrayList<>();

    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calcul_activity);

        mode = getIntent().getStringExtra("operation");
        if (mode == null) mode = "addition";

        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        isAnonymous = prefs.getBoolean("is_anonymous", false);

        textTitreCalcul = findViewById(R.id.textTitreCalcul);
        textProgress = findViewById(R.id.textProgress);
        containerCalcul = findViewById(R.id.containerCalcul);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnValidate);

        textTitreCalcul.setText("Réponds aux " + getFriendlyModeLabel());

        generateCalculs();

        if (!isAnonymous && userId != -1) {
            loadUserFromDatabase(userId);
        }

        btnBack.setOnClickListener(v -> {
            if (currentIndex == 0) {
                finish();
            } else {
                saveCurrentAnswer();
                currentIndex--;
                displayCurrentCalcul();
            }
        });

        btnNext.setOnClickListener(v -> {
            saveCurrentAnswer();
            if (currentIndex == total - 1) {
                launchCorrection();
            } else {
                currentIndex++;
                displayCurrentCalcul();
            }
        });

        displayCurrentCalcul();
    }

    private void generateCalculs() {
        for (int i = 0; i < total; i++) {
            int a, b;
            String currentMode = mode;

            if (mode.equals("mix")) {
                String[] allModes = {"addition", "multiplication", "soustraction", "division"};
                currentMode = allModes[random.nextInt(allModes.length)];
            }

            switch (currentMode) {
                case "multiplication":
                    a = random.nextInt(10) + 1;
                    b = random.nextInt(10) + 1;
                    break;
                case "soustraction":
                    a = random.nextInt(30) + 10;
                    b = random.nextInt(a);
                    break;
                case "division":
                    b = random.nextInt(9) + 2;
                    a = b * (random.nextInt(10) + 1);
                    break;
                default: // addition
                    a = random.nextInt(30) + 1;
                    b = random.nextInt(30) + 1;
            }

            calculs.add(new int[]{a, b});
            userAnswers.add("");
            operationModes.add(currentMode);
        }
    }

    private void displayCurrentCalcul() {
        containerCalcul.removeAllViews();

        int[] current = calculs.get(currentIndex);
        String previousAnswer = userAnswers.get(currentIndex);

        textProgress.setText((currentIndex + 1) + "/" + total);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);

        TextView left = new TextView(this);
        left.setText(String.valueOf(current[0]));
        left.setTextSize(24);

        TextView op = new TextView(this);
        op.setText(" " + getOperatorSymbol(currentIndex) + " ");
        op.setTextSize(24);

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
        row.addView(op);
        row.addView(right);
        row.addView(equals);
        row.addView(answerField);

        containerCalcul.addView(row);

        btnBack.setText(currentIndex == 0 ? "⏮️ - Retour" : "⬅️ - Précédent");
        btnNext.setText(currentIndex == total - 1 ? "Valider - ✅" : "Suivant - ➡️");
    }

    private void saveCurrentAnswer() {
        String answer = answerField.getText().toString().trim();
        userAnswers.set(currentIndex, answer);
    }

    private int getExpectedResult(int a, int b, int index) {
        String m = mode.equals("mix") ? operationModes.get(index) : mode;

        switch (m) {
            case "multiplication": return a * b;
            case "soustraction": return a - b;
            case "division": return a / b;
            default: return a + b;
        }
    }

    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < total; i++) {
            try {
                int expected = getExpectedResult(calculs.get(i)[0], calculs.get(i)[1], i);
                int input = Integer.parseInt(userAnswers.get(i));
                if (input == expected) score++;
            } catch (Exception ignored) {}
        }
        return score;
    }

    private void launchCorrection() {
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
            goToCorrection(finalScore);
        }
    }

    private void goToCorrection(int finalScore) {
        Intent intent = new Intent(this, CorrectionCalculActivity.class);
        intent.putExtra("score", finalScore);
        intent.putExtra("total", total);
        intent.putExtra("mode", mode);
        for (int i = 0; i < total; i++) {
            intent.putExtra("a" + i, calculs.get(i)[0]);
            intent.putExtra("b" + i, calculs.get(i)[1]);
            intent.putExtra("answer" + i, userAnswers.get(i));
            if (mode.equals("mix")) {
                intent.putExtra("op" + i, operationModes.get(i));
            }
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
                    Toast.makeText(CalculActivity.this, "Utilisateur introuvable", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    currentUser = user;
                }
            }
        }
        new GetUserTask().execute();
    }

    private String getOperatorSymbol(int index) {
        String m = mode.equals("mix") ? operationModes.get(index) : mode;

        switch (m) {
            case "multiplication": return "×";
            case "soustraction": return "−";
            case "division": return "÷";
            default: return "+";
        }
    }

    private String getFriendlyModeLabel() {
        switch (mode) {
            case "addition": return "additions";
            case "multiplication": return "multiplications";
            case "soustraction": return "soustractions";
            case "division": return "divisions";
            case "mix": return "calculs";
            default: return "exercices";
        }
    }
}