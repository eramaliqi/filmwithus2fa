package com.example.project1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movieList;
    private boolean isProfileView;

    public MovieAdapter(Context context, List<Movie> movieList, boolean isProfileView) {
        this.context = context;
        this.movieList = movieList;
        this.isProfileView = isProfileView;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        holder.titleTextView.setText("Movie: " + movie.getTitle());
        holder.directorTextView.setText("Directed by: " + movie.getDirector());
        holder.descriptionTextView.setText("Description: " + movie.getDescription());
        holder.yearTextView.setText("Year: " + movie.getYear());
        holder.durationTextView.setText("Duration: " + movie.getDuration() + " mins");
        holder.watchedMinutesTextView.setText("Watched: " + movie.getWatchedMinutes() + " mins");

        if (movie.isCompleted()) {
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.ratingBar.setRating(movie.getRating());

            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
            holder.completedButton.setVisibility(View.GONE);
        } else {
            if (isProfileView) {
                holder.editButton.setVisibility(View.GONE);
                holder.deleteButton.setVisibility(View.GONE);
                holder.completedButton.setVisibility(View.GONE);
                holder.ratingBar.setVisibility(View.GONE);
            } else {
                holder.editButton.setVisibility(View.VISIBLE);
                holder.deleteButton.setVisibility(View.VISIBLE);
                holder.completedButton.setVisibility(View.VISIBLE);
                holder.ratingBar.setVisibility(View.GONE);
            }
        }

        holder.completedButton.setOnClickListener(v -> openRatingDialog(movie, position));

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditMovieActivity.class);
            intent.putExtra("documentId", movie.getDocumentId());
            intent.putExtra("title", movie.getTitle());
            intent.putExtra("director", movie.getDirector());
            intent.putExtra("description", movie.getDescription());
            intent.putExtra("year", movie.getYear());
            intent.putExtra("duration", movie.getDuration());
            intent.putExtra("watchedMinutes", movie.getWatchedMinutes());
            context.startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Movie")
                    .setMessage("Are you sure you want to delete this movie?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteMovie(movie, position))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void openRatingDialog(Movie movie, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rate_movie, null);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);

        new AlertDialog.Builder(context)
                .setTitle("Rate Movie")
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    float rating = ratingBar.getRating();
                    if (rating > 0) {
                        saveRatingAndComplete(movie, rating, position);
                    } else {
                        Toast.makeText(context, "Please provide a rating.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveRatingAndComplete(Movie movie, float rating, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        movie.setCompleted(true);
        movie.setRating(rating);

        db.collection("movies").document(movie.getDocumentId())
                .update("completed", true, "rating", rating)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Movie marked as completed!", Toast.LENGTH_SHORT).show();
                    movieList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to mark as completed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void deleteMovie(Movie movie, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("movies").document(movie.getDocumentId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Movie deleted successfully!", Toast.LENGTH_SHORT).show();
                    movieList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete movie: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, directorTextView, descriptionTextView, yearTextView, durationTextView, watchedMinutesTextView;
        RatingBar ratingBar;
        Button completedButton, editButton, deleteButton;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.movie_title);
            directorTextView = itemView.findViewById(R.id.movie_director);
            descriptionTextView = itemView.findViewById(R.id.movie_description);
            yearTextView = itemView.findViewById(R.id.movie_year);
            durationTextView = itemView.findViewById(R.id.movie_duration);
            watchedMinutesTextView = itemView.findViewById(R.id.movie_watchedMinutes);
            ratingBar = itemView.findViewById(R.id.movie_ratingBar);
            completedButton = itemView.findViewById(R.id.btn_completed);
            editButton = itemView.findViewById(R.id.btn_edit);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }
    }
}
