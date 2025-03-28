package fr.iut.androidprojet.multiplications;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import fr.iut.androidprojet.R;
import fr.iut.androidprojet.SelectExerciseActivity;

public class MultiplicationCorrectionActivity extends AppCompatActivity {
    private LinearLayout correctionContainer;
    private TextView correctionScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correction_multiplications);

        correctionContainer = findViewById(R.id.multiplicationCorrectionContainer);
        correctionScore = findViewById(R.id.textCorrectionScore);

        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 10);
        correctionScore.setText("Score : " + score + "/" + total);

        for (int i = 0; i < total; i++) {
            int a = getIntent().getIntExtra("a" + i, 0);
            int b = getIntent().getIntExtra("b" + i, 0);
            String answer = getIntent().getStringExtra("answer" + i);

            int expected = a * b;
            boolean correct = false;
            try {
                correct = Integer.parseInt(answer) == expected;
            } catch (Exception ignored) {}

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(0, 16, 0, 16);

            TextView operation = new TextView(this);
            operation.setText(a + " × " + b + " = " + answer);
            operation.setTextSize(18);
            operation.setGravity(android.view.Gravity.CENTER_HORIZONTAL);

            TextView result = new TextView(this);
            result.setTextSize(16);
            result.setText(correct
                    ? "Bonne réponse!"
                    : "Faux ! " + a + " × " + b + " = " + expected);
            result.setTextColor(ContextCompat.getColor(this,
                    correct ? R.color.green : R.color.red));
            result.setGravity(android.view.Gravity.CENTER_HORIZONTAL);

            row.addView(operation);
            row.addView(result);
            correctionContainer.addView(row);
        }

        findViewById(R.id.btnRetry).setOnClickListener(v -> {
            Intent intent = new Intent(this, MultiplicationActivity.class);
            intent.putExtra("user_id", getIntent().getIntExtra("user_id", -1));
            startActivity(intent);
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