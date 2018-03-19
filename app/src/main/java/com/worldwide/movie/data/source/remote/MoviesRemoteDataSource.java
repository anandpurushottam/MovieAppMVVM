package com.worldwide.movie.data.source.remote;

import android.support.annotation.NonNull;
import com.worldwide.movie.data.Movie;
import com.worldwide.movie.data.source.MovieDataSource;
import com.worldwide.movie.data.source.remote.networking.MoviesService;
import com.worldwide.movie.data.source.remote.networking.NetworkModule;
import com.worldwide.movie.util.AppExecutors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Anand on 18-03-2018.
 */

public class MoviesRemoteDataSource implements MovieDataSource {

  private static volatile MoviesRemoteDataSource INSTANCE;

  private Map<Integer, Movie> movieMap;

  private AppExecutors mAppExecutors;

  // Prevent direct instantiation.
  private MoviesRemoteDataSource() {
    mAppExecutors = new AppExecutors();
    movieMap = new HashMap<>();
  }

  public static MoviesRemoteDataSource getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new MoviesRemoteDataSource();
    }
    return INSTANCE;
  }

  @Override public void getMovies(@NonNull LoadMoviesCallback callback) {
    Runnable runnable = () -> {

      List<Movie> movies = NetworkModule.getInstance().create(MoviesService.class).movieList(1);

      mAppExecutors.mainThread().execute(() -> {
        if (movies.isEmpty()) {
          callback.onDataNotAvailable();
        } else {
          callback.onMoviesLoaded(movies);
        }
      });
    };
    mAppExecutors.networkIO().execute(runnable);
  }

  @Override public void getMovie(@NonNull String movieID, @NonNull GetMovieCallback callback) {
    //TODO Implement

  }

  @Override public void saveMovie(@NonNull Movie movie) {

    movieMap.put(movie.getId(), movie);
  }

  @Override public void refreshMovies() {
    // Not required because the {@link MoviesRepository} handles the logic of refreshing the
    // tasks from all the available data sources.
  }

  @Override public void deleteAllMovies() {
    movieMap.clear();
  }

  @Override public void deleteMovie(@NonNull String movieID) {
    movieMap.remove(movieID);
  }
}
