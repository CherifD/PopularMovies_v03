package com.cherifcodes.popularmovies_v03.UI;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cherifcodes.popularmovies_v03.R;

import java.util.ArrayList;
import java.util.List;

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter
        .MovieTrailerViewHolder> {

    private List<String> movieTrailerList;

    public MovieTrailerAdapter() {
        this.movieTrailerList = new ArrayList<>();
    }

    public void setMovieTrailerList(List<String> trailerList) {
        this.movieTrailerList = trailerList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieTrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View listItem = inflater.inflate(R.layout.movie_trailer_list_item, parent, false);

        return new MovieTrailerViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieTrailerViewHolder holder, int position) {

        String trailerLink = movieTrailerList.get(position);
        holder.trailerTitleTextView.setText(trailerLink + ": " + position);

    }

    @Override
    public int getItemCount() {
        return movieTrailerList == null ? 0 : movieTrailerList.size();
    }


    public class MovieTrailerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ImageButton playButtonImageButton;
        private TextView trailerTitleTextView;

        public MovieTrailerViewHolder(View itemView) {
            super(itemView);
            playButtonImageButton = itemView.findViewById(R.id.imv_play_button);
            trailerTitleTextView = itemView.findViewById(R.id.tv_trailer_title);
        }

        @Override
        public void onClick(View v) {

        }
    }

}
