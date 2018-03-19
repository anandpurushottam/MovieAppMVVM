package com.worldwide.movie.ui.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.worldwide.movie.R;
import com.worldwide.movie.data.Movie;
import com.worldwide.movie.databinding.MovieListFragBinding;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

import static com.worldwide.movie.util.Precondition.checkNotNull;

/**
 * Created by Anand on 18-03-2018.
 */

public class MovieListFragment extends Fragment {
  private RecyclerView recyclerView;
  private MovieAdapter adapter;
  private MovieListViewModel mViewModel;
  private List<Movie> mMovieList = new ArrayList<>();

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    adapter = new MovieAdapter(mMovieList);
  }

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.movie_list_frag, container, false);
    recyclerView = rootView.findViewById(R.id.recyclerView);
    setupRecyclerView();

    MovieListFragBinding viewDataBinding = MovieListFragBinding.bind(rootView);

    mViewModel = MovieListActivity.obtainViewModel(getActivity());

    viewDataBinding.setViewModel(mViewModel);
    subscribeToItemChange(mViewModel);
    return rootView;
  }

  private void subscribeToItemChange(MovieListViewModel viewModel) {
    int prevIndex = mMovieList.size();
    viewModel.getMovies(false).observe(this, movies -> {
      // update UI
      checkNotNull(movies);
      mMovieList.addAll(movies);
      Timber.d("movies: " + movies.size());
      adapter.notifyItemRangeInserted(prevIndex, movies.size());
    });
  }

  private void setupRecyclerView() {
    GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(adapter);
  }

  public static MovieListFragment newInstance() {
    return new MovieListFragment();
  }
}
