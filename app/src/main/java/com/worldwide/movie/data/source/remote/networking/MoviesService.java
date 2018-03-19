package com.worldwide.movie.data.source.remote.networking;

import com.worldwide.movie.data.Movie;
import com.worldwide.movie.data.MovieList;
import com.worldwide.movie.data.VideoList;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Anand on 09-03-2018.
 */

public interface MoviesService {
  @GET("movie/popular") List<Movie> movieList(@Query("page") Integer page);

  @GET("movie/{movie_id}/videos") List<VideoList> videoList(@Path("movie_id") Integer movieId);
}