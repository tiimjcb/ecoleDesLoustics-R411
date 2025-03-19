package fr.iut.androidprojet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.iut.androidprojet.database.DatabaseClient;
import fr.iut.androidprojet.model.User;

public class AdditionActivity extends AppCompatActivity {
    private User currentUser;
    private LinearLayout additionContainer;
    private List<EditText> answerFields;
    private List<TextView> correctionTexts;
    private TextView scoreSummary;
    private TextView btnAction;
    private int score = 0;
    private boolean isValidationMode = true; // logique de switch pour le bouton valider/recommencer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additions);

        int userId = getIntent().getIntExtra("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Erreur : Aucun utilisateur sélectionné", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        additionContainer = findViewById(R.id.additionContainer);
        scoreSummary = findViewById(R.id.scoreSummary);
        btnAction = findViewById(R.id.btnValidate);

        answerFields = new ArrayList<>();
        correctionTexts = new ArrayList<>();

        loadUserFromDatabase(userId);

        generateAdditions();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnAction.setOnClickListener(v -> {
            if (isValidationMode) {
                validateAnswers();
                btnAction.setText("Recommencer");
            } else {
                restartGame();
                btnAction.setText("Valider");
            }
            isValidationMode = !isValidationMode;
        });
    }

    private void generateAdditions() {
        additionContainer.removeAllViews();
        answerFields.clear();
        correctionTexts.clear();
        score = 0;

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int num1 = random.nextInt(30) + 1;
            int num2 = random.nextInt(30) + 1;

            LinearLayout additionRow = new LinearLayout(this);
            additionRow.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16);
            additionRow.setLayoutParams(params);

            TextView textNum1 = new TextView(this);
            textNum1.setText(String.valueOf(num1));
            textNum1.setTextSize(18);

            TextView textPlus = new TextView(this);
            textPlus.setText(" + ");
            textPlus.setTextSize(18);

            TextView textNum2 = new TextView(this);
            textNum2.setText(String.valueOf(num2));
            textNum2.setTextSize(18);

            TextView textEquals = new TextView(this);
            textEquals.setText(" = ");
            textEquals.setTextSize(18);

            EditText answerField = new EditText(this);
            answerField.setHint("?");
            answerField.setTextSize(18);
            answerField.setEms(3);

            TextView correctionText = new TextView(this);
            correctionText.setTextSize(16);
            correctionText.setPadding(16, 0, 0, 0);

            answerFields.add(answerField);
            correctionTexts.add(correctionText);

            additionRow.addView(textNum1);
            additionRow.addView(textPlus);
            additionRow.addView(textNum2);
            additionRow.addView(textEquals);
            additionRow.addView(answerField);
            additionRow.addView(correctionText);

            additionContainer.addView(additionRow);
        }
    }

    private void validateAnswers() {
        score = 0;

        for (int i = 0; i < 10; i++) {
            try {
                int userAnswer = Integer.parseInt(answerFields.get(i).getText().toString());
                LinearLayout row = (LinearLayout) additionContainer.getChildAt(i);
                int num1 = Integer.parseInt(((TextView) row.getChildAt(0)).getText().toString());
                int num2 = Integer.parseInt(((TextView) row.getChildAt(2)).getText().toString());

                if (userAnswer == (num1 + num2)) {
                    correctionTexts.get(i).setText("Correct !");
                    correctionTexts.get(i).setTextColor(ContextCompat.getColor(this, R.color.green));
                    score++;
                } else {
                    correctionTexts.get(i).setText("Faux! - Réponse attendue : " + (num1 + num2));
                    correctionTexts.get(i).setTextColor(ContextCompat.getColor(this, R.color.red));
                }
            } catch (NumberFormatException e) {
                correctionTexts.get(i).setText("Faux! - Réponse attendue : ?");
                correctionTexts.get(i).setTextColor(ContextCompat.getColor(this, R.color.red));
            }
        }

        scoreSummary.setText(score + "/10 - " + score + " points ajoutés au score !");
        updateScoreInDatabase();
    }

    private void updateScoreInDatabase() {
        class UpdateScoreTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .userDao().updateUserScore(currentUser.getId(), score);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(AdditionActivity.this, "Score ajouté : " + score, Toast.LENGTH_SHORT).show();
            }
        }

        new UpdateScoreTask().execute();
    }

    private void restartGame() {
        generateAdditions();
        scoreSummary.setText("");
        for (EditText field : answerFields) {
            field.setText("");
        }
        for (TextView correction : correctionTexts) {
            correction.setText("");
        }
    }

    private void loadUserFromDatabase(int userId) {
        class GetUserTask extends AsyncTask<Void, Void, User> {
            @Override
            protected User doInBackground(Void... voids) {
                return DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .userDao().getUserById(userId);
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