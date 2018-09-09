package com.cherifcodes.popularmovies_v03.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MovieDao {
    @Insert
    void insert(Movie movie);

    @Delete
    void delete(Movie movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Movie movie);

    @Query("SELECT * FROM movie ORDER BY mVoteAverage DESC")
    LiveData<List<Movie>> loadAllMovies();
}
