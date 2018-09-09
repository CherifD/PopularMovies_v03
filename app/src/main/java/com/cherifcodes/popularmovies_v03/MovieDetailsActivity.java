package com.cherifcodes.popularmovies_v03;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cherifcodes.popularmovies_v03.Utils.IntentConstants;
import com.cherifcodes.popularmovies_v03.Utils.NetworkUtils;
import com.cherifcodes.popularmovies_v03.model.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {

    @BindView(R.id.tv_details_movie_title) TextView mOriginalTitleTextView;
    @BindView(R.id.imv_details_movie_poster) ImageView mPosterImageView;
    @BindView(R.id.tv_details_movie_release_date) TextView mReleaseDateTextView;
    @BindView(R.id.tv_details_vote_average) TextView mVoteAverage;
    @BindView(R.id.tv_details_movie_overview) TextView mOverviewTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        Bundle extra = getIntent().getExtras();
        Movie clickedMovie = extra.getParcelable(IntentConstants.CLICKED_MOVIE_ITEM);

        if (clickedMovie != null) {
            mOriginalTitleTextView.setText(clickedMovie.getOriginalTitle());
            mOverviewTextView.setText(clickedMovie.getOverview());
            mReleaseDateTextView.append(clickedMovie.getReleaseDate());
            mVoteAverage.append(String.valueOf(clickedMovie.getVoteAverage()));

            //Load and display the movie poster using the Picasso library.
            Picasso.with(this)
                    .load(NetworkUtils.POSTER_BASE_URL + clickedMovie.getPosterString())
                    .error(R.drawable.ic_missing_image_error) //Displays this image if image failed to load
                    .into(mPosterImageView);
        } else {
            Log.e(MovieDetailsActivity.class.getSimpleName(), "Null clickedMovie");
        }
    }
}
