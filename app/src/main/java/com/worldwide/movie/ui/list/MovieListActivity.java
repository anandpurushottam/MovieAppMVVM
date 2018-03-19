package com.worldwide.movie.ui.list;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import com.worldwide.movie.R;
import com.worldwide.movie.ViewModelFactory;
import com.worldwide.movie.util.ActivityUtils;
import timber.log.Timber;

/**
 * Created by Anand on 18-03-2018.
 */

public class MovieListActivity extends AppCompatActivity {
  private DrawerLayout mDrawerLayout;
  private MovieListViewModel mViewModel;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.list_activity);

    setupToolbar();

    setupNavigationDrawer();

    setupViewFragment();

    mViewModel = obtainViewModel(this);
  }

  private void setupViewFragment() {
    MovieListFragment movieListFragment =
        (MovieListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
    if (movieListFragment == null) {
      // Create the fragment
      movieListFragment = MovieListFragment.newInstance();
      ActivityUtils.replaceFragmentInActivity(getSupportFragmentManager(), movieListFragment,
          R.id.contentFrame);
    }
  }

  private void setupNavigationDrawer() {
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    if (navigationView != null) {
      setupDrawerContent(navigationView);
    }
  }

  private void setupDrawerContent(NavigationView navigationView) {
    navigationView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
              case R.id.action_first:
                // Do nothing, we're already on that screen
                break;
              case R.id.action_second:
                //Intent intent = new Intent(TasksActivity.this, StatisticsActivity.class);
                //startActivity(intent);
                break;
              default:
                break;
            }
            // Close the navigation drawer when an item is selected.
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            return true;
          }
        });
  }

  private void setupToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar ab = getSupportActionBar();
    ab.setHomeAsUpIndicator(R.drawable.ic_menu);
    ab.setDisplayHomeAsUpEnabled(true);
  }

  public static MovieListViewModel obtainViewModel(FragmentActivity activity) {
    // Use a Factory to inject dependencies into the ViewModel
    ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

    MovieListViewModel viewModel =
        ViewModelProviders.of(activity, factory).get(MovieListViewModel.class);

    return viewModel;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // Open the navigation drawer when the home icon is selected from the toolbar.
        mDrawerLayout.openDrawer(GravityCompat.START);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
