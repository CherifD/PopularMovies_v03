package com.cherifcodes.popularmovies_v03.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.cherifcodes.popularmovies_v03.model.Movie;
import com.cherifcodes.popularmovies_v03.model.Repository;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> mMovieList;
    private Repository mRepository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        mRepository = Repository.getInstance(application);
        mMovieList = mRepository.loadAllMovies();
    }

    public void insertMovie(Movie movie) {
        mRepository.insertMovie(movie);
    }

    public void deleteMovie(Movie movie) {
        mRepository.deleteMovie(movie);
    }

    public LiveData<List<Movie>> getMovieList() {
        return mMovieList;
    }
}
