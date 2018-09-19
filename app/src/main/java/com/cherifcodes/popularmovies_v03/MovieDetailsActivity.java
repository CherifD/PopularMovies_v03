package com.cherifcodes.popularmovies_v03;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cherifcodes.popularmovies_v03.adaptersAndListeners.MovieReviewAdapter;
import com.cherifcodes.popularmovies_v03.adaptersAndListeners.MovieTrailerAdapter;
import com.cherifcodes.popularmovies_v03.adaptersAndListeners.TrailerClickListener;
import com.cherifcodes.popularmovies_v03.model.Movie;
import com.cherifcodes.popularmovies_v03.utils.ImageIO;
import com.cherifcodes.popularmovies_v03.utils.IntentConstants;
import com.cherifcodes.popularmovies_v03.utils.JsonToMovieList;
import com.cherifcodes.popularmovies_v03.utils.LocalImageConstants;
import com.cherifcodes.popularmovies_v03.utils.NetworkUtils;
import com.cherifcodes.popularmovies_v03.viewModels.MovieDetailsViewModel;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
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
    @BindView(R.id.tgb_like_movie)
    ToggleButton toggle;

    private int mMovieId;
    MovieDetailsViewModel mViewModel;
    private Movie mMovie;
    private List<Movie> mFavoriteMovieList = new ArrayList<>();

    private MovieTrailerAdapter mMovieTrailerAdapter;
    private MovieReviewAdapter mMovieReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getBundleExtra(IntentConstants.BUNDLE_KEY);
        mMovie = bundle.getParcelable(IntentConstants.CLICKED_MOVIE_ITEM);

        if (mMovie != null) {
            mOriginalTitleTextView.setText(mMovie.getOriginalTitle());
            mOverviewTextView.setText(mMovie.getOverview());
            mReleaseDateTextView.append(mMovie.getReleaseDate());
            mVoteAverage.append(String.valueOf(mMovie.getVoteAverage()));

            mMovieId = mMovie.getId();
            //Load and display the movie poster using the Picasso library.
            Picasso.with(this)
                    .load(NetworkUtils.POSTER_BASE_URL + mMovie.getPosterString())
                    .error(R.drawable.ic_missing_image_error) //Displays this image if image failed to load
                    .into(mPosterImageView);

            setUpTheRecyclerViews();

            new MovieTrailerAsyncTask().execute(NetworkUtils.DETAIL_VIDEO_TRAILERS);
            new MovieReviewAsyncTask().execute(NetworkUtils.DETAIL_REVIEWS);

            setUpViewModel();

            handleFavoriteMovieEvents();

        } else {
            Log.e(MovieDetailsActivity.class.getSimpleName(), "Null clickedMovie");
        }
    }

    private void setUpTheRecyclerViews() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        movieReviewsRecycleView.setLayoutManager(reviewsLayoutManager);
        movieTrailerRecycleView.setLayoutManager(layoutManager);

        mMovieTrailerAdapter = new MovieTrailerAdapter(this);
        mMovieReviewAdapter = new MovieReviewAdapter();

        movieTrailerRecycleView.setAdapter(mMovieTrailerAdapter);
        movieReviewsRecycleView.setAdapter(mMovieReviewAdapter);
    }

    private void handleFavoriteMovieEvents() {
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast toast = Toast.makeText(MovieDetailsActivity.this,
                        R.string.movie_saved_message, Toast.LENGTH_LONG);
                if (isChecked) { // User wants to save the movie as a favorite
                    Bitmap currPosterImage = ((BitmapDrawable) mPosterImageView.getDrawable())
                            .getBitmap();
                    if (!mFavoriteMovieList.contains(mMovie)) {
                        likeMovie(currPosterImage);
                        toast.show();
                    } else {
                        toast.setText(R.string.movie_already_saved_msg);
                    }
                } else { // User does not like the movie.
                    unlikeMovie();
                    toast.setText(R.string.movie_unliked_message);
                    toast.show();
                }
            }
        });
    }

    private void setUpViewModel() {
        mViewModel = ViewModelProviders.of(this)
                .get(MovieDetailsViewModel.class);
        mViewModel.getLocalMovieList().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                mFavoriteMovieList = movies;

                if (mMovie.isFavoriteMovie(mFavoriteMovieList))
                    toggle.setChecked(true);
            }
        });
    }

    /**
     * Saves the movie to the local database and the specified image to internal storage
     *
     * @param bitmapImage the specified movie poster image
     */
    private void likeMovie(Bitmap bitmapImage) {
        new ImageIO(this)
                .setDirectoryName(LocalImageConstants.DIRECTORY_NAME)
                .setFileName(String.valueOf(mMovieId))
                .save(bitmapImage);

        // save the movie to the local database
        if (mMovie != null) {
            mViewModel.insertMovie(mMovie);
        }
    }

    /**
     * Deletes the movie from the local database and its poster image from internal storage
     */
    private void unlikeMovie() {
        new ImageIO(this)
                .setDirectoryName(LocalImageConstants.DIRECTORY_NAME)
                .setFileName(String.valueOf(mMovieId))
                .deleteFile();

        //Delete the movie from local database
        if (mMovie != null) {
            mViewModel.deleteMovie(mMovie);
        }
    }

    /**
     * Plays the Youtube movie trailer located at a specified Youtube video link
     *
     * @param trailerLink the specified Youtube video link
     */
    @Override
    public void OnTrailerClicked(String trailerLink) {
        Uri webpage = Uri.parse(NetworkUtils.YOUTUBE_BASE_URL + trailerLink);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Loads the movie's reviews
     */
    private class MovieReviewAsyncTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... strings) {

            URL reviewsUrl = NetworkUtils.buildMovieDetailUrlFromId(mMovieId,
                    NetworkUtils.DETAIL_REVIEWS);
            String reviewsJson = NetworkUtils.getJsonResponse(reviewsUrl);
            return JsonToMovieList.getMovieDetailListFromJson(reviewsJson,
                    JsonToMovieList.REVIEWS_DETAIL_KEY);
        }

        @Override
        protected void onPostExecute(List<String> reviewsList) {
            if (reviewsList != null) {
                mMovieReviewAdapter.setReviewList(reviewsList);
            }
        }
    }

    /**
     * Loads the movie's trailer video links
     */
    private class MovieTrailerAsyncTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... strings) {
            //Fetch and display the movie trailers into their RecyclerView
            URL trailerUrl = NetworkUtils.buildMovieDetailUrlFromId(mMovieId,
                    NetworkUtils.DETAIL_VIDEO_TRAILERS);
            String trailerJson = NetworkUtils.getJsonResponse(trailerUrl);
            return JsonToMovieList.getMovieDetailListFromJson(trailerJson,
                    JsonToMovieList.VIDEO_TRAILER_DETAIL_KEY);
        }

        @Override
        protected void onPostExecute(List<String> trailerList) {
            if (trailerList != null) {
                mMovieTrailerAdapter.setMovieTrailerList(trailerList);
            }
        }
    }
}
