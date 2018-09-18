package com.cherifcodes.popularmovies_v03.adaptersAndListeners;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cherifcodes.popularmovies_v03.R;
import com.cherifcodes.popularmovies_v03.model.Movie;
import com.cherifcodes.popularmovies_v03.utils.ImageIO;
import com.cherifcodes.popularmovies_v03.utils.LocalImageConstants;
import com.cherifcodes.popularmovies_v03.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> mMovieList;
    private boolean mIsLocalList;
    private MovieClickListener mMovieClickListener;

    public MovieAdapter(MovieClickListener movieClickListener) {
        this.mMovieClickListener = movieClickListener;
        mMovieList = new ArrayList<>();
    }

    public void setMovieList(List<Movie> movieList, boolean isLocalList) {
        this.mMovieList = movieList;
        this.mIsLocalList = isLocalList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate (initialize) the list item
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View listItem = inflater.inflate(R.layout.movie_list_item, parent, false);

        //Create and return a ViewHolder passing in the listItem
        return new MovieViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        //Get the movie for the current position
        Movie currMovie = mMovieList.get(position);
        Context context = (Context)mMovieClickListener;

        //If the movieList is from the local database, retrieve the image locally, otherwise
        //use Picasso to retrieve the image
        if (mIsLocalList) {
            Bitmap currBitmap = new ImageIO(context)
                    .setDirectoryName(LocalImageConstants.DIRECTORY_NAME)
                    .setFileName(String.valueOf(currMovie.getId()))
                    .load();

            holder.mMoviePosterImageView.setImageBitmap(currBitmap);
        } else {
            Picasso.with(context)
                    .load(NetworkUtils.POSTER_BASE_URL + currMovie.getPosterString())
                    .error(R.drawable.ic_missing_image_error) //Displays this image if image failed to load
                    .into(holder.mMoviePosterImageView);
        }

        //Display the title
        holder.mMovieTitleTextView.setText(currMovie.getOriginalTitle());
    }

    @Override
    public int getItemCount() {
        return mMovieList == null? 0 : mMovieList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mMovieTitleTextView;
        private ImageView mMoviePosterImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            mMovieTitleTextView = itemView.findViewById(R.id.tv_movie_title);
            mMoviePosterImageView = itemView.findViewById(R.id.imv_movie_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mMovieClickListener.onMovieClicked(mMovieList.get(getAdapterPosition()));
        }
    }
}
