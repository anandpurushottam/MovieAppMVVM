package com.worldwide.movie.ui.list;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import android.content.res.Resources;

import com.worldwide.movie.data.Movie;
import com.worldwide.movie.data.source.MovieDataSource;
import com.worldwide.movie.data.source.MovieRepository;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Kanthesh on 19/03/18.
 */

public class MovieListViewModelTest {

  @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
  @Rule
  public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

  private List<Movie> MOVIE;

  @Mock
  private MovieRepository mMovieRepository;

  @Mock
  private Application mContext;
  MovieListViewModel movieListViewModel;

  @Captor
  private ArgumentCaptor<MovieDataSource.LoadMoviesCallback> mLoadMoviesCallbackCaptor;

  @Before
  public void setUpViewModel() {
    MockitoAnnotations.initMocks(this);
    setupContext();
    movieListViewModel = new MovieListViewModel(mContext, mMovieRepository);
    MOVIE = new ArrayList<>();
    MOVIE.add(new Movie(1, "Movie1", "somelink1"));
    MOVIE.add(new Movie(2, "Movie2", "somelink2"));
    MOVIE.add(new Movie(3, "Movie3", "somelink3"));
    MOVIE.add(new Movie(4, "Movie4", "somelink4"));
  }

  private void setupContext() {
    when(mContext.getApplicationContext()).thenReturn(mContext);
  }

  @Test
  public void loadAllMovies() {

    movieListViewModel.getMovies(false);
    verify(mMovieRepository).getMovies(mLoadMoviesCallbackCaptor.capture());

    // Then progress indicator is shown
    assertTrue(movieListViewModel.dataLoading.get());
    mLoadMoviesCallbackCaptor.getValue().onMoviesLoaded(MOVIE);

    // Then progress indicator is hidden
    assertFalse(movieListViewModel.dataLoading.get());

    // And data loaded
    assertFalse(movieListViewModel.movieList.getValue().isEmpty());

    assertTrue(movieListViewModel.movieList.getValue().size() == 4);
  }

  @Test
  public void loadingFailedMovies() {

    movieListViewModel.getMovies(false);
    verify(mMovieRepository).getMovies(mLoadMoviesCallbackCaptor.capture());

    // Then progress indicator is shown
    assertTrue(movieListViewModel.dataLoading.get());
    mLoadMoviesCallbackCaptor.getValue().onMoviesLoaded(Collections.emptyList());

    // Then progress indicator is hidden
    assertFalse(movieListViewModel.dataLoading.get());

    // And data loaded
    assertTrue(movieListViewModel.movieList.getValue().isEmpty());

    // Check list is empty
    assertFalse(movieListViewModel.movieList.getValue().size() == 4);

    // Error screen showed
    assertTrue(movieListViewModel.error.get());
  }
}