package com.cherifcodes.popularmovies_v03.Utils;

import com.cherifcodes.popularmovies_v03.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonToMovieList {

    private static final String RESULTS = "results";
    private static final String ID = "id";
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String OVERVIEW = "overview";
    private static final String POSTER_PATH = "poster_path";
    private static final String RELEASE_DATE = "release_date";
    private static final String VOTE_AVERAGE = "vote_average";

    public static final String VIDEO_TRAILER_DETAIL_KEY = "key";
    public static final String REVIEWS_DETAIL_KEY = "content";


    /**
     * Parses a JSON string to create a Movie object
     * @param json the JSON string to parse
     * @return the newly created Movie object
     */
    public static List<Movie> parseMovieListJson(String json) {

        List<Movie> movieList = new ArrayList<>();

        try {
            JSONObject rootJsonObject = new JSONObject(json);
            JSONArray jsonArrayOfMovies = rootJsonObject.getJSONArray(RESULTS);

            for (int i = 0; i < jsonArrayOfMovies.length(); i++) {
                JSONObject jsonMovie = jsonArrayOfMovies.getJSONObject(i);
                int movieId = jsonMovie.getInt(ID);
                String originalTitle = jsonMovie.getString(ORIGINAL_TITLE);
                String overview = jsonMovie.getString(OVERVIEW);
                String moviePoster = jsonMovie.getString(POSTER_PATH);
                String releaseDate = jsonMovie.getString(RELEASE_DATE);
                double voteAverage = jsonMovie.getDouble(VOTE_AVERAGE);

                Movie nextMovie = new Movie(movieId, originalTitle, moviePoster, releaseDate,
                        overview, voteAverage);

                movieList.add(nextMovie);
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return movieList;
    }

    /**
     * Gets a list of movie details, such as videos, or reviews, from a given JSON result
     *
     * @param json      the specified JSON result string
     * @param detailKey the movie detail result key (i.e. "key" for video trailers or "content" for
     *                  movie reviews)
     * @return a List of strings representing movie video trailers or movie reviews
     */
    public static List<String> getMovieDetailListFromJson(String json, String detailKey) {

        List<String> movieDetailList = new ArrayList<>();

        try {
            JSONObject rootJsonObject = new JSONObject(json);
            JSONArray jsonArrayOfDetails = rootJsonObject.getJSONArray(RESULTS);

            for (int i = 0; i < jsonArrayOfDetails.length(); i++) {
                JSONObject movieDetailJsonObject = jsonArrayOfDetails.getJSONObject(i);
                movieDetailList.add(movieDetailJsonObject.getString(detailKey));
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return movieDetailList;
    }


}
