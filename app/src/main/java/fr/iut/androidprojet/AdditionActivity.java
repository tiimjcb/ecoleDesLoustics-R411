package fr.iut.androidprojet;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.iut.androidprojet.database.DatabaseClient;
import fr.iut.androidprojet.model.User;

public class AdditionActivity extends AppCompatActivity {
    private User currentUser;
    private LinearLayout additionContainer;
    private List<EditText> answerFields;
    private int score = 0; // Score calculé après validation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additions);

        currentUser = (User) getIntent().getSerializableExtra("selectedUser");

        TextView textSelectedUser = findViewById(R.id.textSelectedUser);
        additionContainer = findViewById(R.id.additionContainer);
        answerFields = new ArrayList<>();

        if (currentUser != null) {
            textSelectedUser.setText("Utilisateur sélectionné : " + currentUser.getFirstName() + " " + currentUser.getLastName());
        }

        generateAdditions();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnValidate).setOnClickListener(v -> validateAnswers());
    }

    private void generateAdditions() {
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

            answerFields.add(answerField);

            additionRow.addView(textNum1);
            additionRow.addView(textPlus);
            additionRow.addView(textNum2);
            additionRow.addView(textEquals);
            additionRow.addView(answerField);

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
                    score++;
                }
            } catch (NumberFormatException e) {
                // on ignore si il a rien mis
            }
        }

        //
        if (currentUser != null) {
            updateScoreInDatabase();
        }
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
                // goToNextActivity();
            }
        }

        new UpdateScoreTask().execute();
    }

   /* private void goToNextActivity() {
        Intent intent = new Intent(this, NextActivity.class); // Remplace "NextActivity" par la future activité
        intent.putExtra("selectedUser", currentUser);
        startActivity(intent);
    }*/
}