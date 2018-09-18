package com.cherifcodes.popularmovies_v03.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

@Entity (tableName = "movie")
public class Movie implements Parcelable {
    @PrimaryKey
    private int id;
    private String mOriginalTitle;
    private String mPosterString;
    private String mReleaseDate;
    private String mOverview;
    private double mVoteAverage;

    public Movie(int id, String originalTitle, String posterString, String releaseDate,
                 String mOverview, double voteAverage) {
        this.id = id;
        this.mOriginalTitle = originalTitle;
        this.mPosterString = posterString;
        this.mReleaseDate = releaseDate;
        this.mOverview = mOverview;
        this.mVoteAverage = voteAverage;
    }

    public int getId() {return id;}

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getPosterString() {
        return mPosterString;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getOverview() {
        return mOverview;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    @Override
    @Ignore
    public String toString() {
        return "Movie{" +
                "\nid= '" + id + '\'' +
                "\nmOriginalTitle='" + mOriginalTitle + '\'' +
                ", \nmPosterString='" + mPosterString + '\'' +
                ", \nmReleaseDate='" + mReleaseDate + '\'' +
                ", \nmOverview='" + mOverview + '\'' +
                ", \nmVoteAverage=" + mVoteAverage +
                '}';
    }

    @Ignore
    @Override
    public boolean equals(Object object) {
        if (object != null && (object instanceof Movie)) {
            Movie movie = (Movie) object;
            return movie.id == this.id;
        }
        return false;
    }

    @Ignore
    public Movie(Parcel in) {
        this.id = in.readInt();
        this.mOriginalTitle = in.readString();
        this.mOverview = in.readString();
        this.mPosterString = in.readString();
        this.mReleaseDate = in.readString();
        this.mVoteAverage = in.readDouble();
    }

    @Ignore
    public static final Creator CREATOR = new Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Ignore
    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.id);
        parcel.writeString(this.mOriginalTitle);
        parcel.writeString(this.mOverview);
        parcel.writeString(this.mPosterString);
        parcel.writeString(this.mReleaseDate);
        parcel.writeDouble(this.mVoteAverage);
    }

    @Ignore
    public boolean isFavoriteMovie(List<Movie> movieList) {
        for (Movie movie : movieList) {
            if (this.equals(movie))
                return true;
        }
        return false;
    }
}
