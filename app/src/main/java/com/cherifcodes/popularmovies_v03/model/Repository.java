package com.cherifcodes.popularmovies_v03.model;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Repository {

    private static Repository instance;
    private static Context mContext;
    private AppDatabase db;
    private Executor mExecutor = Executors.newSingleThreadExecutor();

    private Repository() {
        db = AppDatabase.getInstance(mContext);
    }

    public static Repository getInstance(Context context) {
        if (instance == null) {
            mContext = context;
            instance = new Repository();
            return instance;
        }

        return instance;
    }

    public void insertMovie(final Movie movie) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                db.getMovieDao().insert(movie);
            }
        });

    }

    public void deleteMovie(final Movie movie) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                db.getMovieDao().delete(movie);
            }
        });
    }

    public LiveData<List<Movie>> loadAllMovies() {
        return db.getMovieDao().loadAllMovies();
    }
}
