package fr.iut.androidprojet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import fr.iut.androidprojet.adapter.UserAdapter;
import fr.iut.androidprojet.data.QuestionSamples;
import fr.iut.androidprojet.database.AppDatabase;
import fr.iut.androidprojet.database.DatabaseClient;
import fr.iut.androidprojet.model.User;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_USER = 1;
    private RecyclerView recyclerViewUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        LinearLayout btnAddUser = findViewById(R.id.btnAddUser);
        btnAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateUserActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_USER); // faut attendre le résultat pour recharger la liste
        });

        loadUsers();
        populateQuestionsIfNeeded();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_USER && resultCode == RESULT_OK) {
            loadUsers();
        }
    }

    private void loadUsers() {
        class GetUsers extends AsyncTask<Void, Void, List<User>> {
            @Override
            protected List<User> doInBackground(Void... voids) {
                return DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .userDao()
                        .getAllUsers();
            }

            @Override
            protected void onPostExecute(List<User> users) {
                super.onPostExecute(users);
                UserAdapter adapter = new UserAdapter(users,
                        user -> {
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("user_id", user.getId());

                            editor.putString("user_first_name", user.getFirstName());
                            editor.putString("user_last_name", user.getLastName());
                            editor.apply();

                            Intent intent = new Intent(MainActivity.this, SelectExerciseActivity.class);
                            startActivity(intent);
                        },
                        user -> {
                            showDeleteConfirmation(user);
                }
                );
                recyclerViewUsers.setAdapter(adapter);
            }
        }
        new GetUsers().execute();
    }

    private void showDeleteConfirmation(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer utilisateur")
                .setMessage("Voulez-vous vraiment supprimer " + user.getFirstName() + " ?")
                .setPositiveButton("Oui", (dialog, which) -> deleteUserFromDatabase(user))
                .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void deleteUserFromDatabase(User user) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .userDao()
                        .deleteUser(user);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                loadUsers();
                Toast.makeText(MainActivity.this, user.getFirstName() + " supprimé", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    // nécéssaire pour remplir la base de données avec des questions si jamais y'a rien
    private void populateQuestionsIfNeeded() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                AppDatabase db = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase();
                if (db.questionFrDao().countQuestions() == 0) {
                    db.questionFrDao().insertAll(QuestionSamples.getAll());
                }
                return null;
            }
        }.execute();
    }
}