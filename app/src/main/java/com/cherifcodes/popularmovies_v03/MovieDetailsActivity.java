package com.cherifcodes.popularmovies_v03;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.cherifcodes.popularmovies_v03.UI.MovieReviewAdapter;
import com.cherifcodes.popularmovies_v03.UI.MovieTrailerAdapter;
import com.cherifcodes.popularmovies_v03.UI.TrailerClickListener;
import com.cherifcodes.popularmovies_v03.Utils.IntentConstants;
import com.cherifcodes.popularmovies_v03.Utils.JsonToMovieList;
import com.cherifcodes.popularmovies_v03.Utils.NetworkUtils;
import com.cherifcodes.popularmovies_v03.model.Movie;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity implements TrailerClickListener {

    @BindView(R.id.tv_details_movie_title) TextView mOriginalTitleTextView;
    @BindView(R.id.imv_details_movie_poster) ImageView mPosterImageView;
    @BindView(R.id.tv_details_movie_release_date) TextView mReleaseDateTextView;
    @BindView(R.id.tv_details_vote_average) TextView mVoteAverage;
    @BindView(R.id.tv_details_movie_overview) TextView mOverviewTextView;
    @BindView(R.id.rv_movie_trailer)
    RecyclerView movieTrailerRecycleView;
    @BindView(R.id.rv_movie_review)
    RecyclerView movieReviewsRecycleView;

    private int mMovieId;
    private MovieTrailerAdapter mMovieTrailerAdapter;
    private MovieReviewAdapter mMovieReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getBundleExtra(IntentConstants.BUNDLE_KEY);
        Movie clickedMovie = bundle.getParcelable(IntentConstants.CLICKED_MOVIE_ITEM);

        if (clickedMovie != null) {
            mOriginalTitleTextView.setText(clickedMovie.getOriginalTitle());
            mOverviewTextView.setText(clickedMovie.getOverview());
            mReleaseDateTextView.append(clickedMovie.getReleaseDate());
            mVoteAverage.append(String.valueOf(clickedMovie.getVoteAverage()));

            mMovieId = clickedMovie.getId();
            //Load and display the movie poster using the Picasso library.
            Picasso.with(this)
                    .load(NetworkUtils.POSTER_BASE_URL + clickedMovie.getPosterString())
                    .error(R.drawable.ic_missing_image_error) //Displays this image if image failed to load
                    .into(mPosterImageView);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            RecyclerView.LayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
            movieReviewsRecycleView.setLayoutManager(reviewsLayoutManager);
            movieTrailerRecycleView.setLayoutManager(layoutManager);

            mMovieTrailerAdapter = new MovieTrailerAdapter(this);
            mMovieReviewAdapter = new MovieReviewAdapter();

            movieTrailerRecycleView.setAdapter(mMovieTrailerAdapter);
            movieReviewsRecycleView.setAdapter(mMovieReviewAdapter);

            new MovieTrailerAsynTask().execute(NetworkUtils.DETAIL_VIDEO_TRAILERS);
            new MovieReviewAsyncTask().execute(NetworkUtils.DETAIL_REVIEWS);

        } else {
            Log.e(MovieDetailsActivity.class.getSimpleName(), "Null clickedMovie");
        }
    }

    @Override
    public void OnTrailerClicked(String trailerLink) {
        Uri webpage = Uri.parse(NetworkUtils.YOUTUBE_BASE_URL + trailerLink);

        Log.i(getClass().getSimpleName(), webpage.toString());

        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private class MovieReviewAsyncTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... strings) {

            URL reviewsUrl = NetworkUtils.buildMovieDetailUrlFromId(mMovieId,
                    NetworkUtils.DETAIL_REVIEWS);
            String reviewsJson = NetworkUtils.getJsonResponse(reviewsUrl);
            List<String> reviewsList = JsonToMovieList.getMovieDetailListFromJson(reviewsJson,
                    JsonToMovieList.REVIEWS_DETAIL_KEY);
            return reviewsList;
        }

        @Override
        protected void onPostExecute(List<String> reviewsList) {
            if (reviewsList != null) {
                mMovieReviewAdapter.setReviewList(reviewsList);
            }
        }
    }

    private class MovieTrailerAsynTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... strings) {
            //Fetch and display the movie trailers into their RecyclerView
            URL trailerUrl = NetworkUtils.buildMovieDetailUrlFromId(mMovieId,
                    NetworkUtils.DETAIL_VIDEO_TRAILERS);
            String trailerJson = NetworkUtils.getJsonResponse(trailerUrl);
            List<String> trailerList = JsonToMovieList.getMovieDetailListFromJson(trailerJson,
                    JsonToMovieList.VIDEO_TRAILER_DETAIL_KEY);
            return trailerList;
        }

        @Override
        protected void onPostExecute(List<String> trailerList) {
            if (trailerList != null) {
                mMovieTrailerAdapter.setMovieTrailerList(trailerList);
            }
        }
    }
}
