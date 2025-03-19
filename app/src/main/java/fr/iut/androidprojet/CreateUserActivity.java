package fr.iut.androidprojet;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fr.iut.androidprojet.database.DatabaseClient;
import fr.iut.androidprojet.model.User;

public class CreateUserActivity extends AppCompatActivity {

    private EditText inputFirstName, inputLastName;
    private TextView btnCancel, btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        inputFirstName = findViewById(R.id.inputFirstName);
        inputLastName = findViewById(R.id.inputLastName);
        btnCancel = findViewById(R.id.btnCancel);
        btnConfirm = findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(v -> {
            finish();
        });

        btnConfirm.setOnClickListener(v -> {
            String firstName = inputFirstName.getText().toString().trim();
            String lastName = inputLastName.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(CreateUserActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                addUserToDatabase(firstName, lastName);
            }
        });
    }

    private void addUserToDatabase(String firstName, String lastName) {
        class InsertUserTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                User newUser = new User(firstName, lastName);

                DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .userDao()
                        .insert(newUser);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(CreateUserActivity.this, "Utilisateur ajouté !", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        new InsertUserTask().execute();
    }
}
