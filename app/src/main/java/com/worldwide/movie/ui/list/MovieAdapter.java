package com.worldwide.movie.ui.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.worldwide.movie.R;
import com.worldwide.movie.data.Movie;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

import static com.worldwide.movie.util.Config.BaseImageUrl;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

  private List<Movie> mMovies = new ArrayList<>();

  public MovieAdapter(@NonNull List<Movie> movies) {
    mMovies = movies;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    // create a new view
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_movie, parent, false);
    // set the view's size, margins, paddings and layout parameters
    ViewHolder vh = new ViewHolder(v);
    return vh;
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    Movie movie = mMovies.get(position);
    Timber.d("movies: " + BaseImageUrl + movie.getImage());

    Picasso.get()
        .load(BaseImageUrl + movie.getImage())
        .placeholder(R.drawable.placeholder)
        .fit()
        .into(holder.ivPoster);
    holder.tvTitle.setText(movie.getTitle());
  }

  @Override public int getItemCount() {
    return mMovies.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public TextView tvTitle;
    public ImageView ivPoster;

    public ViewHolder(View v) {
      super(v);
      tvTitle = v.findViewById(R.id.tvTitle);
      ivPoster = v.findViewById(R.id.ivPoster);
    }
  }
}