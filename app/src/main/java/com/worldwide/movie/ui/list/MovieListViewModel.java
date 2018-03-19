package com.worldwide.movie.ui.list;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import com.worldwide.movie.data.Movie;
import com.worldwide.movie.data.source.MovieDataSource;
import com.worldwide.movie.data.source.MovieRepository;
import java.util.List;
import timber.log.Timber;

/**
 * Created by Anand on 18-03-2018.
 */

public class MovieListViewModel extends AndroidViewModel {

  private final Context mContext;
  private final MovieRepository mMoviesRepository;

  private MutableLiveData<List<Movie>> movieList;
  public final ObservableBoolean dataLoading = new ObservableBoolean(false);
  public final ObservableBoolean error = new ObservableBoolean(false);

  public MovieListViewModel(@NonNull Application context, MovieRepository movieRepository) {
    super(context);
    mContext = context;
    mMoviesRepository = movieRepository;
  }

  public void start() {
    loadMovies(false);
  }

  private void updateDataBindingObservables(List<Movie> movies) {
    movieList.setValue(movies);
    dataLoading.set(false);
  }

  public LiveData<List<Movie>> getMovies(boolean forceUpdate) {

    if (movieList == null) {
      movieList = new MutableLiveData<>();
      loadMovies(forceUpdate);
    }
    return movieList;
  }

  private void loadMovies(boolean forceUpdate) {
    loadMovies(forceUpdate, true);
  }

  private void loadMovies(boolean forceUpdate, boolean showLoadingUI) {
    if (showLoadingUI) {
      dataLoading.set(true);
    }
    if (forceUpdate) {
      mMoviesRepository.refreshMovies();
    }
    mMoviesRepository.getMovies(new MovieDataSource.LoadMoviesCallback() {
      @Override public void onMoviesLoaded(List<Movie> movies) {
        updateDataBindingObservables(movies);
      }

      @Override public void onDataNotAvailable() {
        error.set(true);
      }
    });
  }
}
