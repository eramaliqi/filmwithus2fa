package com.example.project1;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private List<Movie> completedMovies;
    private FirebaseFirestore db;
    private TextView userEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerViewCompletedMovies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userEmailTextView = findViewById(R.id.textViewUserEmail);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            userEmailTextView.setText("Email: " + userEmail);
        } else {
            userEmailTextView.setText("Email: Not available");
        }

        completedMovies = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, completedMovies, true);
        recyclerView.setAdapter(movieAdapter);

        loadCompletedMovies();
    }

    private void loadCompletedMovies() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("movies")
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("completed", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        completedMovies.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String director = document.getString("director");
                            String description = document.getString("description");
                            int year = document.contains("year") ? document.getLong("year").intValue() : 0;
                            int duration = document.contains("duration") ? document.getLong("duration").intValue() : 0;
                            int watchedMinutes = document.contains("watchedMinutes") ? document.getLong("watchedMinutes").intValue() : 0;
                            float rating = document.contains("rating") ? document.getDouble("rating").floatValue() : 0.0f;

                            Movie movie = new Movie(title, director, description, year, duration, watchedMinutes);
                            movie.setRating(rating);
                            movie.setCompleted(true);
                            movie.setDocumentId(document.getId());

                            completedMovies.add(movie);
                        }
                        movieAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(Profile.this, "Failed to load completed movies.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(Profile.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
