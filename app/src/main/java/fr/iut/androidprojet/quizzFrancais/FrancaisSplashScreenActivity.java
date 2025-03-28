package fr.iut.androidprojet.quizzFrancais;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import fr.iut.androidprojet.R;
import fr.iut.androidprojet.SelectExerciseActivity;

public class FrancaisSplashScreenActivity extends AppCompatActivity {

    private TextView btnStart, btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_fr_splashscreen);

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
    }
}