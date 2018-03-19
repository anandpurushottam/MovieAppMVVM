package com.worldwide.movie.data.source.local;

import android.support.annotation.NonNull;
import com.worldwide.movie.data.Movie;
import com.worldwide.movie.data.source.MovieDataSource;
import com.worldwide.movie.util.AppExecutors;
import java.util.List;

import static com.worldwide.movie.util.Precondition.checkNotNull;

/**
 * Created by Anand on 18-03-2018.
 */

public class MoviesLocalDataSource implements MovieDataSource {
  private static volatile MoviesLocalDataSource INSTANCE;

  private MoviesDao mMovieDao;

  private AppExecutors mAppExecutors;

  // Prevent direct instantiation.
  private MoviesLocalDataSource(@NonNull AppExecutors appExecutors, @NonNull MoviesDao moviesDao) {
    mAppExecutors = appExecutors;
    mMovieDao = moviesDao;
  }

  public static MoviesLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
      @NonNull MoviesDao moviesDao) {
    if (INSTANCE == null) {
      synchronized (MoviesLocalDataSource.class) {
        if (INSTANCE == null) {
          INSTANCE = new MoviesLocalDataSource(appExecutors, moviesDao);
        }
      }
    }
    return INSTANCE;
  }

  @Override public void getMovies(@NonNull LoadMoviesCallback callback) {
    Runnable runnable = () -> {
      List<Movie> movies = mMovieDao.getMovies();
      mAppExecutors.mainThread().execute(() -> {
        if (movies.isEmpty()) {
          callback.onDataNotAvailable();
        } else {
          callback.onMoviesLoaded(movies);
        }
      });
    };
    mAppExecutors.diskIO().execute(runnable);
  }

  @Override public void getMovie(@NonNull String movieID, @NonNull GetMovieCallback callback) {
    Runnable runnable = () -> {
      Movie movie = mMovieDao.getMovieById(movieID);
      mAppExecutors.mainThread().execute(() -> {
        if (movie != null) {
          callback.onMovieLoaded(movie);
        } else {
          callback.onDataNotAvailable();
        }
      });
    };
    mAppExecutors.diskIO().execute(runnable);
  }

  @Override public void saveMovie(@NonNull Movie movie) {
    checkNotNull(movie);
    Runnable saveRunnable = () -> {
      mMovieDao.insertMovie(movie);
    };
    mAppExecutors.diskIO().execute(saveRunnable);
  }

  @Override public void refreshMovies() {
    // Not required because the {@link MoviesRepository} handles the logic of refreshing the
    // tasks from all the available data sources.
  }

  @Override public void deleteAllMovies() {
    Runnable deleteAllMovies = () -> mMovieDao.deleteMovies();

    mAppExecutors.diskIO().execute(deleteAllMovies);
  }

  @Override public void deleteMovie(@NonNull String movieID) {
    Runnable deleteRunnable = () -> mMovieDao.deleteMovieById(movieID);
    mAppExecutors.diskIO().execute(deleteRunnable);
  }
}
