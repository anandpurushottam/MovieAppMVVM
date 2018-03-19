package com.worldwide.movie.data.source;

import android.support.annotation.NonNull;
import com.worldwide.movie.data.Movie;
import java.util.List;

/**
 * Created by Anand on 18-03-2018.
 */

public interface MovieDataSource {
  interface LoadMoviesCallback {

    void onMoviesLoaded(List<Movie> movies);

    void onDataNotAvailable();
  }

  interface GetMovieCallback {

    void onMovieLoaded(Movie movie);

    void onDataNotAvailable();
  }

  void getMovies(@NonNull LoadMoviesCallback callback);

  void getMovie(@NonNull String movieID, @NonNull GetMovieCallback callback);

  void saveMovie(@NonNull Movie movie);

  void refreshMovies();

  void deleteAllMovies();

  void deleteMovie(@NonNull String movieID);
}
