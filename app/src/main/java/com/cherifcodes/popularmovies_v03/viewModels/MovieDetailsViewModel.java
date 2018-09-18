package com.cherifcodes.popularmovies_v03.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.cherifcodes.popularmovies_v03.model.Movie;
import com.cherifcodes.popularmovies_v03.model.Repository;

import java.util.List;

public class MovieDetailsViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> mLocalMovieList;
    private Repository mRepository;

    public MovieDetailsViewModel(@NonNull Application application) {
        super(application);
        mRepository = Repository.getInstance(application);
        mLocalMovieList = mRepository.loadAllMovies();
    }

    public LiveData<List<Movie>> getLocalMovieList() {
        return mLocalMovieList;
    }

    public void updateMovie(Movie movie) {
        mRepository.updateMovie(movie);
    }

    public void deleteMovie(Movie movie) {
        mRepository.deleteMovie(movie);
    }

    public void insertMovie(Movie movie) {
        mRepository.insertMovie(movie);
    }
}
