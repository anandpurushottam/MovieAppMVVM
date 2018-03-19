package com.worldwide.movie.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.worldwide.movie.data.Movie;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.worldwide.movie.util.Precondition.checkNotNull;

/**
 * Created by Anand on 18-03-2018.
 */

public class MovieRepository implements MovieDataSource {

  private volatile static MovieRepository INSTANCE = null;

  private final MovieDataSource mMoviesRemoteDataSource;

  private final MovieDataSource mMoviesLocalDataSource;
  Map<Integer, Movie> mCachedTasks;
  private boolean mCacheIsDirty = false;

  // Prevent direct instantiation.
  private MovieRepository(@NonNull MovieDataSource moviesRemoteDataSource,
      @NonNull MovieDataSource moviesLocalDataSource) {
    mMoviesRemoteDataSource = checkNotNull(moviesRemoteDataSource);
    mMoviesLocalDataSource = checkNotNull(moviesLocalDataSource);
  }

  public static MovieRepository getInstance(MovieDataSource moviesRemoteDataSource,
      MovieDataSource moviesLocalDataSource) {
    if (INSTANCE == null) {
      synchronized (MovieDataSource.class) {
        if (INSTANCE == null) {
          INSTANCE = new MovieRepository(moviesRemoteDataSource, moviesLocalDataSource);
        }
      }
    }
    return INSTANCE;
  }

  @Override public void getMovies(@NonNull final LoadMoviesCallback callback) {
    checkNotNull(callback);

    // Respond immediately with cache if available and not dirty
    if (mCachedTasks != null && !mCacheIsDirty) {
      callback.onMoviesLoaded(new ArrayList<>(mCachedTasks.values()));
      return;
    }

    if (mCacheIsDirty) {
      // If the cache is dirty we need to fetch new data from the network.
      getTasksFromRemoteDataSource(callback);
    } else {
      // Query the local storage if available. If not, query the network.
      mMoviesLocalDataSource.getMovies(new LoadMoviesCallback() {

        @Override public void onMoviesLoaded(List<Movie> movies) {
          refreshCache(movies);
          callback.onMoviesLoaded(new ArrayList<>(mCachedTasks.values()));
        }

        @Override public void onDataNotAvailable() {
          getTasksFromRemoteDataSource(callback);
        }
      });
    }
  }

  @Override
  public void getMovie(@NonNull final String movieID, @NonNull final GetMovieCallback callback) {
    checkNotNull(movieID);
    checkNotNull(callback);

    Movie cachedTask = getMovieWithId(movieID);

    // Respond immediately with cache if available
    if (cachedTask != null) {
      callback.onMovieLoaded(cachedTask);
      return;
    }

    // Load from server/persisted if needed.

    // Is the task in the local data source? If not, query the network.
    mMoviesLocalDataSource.getMovie(movieID, new GetMovieCallback() {

      @Override public void onMovieLoaded(Movie movie) {
        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
          mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(movie.getId(), movie);

        callback.onMovieLoaded(movie);
      }

      @Override public void onDataNotAvailable() {
        mMoviesRemoteDataSource.getMovie(movieID, new GetMovieCallback() {

          @Override public void onMovieLoaded(Movie movie) {
            if (movie == null) {
              onDataNotAvailable();
              return;
            }
            // Do in memory cache update to keep the app UI up to date
            if (mCachedTasks == null) {
              mCachedTasks = new LinkedHashMap<>();
            }
            mCachedTasks.put(movie.getId(), movie);

            callback.onMovieLoaded(movie);
          }

          @Override public void onDataNotAvailable() {
            callback.onDataNotAvailable();
          }
        });
      }
    });
  }

  @Override public void saveMovie(@NonNull Movie movie) {
    checkNotNull(movie);
    mMoviesRemoteDataSource.saveMovie(movie);
    mMoviesLocalDataSource.saveMovie(movie);

    // Do in memory cache update to keep the app UI up to date
    if (mCachedTasks == null) {
      mCachedTasks = new LinkedHashMap<>();
    }
    mCachedTasks.put(movie.getId(), movie);
  }

  @Override public void refreshMovies() {
    mCacheIsDirty = true;
  }

  @Override public void deleteAllMovies() {
    mMoviesRemoteDataSource.deleteAllMovies();
    mMoviesLocalDataSource.deleteAllMovies();

    if (mCachedTasks == null) {
      mCachedTasks = new LinkedHashMap<>();
    }
    mCachedTasks.clear();
  }

  @Override public void deleteMovie(@NonNull String movieID) {
    mMoviesRemoteDataSource.deleteMovie(checkNotNull(movieID));
    mMoviesLocalDataSource.deleteMovie(checkNotNull(movieID));

    mCachedTasks.remove(movieID);
  }

  private void getTasksFromRemoteDataSource(@NonNull final LoadMoviesCallback callback) {
    mMoviesRemoteDataSource.getMovies(new LoadMoviesCallback() {

      @Override public void onMoviesLoaded(List<Movie> movies) {
        refreshCache(movies);
        refreshLocalDataSource(movies);
        callback.onMoviesLoaded(new ArrayList<>(mCachedTasks.values()));
      }

      @Override public void onDataNotAvailable() {
        callback.onDataNotAvailable();
      }
    });
  }

  private void refreshLocalDataSource(List<Movie> movies) {
    mMoviesLocalDataSource.deleteAllMovies();
    for (Movie movie : movies) {
      mMoviesLocalDataSource.saveMovie(movie);
    }
  }

  private void refreshCache(List<Movie> movies) {
    if (mCachedTasks == null) {
      mCachedTasks = new LinkedHashMap<>();
    }
    mCachedTasks.clear();
    for (Movie movie : movies) {
      mCachedTasks.put(movie.getId(), movie);
    }
    mCacheIsDirty = false;
  }

  @Nullable private Movie getMovieWithId(@NonNull String id) {
    checkNotNull(id);
    if (mCachedTasks == null || mCachedTasks.isEmpty()) {
      return null;
    } else {
      return mCachedTasks.get(id);
    }
  }
}
