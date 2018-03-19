package com.worldwide.movie;

import android.content.Context;
import com.worldwide.movie.data.source.MovieRepository;
import com.worldwide.movie.data.source.local.MovieDatabase;
import com.worldwide.movie.data.source.local.MoviesLocalDataSource;
import com.worldwide.movie.data.source.remote.MoviesRemoteDataSource;
import com.worldwide.movie.util.AppExecutors;

import static com.worldwide.movie.util.Precondition.checkNotNull;

/**
 * Created by Anand on 18-03-2018.
 */

public class Injection {

  public static MovieRepository provideTasksRepository(Context context) {

    checkNotNull(context);
    MovieDatabase database = MovieDatabase.getInstance(context);
    return MovieRepository.getInstance(MoviesRemoteDataSource.getInstance(),
        MoviesLocalDataSource.getInstance(new AppExecutors(), database.moviesDao()));
  }
}
