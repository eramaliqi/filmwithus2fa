package com.example.project1;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddMovieActivity extends AppCompatActivity {

    private EditText titleEditText, directorEditText, descriptionEditText, yearEditText, durationEditText, watchedMinutesEditText;
    private Button saveButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        titleEditText = findViewById(R.id.editTextTitle);
        directorEditText = findViewById(R.id.editTextDirector);
        descriptionEditText = findViewById(R.id.editTextDescription);
        yearEditText = findViewById(R.id.editTextYear);
        durationEditText = findViewById(R.id.editTextDuration);
        watchedMinutesEditText = findViewById(R.id.editTextWatchedMinutes);
        saveButton = findViewById(R.id.btnSaveMovie);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        saveButton.setOnClickListener(v -> saveMovie());
    }

    private void saveMovie() {
        String title = titleEditText.getText().toString().trim();
        String director = directorEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String yearText = yearEditText.getText().toString().trim();
        String durationText = durationEditText.getText().toString().trim();
        String watchedMinutesText = watchedMinutesEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(director) || TextUtils.isEmpty(description) ||
                TextUtils.isEmpty(yearText) || TextUtils.isEmpty(durationText) || TextUtils.isEmpty(watchedMinutesText)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int year, duration, watchedMinutes;
        try {
            year = Integer.parseInt(yearText);
            duration = Integer.parseInt(durationText);
            watchedMinutes = Integer.parseInt(watchedMinutesText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for year, duration, and watched minutes", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        Movie newMovie = new Movie(title, director, description, year,duration, watchedMinutes);
        newMovie.setCompleted(false);

        db.collection("movies")
                .add(newMovie.toMap(userId))
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddMovieActivity.this, "Movie added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddMovieActivity.this, "Error adding movie", Toast.LENGTH_SHORT).show();
                });
    }
}
