package fr.iut.androidprojet.additions;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import fr.iut.androidprojet.R;
import fr.iut.androidprojet.SelectExerciseActivity;

public class AdditionCorrectionActivity extends AppCompatActivity {
    private LinearLayout correctionContainer;
    private TextView correctionScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correction_additions);

        correctionContainer = findViewById(R.id.additionCorrectionContainer);
        correctionScore = findViewById(R.id.textCorrectionScore);

        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 10);
        correctionScore.setText("Score : " + score + "/" + total);

        for (int i = 0; i < total; i++) {
            int a = getIntent().getIntExtra("a" + i, 0);
            int b = getIntent().getIntExtra("b" + i, 0);
            String answer = getIntent().getStringExtra("answer" + i);

            int expected = a + b;
            boolean isCorrect = false;
            try {
                isCorrect = Integer.parseInt(answer) == expected;
            } catch (Exception ignored) {}

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(0, 16, 0, 16);

            TextView operation = new TextView(this);
            operation.setText(a + " + " + b + " = " + answer);
            operation.setTextSize(18);

            TextView result = new TextView(this);
            result.setTextSize(16);
            result.setText(isCorrect
                    ? "Bonne réponse!"
                    : "Faux ! " + a + " + " + b + " = " + expected);
            result.setTextColor(ContextCompat.getColor(this,
                    isCorrect ? R.color.green : R.color.red));

            operation.setGravity(Gravity.CENTER_HORIZONTAL);
            result.setGravity(Gravity.CENTER_HORIZONTAL);

            row.addView(operation);
            row.addView(result);

            correctionContainer.addView(row);
        }

        findViewById(R.id.btnRetry).setOnClickListener(v -> {
            startActivity(new Intent(this, AdditionActivity.class));
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