package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class Home extends AppCompatActivity {

    private TextView emptyMessage;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private List<Movie> movieList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        emptyMessage = findViewById(R.id.emptyMessage);
        recyclerView = findViewById(R.id.recyclerViewMovies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movieList, false);
        recyclerView.setAdapter(movieAdapter);

        db = FirebaseFirestore.getInstance();
        fetchMovies();

        Button btnAddMovie = findViewById(R.id.btnAddMovie);
        btnAddMovie.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, AddMovieActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchMovies();
    }

    private void fetchMovies() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("movies")
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        movieList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String director = document.getString("director");
                            String description = document.getString("description");
                            int year = document.contains("year") ? document.getLong("year").intValue() : 0;
                            int watchedMinutes = document.contains("watchedMinutes") ? document.getLong("watchedMinutes").intValue() : 0;
                            int duration = document.contains("duration") ? document.getLong("duration").intValue() : 0;
                            boolean completed = document.contains("completed") ? document.getBoolean("completed") : false;
                            // <-- Edited line here: use lowercase "rating"
                            float rating = document.contains("rating") ? document.getDouble("rating").floatValue() : 0f;

                            Movie movie = new Movie(title, director, description, year, duration, watchedMinutes);
                            movie.setDocumentId(document.getId());
                            movie.setCompleted(completed);
                            movie.setDuration(duration);
                            movie.setRating(rating);

                            movieList.add(movie);
                        }

                        movieAdapter.notifyDataSetChanged();
                        if (movieList.isEmpty()) {
                            emptyMessage.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            emptyMessage.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(Home.this, "Failed to load movies.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Home.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
