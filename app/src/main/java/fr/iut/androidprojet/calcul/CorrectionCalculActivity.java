package fr.iut.androidprojet.calcul;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import fr.iut.androidprojet.R;
import fr.iut.androidprojet.SelectExerciseActivity;
import fr.iut.androidprojet.calcul.CalculActivity;

public class CorrectionCalculActivity extends AppCompatActivity {

    private LinearLayout correctionContainer;
    private TextView correctionScore, textTitreCorrection;
    private String operationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.correction_calcul_activity);

        correctionContainer = findViewById(R.id.correctionContainer);
        correctionScore = findViewById(R.id.textCorrectionScore);
        textTitreCorrection = findViewById(R.id.textTitreCorrection);

        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 10);
        operationType = getIntent().getStringExtra("mode");

        if (operationType == null) operationType = "calcul";

        // Titre dynamique
        String titre = "Correction - " + operationType.substring(0, 1).toUpperCase() + operationType.substring(1);
        textTitreCorrection.setText(titre);

        // Score
        correctionScore.setText("Score : " + score + "/" + total);

        // Affichage des questions et réponses
        for (int i = 0; i < total; i++) {
            int a = getIntent().getIntExtra("a" + i, 0);
            int b = getIntent().getIntExtra("b" + i, 0);
            String answer = getIntent().getStringExtra("answer" + i);

            int expected = 0;
            String symbol = "?";

            switch (operationType) {
                case "addition":
                    expected = a + b;
                    symbol = " + ";
                    break;
                case "multiplication":
                    expected = a * b;
                    symbol = " × ";
                    break;
                case "soustraction":
                    expected = a - b;
                    symbol = " - ";
                    break;
                case "division":
                    expected = (b != 0) ? a / b : 0;
                    symbol = " ÷ ";
                    break;
            }

            boolean isCorrect = false;
            try {
                isCorrect = Integer.parseInt(answer) == expected;
            } catch (Exception ignored) {}

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(0, 16, 0, 16);

            TextView operation = new TextView(this);
            operation.setText(a + symbol + b + " = " + answer);
            operation.setTextSize(18);
            operation.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView result = new TextView(this);
            result.setTextSize(16);
            result.setText(isCorrect
                    ? "Bonne réponse!"
                    : "Faux ! " + a + symbol + b + " = " + expected);
            result.setTextColor(ContextCompat.getColor(this,
                    isCorrect ? R.color.green : R.color.red));
            result.setGravity(Gravity.CENTER_HORIZONTAL);

            row.addView(operation);
            row.addView(result);
            correctionContainer.addView(row);
        }

        findViewById(R.id.btnRetry).setOnClickListener(v -> {
            Intent retryIntent = new Intent();
            switch (operationType) {
                case "addition":
                    retryIntent.setClass(this, CalculActivity.class);
                    retryIntent.putExtra("operation", "addition");
                    break;
                case "multiplication":
                    retryIntent.setClass(this, CalculActivity.class);
                    retryIntent.putExtra("operation", "multiplication");
                    break;
                case "soustraction":
                    retryIntent.setClass(this, CalculActivity.class);
                    retryIntent.putExtra("operation", "soustraction");
                    break;
                case "division":
                    retryIntent.setClass(this, CalculActivity.class);
                    retryIntent.putExtra("operation", "division");
                    break;
                default:
                    retryIntent.setClass(this, SelectExerciseActivity.class);
            }

            startActivity(retryIntent);
            finish();
        });

        findViewById(R.id.btnBackToExercises).setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectExerciseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}