package com.cherifcodes.popularmovies_v03;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.cherifcodes.popularmovies_v03.UI.MovieAdapter;
import com.cherifcodes.popularmovies_v03.UI.MovieClickListener;
import com.cherifcodes.popularmovies_v03.Utils.IntentConstants;
import com.cherifcodes.popularmovies_v03.Utils.JsonToMovieList;
import com.cherifcodes.popularmovies_v03.Utils.NetworkUtils;
import com.cherifcodes.popularmovies_v03.model.Movie;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieClickListener {

    @BindView(R.id.rv_movie_list) RecyclerView mMovieListRecyclerView;
    private static final int NUM_RECYCLER_VIEW_COLUMNS = 2;
    private MovieAdapter mMovieAdapter;

    String mSortMovieListBy = NetworkUtils.SORT_BY_POPULARITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mMovieAdapter = new MovieAdapter(this);
        getMovieList();
        GridLayoutManager layoutManager = new GridLayoutManager(this,
                NUM_RECYCLER_VIEW_COLUMNS);
        mMovieListRecyclerView.setLayoutManager(layoutManager);
        mMovieListRecyclerView.setAdapter(mMovieAdapter);
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
            mSortMovieListBy = NetworkUtils.SORT_BY_POPULARITY;
            getMovieList();
        } else if (itemId == R.id.item_release_date_sort) {
            mSortMovieListBy = NetworkUtils.SORT_BY_RELEASE_DATE;
            getMovieList();
        } else if (itemId == R.id.item_vote_average_sort) {
            mSortMovieListBy = NetworkUtils.SORT_BY_VOTE_AVERAGE;
            getMovieList();
        } else {
            //This condition is unlikely to occur, but I handle it just for completeness.
            Toast.makeText(this, R.string.unknown_sort_message, Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMovieList() {
        new MovieAsyncTast().execute(mSortMovieListBy);
    }

    @Override
    public void onMovieClicked(Movie clickedMovie) {
        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
        intent.putExtra(IntentConstants.CLICKED_MOVIE_ITEM, clickedMovie);
        this.startActivity(intent);
    }

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
                mMovieAdapter.setMovieList(currMovieList);
            }
        }
    }
}
