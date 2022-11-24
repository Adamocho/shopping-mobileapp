package com.adamocho.firstsemesterfinalproject;

import static com.adamocho.firstsemesterfinalproject.MainActivity.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText usernameET;
    EditText passwordET;
    Button loginBtn;
    Button registerBtn;
    FeedReaderContract dbHelper;
    SQLiteDatabase db;
    final int minChars = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameET = findViewById(R.id.username_input);
        passwordET = findViewById(R.id.password_input);
        loginBtn = findViewById(R.id.login_btn);
        registerBtn = findViewById(R.id.register_btn);

        dbHelper = new FeedReaderContract(this);

        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);

        loginBtn.setOnClickListener(view -> {
//            Log.i(TAG, usernameET.getText().toString() + " " + passwordET.getText().toString());
            String username = usernameET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();

            if (username.length() >= minChars && password.length() >= minChars)
            {
                if (areCridentialsCorrect(username, password))
                {
                    Log.i(TAG, "Correct login cridentials");
                    Toast.makeText(this, getResources().getString(R.string.welcome), Toast.LENGTH_SHORT).show();

                    mainIntent.putExtra("username", username);
                    startActivity(mainIntent);
                } else {
                    Log.i(TAG, "Wrong login credentials");
                    Toast.makeText(this, "Wrong credentials!", Toast.LENGTH_SHORT).show();
                    usernameET.setError(getResources().getString(R.string.invalid_username));
                    passwordET.setError(getResources().getString(R.string.invalid_password));
                }
            } else {
                usernameET.setError(getResources().getString(R.string.invalid_username_len));
                passwordET.setError(getResources().getString(R.string.invalid_password_len));
            }
        });

        registerBtn.setOnClickListener(view -> {
            String username = usernameET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();

            if (username.length() >= minChars && password.length() >= minChars) {
                db = dbHelper.getReadableDatabase();

                String[] projection = { "COUNT(*)" };
                String selection = FeedReaderContract.FeedEntry.COLUMN_USERNAME + " = ?";
                String[] selectionArgs = { username };

                Cursor cursor = db.query(
                        FeedReaderContract.FeedEntry.USER_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

                int sum = 0;
                while(cursor.moveToNext()) {
                    sum += cursor.getInt(0);
                }
                cursor.close();
                db.close();

                // If user already exists
                if (sum > 0) {
                    Toast.makeText(this, getResources().getString(R.string.user_exists), Toast.LENGTH_SHORT).show();
                    usernameET.setError("This user exists");
                } else {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(FeedReaderContract.FeedEntry.COLUMN_USERNAME, username);
                    values.put(FeedReaderContract.FeedEntry.COLUMN_PASSWORD, password);
                    db.insert(FeedReaderContract.FeedEntry.USER_TABLE, null, values);
                    db.close();

                    Toast.makeText(this, getResources().getString(R.string.user_new), Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Created new user: " + username);

                    mainIntent.putExtra("username", username);
                    startActivity(mainIntent);
                }
            } else {
                usernameET.setError(getResources().getString(R.string.invalid_username_len));
                passwordET.setError(getResources().getString(R.string.invalid_password_len));
            }
        });
    }

    public boolean areCridentialsCorrect(String username, String password) {
        db = dbHelper.getReadableDatabase();

        String[] projection = { "COUNT(*)" };
        String selection =
                FeedReaderContract.FeedEntry.COLUMN_USERNAME + " = ? AND "
                + FeedReaderContract.FeedEntry.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = { username, password };

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.USER_TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int sum = 0;
        while(cursor.moveToNext()) {
            sum += cursor.getInt(0);
        }
        cursor.close();
        db.close();

        Log.i(TAG, "areCridentialsCorrect: " + sum);

        return sum == 1;
    }
}