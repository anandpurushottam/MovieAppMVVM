package com.worldwide.movie.data.source;

import com.worldwide.movie.data.Movie;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

/**
 * Created by Kanthesh on 19/03/18.
 */
public class MovieRepositoryTest {

  private MovieRepository mMovieRepository;

  @Mock
  private MovieDataSource mMoviesRemoteDataSource;

  @Mock
  private MovieDataSource mMoviesLocalDataSource;

  @Mock
  private MovieDataSource.GetMovieCallback mGetMoviesCallback;

  @Mock
  private MovieDataSource.LoadMoviesCallback mLoadMoviesCallback;

  @Captor
  private ArgumentCaptor<MovieDataSource.LoadMoviesCallback> mMoviesCallbackCaptor;

  @Captor
  private ArgumentCaptor<MovieDataSource.GetMovieCallback> mMovieCallbackCaptor;

  private List<Movie> MOVIE;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    mMovieRepository = MovieRepository.getInstance(mMoviesRemoteDataSource, mMoviesLocalDataSource);
    MOVIE = new ArrayList<>();
    MOVIE.add(new Movie(1, "Movie1", "somelink1"));
    MOVIE.add(new Movie(2, "Movie2", "somelink2"));
    MOVIE.add(new Movie(3, "Movie3", "somelink3"));
    MOVIE.add(new Movie(4, "Movie4", "somelink4"));
  }

  @Test
  public void loadAllMoviesFromLocalDataStore() {

    mMovieRepository.getMovies(mLoadMoviesCallback);

    verify(mMoviesLocalDataSource).getMovies(any(MovieDataSource.LoadMoviesCallback.class));
  }

  @Test
  public void getTasks_repositoryCachesAfterFirstApiCall() {
    // Given a setup Captor to capture callbacks
    // When two calls are issued to the tasks repository
    twoLoadCallsToRepository(mLoadMoviesCallback);

    // Then tasks were only requested once from Service API
    verify(mMoviesRemoteDataSource).getMovies(any(MovieDataSource.LoadMoviesCallback.class));

  }

  private void twoLoadCallsToRepository(
      MovieDataSource.LoadMoviesCallback callback) {

    //First call to repository
    mMovieRepository.getMovies(callback);

    // Local database queried
    verify(mMoviesLocalDataSource).getMovies(mMoviesCallbackCaptor.capture());

    // Local database doesn't have value
    mMoviesCallbackCaptor.getValue().onDataNotAvailable();

    // Remote database queried
    verify(mMoviesRemoteDataSource).getMovies(mMoviesCallbackCaptor.capture());
    mMoviesCallbackCaptor.getValue().onMoviesLoaded(MOVIE);

    //Second call to repository
    mMovieRepository.getMovies(callback);
  }
}