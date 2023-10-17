package com.example.aplicatiemanagementfilme.database.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity(tableName = "movies", foreignKeys = @ForeignKey(entity = WatchList.class,
        parentColumns = "id", childColumns = "watchListId"))
public class Movie implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "watchListId")
    private long watchListId;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "releaseDate")
    private Date releaseDate;
    @ColumnInfo(name = "genres")
    private List<String> genres;
    @ColumnInfo(name = "storyLine")
    private String storyline;
    @ColumnInfo(name = "actors")
    private List<String> actors;
    @ColumnInfo(name = "imdbRating")
    private float imdbRating;
    @ColumnInfo(name = "posterUrl")
    private String posterUrl;

    public Movie(long id, long watchListId, String title, Date releaseDate, List<String> genres,
                 String storyline, List<String> actors, float imdbRating, String posterUrl) {
        this.id = id;
        this.watchListId = watchListId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.genres = genres;
        String[] storyLineArray = storyline.split("Written by");
        this.storyline = storyLineArray[0];
        this.actors = actors;
        this.imdbRating = imdbRating;
        this.posterUrl = posterUrl;
    }

    @Ignore
    public Movie(String title, Date releaseDate, List<String> genres,
                 String storyline, List<String> actors, float imdbRating, String posterUrl) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.genres = genres;
        String[] storyLineArray = storyline.split("Written by");
        this.storyline = storyLineArray[0];
        this.actors = actors;
        this.imdbRating = imdbRating;
        this.posterUrl = posterUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getStoryline() {
        return storyline;
    }

    public void setStoryline(String storyline) {
        this.storyline = storyline;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }

    public float getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(float imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getWatchListId() {
        return watchListId;
    }

    public void setWatchListId(long watchListId) {
        this.watchListId = watchListId;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                ", genres=" + genres +
                ", storyline='" + storyline + '\'' +
                ", actors=" + actors +
                ", imdbRating=" + imdbRating +
                ", posterUrl='" + posterUrl + '\'' +
                '}';
    }
}
