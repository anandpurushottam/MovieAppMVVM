package com.worldwide.movie.data.source.remote;

import android.support.annotation.NonNull;
import com.worldwide.movie.data.Movie;
import com.worldwide.movie.data.MovieList;
import com.worldwide.movie.data.source.MovieDataSource;
import com.worldwide.movie.data.source.remote.networking.MoviesService;
import com.worldwide.movie.data.source.remote.networking.NetworkModule;
import com.worldwide.movie.util.AppExecutors;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java.util.Timer;
import timber.log.Timber;

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

    NetworkModule.getInstance()
        .create(MoviesService.class)
        .movieList(1)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new Observer<MovieList>() {
          @Override public void onSubscribe(Disposable d) {

          }

          @Override public void onNext(MovieList movieList) {
            if (movieList.getMovies().isEmpty()) {
              callback.onDataNotAvailable();
            } else {
              callback.onMoviesLoaded(movieList.getMovies());
            }
          }

          @Override public void onError(Throwable e) {
            callback.onDataNotAvailable();
          }

          @Override public void onComplete() {

          }
        });
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
