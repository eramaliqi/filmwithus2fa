package com.example.project1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

class EditMovieActivity extends AppCompatActivity {

    private EditText titleEditText, directorEditText, descriptionEditText, yearEditText, durationEditText, watchedMinutesEditText;
    private Button saveButton;
    private FirebaseFirestore db;
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movie);

        titleEditText = findViewById(R.id.editTextTitle);
        directorEditText = findViewById(R.id.editTextDirector);
        descriptionEditText = findViewById(R.id.editTextDescription);
        yearEditText = findViewById(R.id.editTextYear);
        durationEditText = findViewById(R.id.editTextDuration);
        watchedMinutesEditText = findViewById(R.id.editTextWatchedMinutes);
        saveButton = findViewById(R.id.btnSaveMovie);

        db = FirebaseFirestore.getInstance();

        documentId = getIntent().getStringExtra("documentId");
        titleEditText.setText(getIntent().getStringExtra("title"));
        directorEditText.setText(getIntent().getStringExtra("director"));
        descriptionEditText.setText(getIntent().getStringExtra("description"));
        yearEditText.setText(String.valueOf(getIntent().getIntExtra("year", 0)));
        durationEditText.setText(String.valueOf(getIntent().getIntExtra("duration", 0)));
        watchedMinutesEditText.setText(String.valueOf(getIntent().getIntExtra("watchedMinutes", 0)));

        saveButton.setOnClickListener(v -> updateMovie());
    }

    private void updateMovie() {
        String title = titleEditText.getText().toString();
        String director = directorEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        int year = Integer.parseInt(yearEditText.getText().toString());
        int duration = Integer.parseInt(durationEditText.getText().toString());
        int watchedMinutes = Integer.parseInt(watchedMinutesEditText.getText().toString());

        db.collection("movies").document(documentId)
                .update("Title", title,
                        "Director", director,
                        "Description", description,
                        "Year", year,
                        "Duration", duration,
                        "WatchedMinutes", watchedMinutes)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditMovieActivity.this, "Movie updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditMovieActivity.this, "Error updating movie: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
