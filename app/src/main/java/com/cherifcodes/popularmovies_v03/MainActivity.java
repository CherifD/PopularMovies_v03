package com.cherifcodes.popularmovies_v03;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.cherifcodes.popularmovies_v03.adaptersAndListeners.MovieAdapter;
import com.cherifcodes.popularmovies_v03.adaptersAndListeners.MovieClickListener;
import com.cherifcodes.popularmovies_v03.model.Movie;
import com.cherifcodes.popularmovies_v03.utils.IntentConstants;
import com.cherifcodes.popularmovies_v03.utils.JsonToMovieList;
import com.cherifcodes.popularmovies_v03.utils.NetworkUtils;
import com.cherifcodes.popularmovies_v03.viewModels.MainActivityViewModel;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieClickListener {

    @BindView(R.id.rv_movie_list) RecyclerView mMovieListRecyclerView;
    private static final int NUM_RECYCLER_VIEW_COLUMNS = 2;
    private MovieAdapter mMovieAdapter;

    String mSortMovieListBy = NetworkUtils.BASE_URL_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mMovieAdapter = new MovieAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this,
                NUM_RECYCLER_VIEW_COLUMNS);
        mMovieListRecyclerView.setLayoutManager(layoutManager);
        mMovieListRecyclerView.setAdapter(mMovieAdapter);

        if (this.isConnectedToTheInternet()) { // There is an internet connection
            // Start the AsyncTask to fetch the data in parallel
            loadRemoteMovieList();
        } else { // There is no internet connection, show a toast message.
            Toast.makeText(this, R.string.no_internet_error_message,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Determines if the device is connected to the internet
     *
     * @return true if there is a network connection, false otherwise
     */
    private boolean isConnectedToTheInternet() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.sorting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.item_popularity_sort) {
            mSortMovieListBy = NetworkUtils.BASE_URL_POPULAR;
            loadRemoteMovieList();
        } else if (itemId == R.id.item_top_rated_sort) {
            mSortMovieListBy = NetworkUtils.BASE_URL_TOP_RATED;
            loadRemoteMovieList();
        } else if (itemId == R.id.item_favorites) {
            loadFavoriteMovieList();
        } else {
            //This condition is unlikely to occur, but I handle it just for completeness.
            Toast.makeText(this, R.string.unknown_sort_message, Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFavoriteMovieList() {
        MainActivityViewModel viewModel = ViewModelProviders.of(this).get(
                MainActivityViewModel.class);
        viewModel.getMovieList().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                mMovieAdapter.setMovieList(movies, true);
            }
        });
    }

    private void loadRemoteMovieList() {
        new MovieAsyncTast().execute(mSortMovieListBy);
        MainActivityViewModel viewModel = ViewModelProviders.of(this)
                .get(MainActivityViewModel.class);
        viewModel.getMovieList().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {

            }
        });
    }

    @Override
    public void onMovieClicked(Movie clickedMovie) {

        if (this.isConnectedToTheInternet()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(IntentConstants.CLICKED_MOVIE_ITEM, clickedMovie);
            Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
            intent.putExtra(IntentConstants.BUNDLE_KEY, bundle);
            this.startActivity(intent);
        } else {
            Toast.makeText(this, R.string.no_internet_error_message,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Loads movies from the remote database in a non-UI thread.
     */
    private class MovieAsyncTast extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... strings) {
            //Build the URL based on the given sorting string
            URL url = NetworkUtils.buildUrl(strings[0]);
            //Get the JSON response string
            String jsonResponse = NetworkUtils.getJsonResponse(url);
            //Return the new MovieList
            return JsonToMovieList.parseMovieListJson(jsonResponse);
        }

        @Override
        protected void onPostExecute(List<Movie> currMovieList) {
            if (currMovieList != null) {
                mMovieAdapter.setMovieList(currMovieList, false);
            }
        }
    }
}
