package com.cherifcodes.popularmovies_v03.UI;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cherifcodes.popularmovies_v03.R;

import java.util.ArrayList;
import java.util.List;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ReviewViewHolder> {

    private List<String> mReviewList;


    public MovieReviewAdapter() {
        mReviewList = new ArrayList<>();
    }

    public void setReviewList(List<String> reviewList) {
        this.mReviewList = reviewList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View listItem = inflater.inflate(R.layout.movie_review_list_item, parent, false);
        return new ReviewViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewAdapter.ReviewViewHolder holder, int position) {
        holder.movieReviewTextView.setText(mReviewList.get(position));
    }

    @Override
    public int getItemCount() {
        return mReviewList == null ? 0 : mReviewList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView movieReviewTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            movieReviewTextView = itemView.findViewById(R.id.tv_movie_review);
        }
    }
}
