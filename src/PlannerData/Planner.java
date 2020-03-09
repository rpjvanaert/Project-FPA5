package PlannerData;

import Enumerators.Genres;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * A class that saves all added shows, artists and stages.
 */
public class Planner implements Serializable {

    private ArrayList<Show> shows;
    private ArrayList<Stage> stages;
    private ArrayList<Artist> artists;

    public final static String saveFileName = "Resources/saveFile.json";

    public Planner() {
        this.shows = new ArrayList<>();
        this.stages = new ArrayList<>();
        this.artists = new ArrayList<>();
    }

    /**
     * method to add shows and saves the planner
     *
     * @param show object where data about the show is stored
     */
    public void addShow(Show show) {
        this.shows.add(show);
        this.savePlanner();
    }

    /**
     * Adds an artists to artists
     *
     * @param name        the name of the artist
     * @param genre       the genre of the artist
     * @param description the description of the artist
     */
    public void addArtist(String name, Genres genre, String description) {
        for (Artist existingArtist : this.artists) {
            if (name.equals(existingArtist.getName())) return;
        }

        this.artists.add(new Artist(name, genre, description));
        this.savePlanner();
    }

    /**
     * Adds a stage to stages and saves the planner
     *
     * @param stage A new stage to add
     */
    public void addStage(Stage stage) {
        this.stages.add(stage);
        this.savePlanner();
    }

    /**
     * Deletes a show
     *
     * @param show the show to be deleted
     * @return true if the delete is successful, false if not
     */
    public boolean deleteShow(Show show) {
        return this.shows.remove(show);
    }

    /**
     * Deletes an artist
     *
     * @param artist the artist to be deleted
     * @return true if the deletion is successful, false if not
     */
    public boolean deleteArtist(Artist artist) {
        return this.artists.remove(artist);
    }

    /**
     * Delete an artist
     *
     * @param artistName the name of the artist to be deleted
     * @return true if the deletion is successful, false if not
     */
    public boolean deleteArtist(String artistName) {
        for (Artist artist : this.artists) {
            if (artist.getName().equals(artistName)) {
                return deleteArtist(artist);
            }
        }

        return false;
    }

    /**
     * Deletes a stage
     *
     * @param stage the stage to be deleted
     * @return true if the deletion is successful, false if not
     */
    public boolean deleteStage(Stage stage) {
        return this.stages.remove(stage);
    }

    /**
     * Deletes a stage
     *
     * @param stageName the name of the stage to be deleted
     * @return true if the deletion is succesful, false if not
     */
    public boolean deleteStage(String stageName) {
        for (Stage stage : this.stages) {
            if (stage.getName().equals(stageName)) {
                return deleteStage(stage);
            }
        }

        return false;
    }

    public ArrayList<Show> getShows() {
        return this.shows;
    }

    public ArrayList<Stage> getStages() {
        return this.stages;
    }

    public ArrayList<Artist> getArtists() {
        return this.artists;
    }

    /**
     * Saves the shows, artists and stages in a Json file
     */
    public void savePlanner() {
        try {
            JsonWriter writer = Json.createWriter(new FileWriter(saveFileName));
            JsonObjectBuilder plannerBuilder = Json.createObjectBuilder();
            JsonArrayBuilder showsBuilder = Json.createArrayBuilder();
            JsonArrayBuilder stagesBuilder = Json.createArrayBuilder();
            JsonArrayBuilder artistsBuilder = Json.createArrayBuilder();

            //saves all stages
            for (Stage stage : this.getStages()) {
                JsonObjectBuilder stageBuilder = Json.createObjectBuilder();
                stageBuilder.add("name", stage.getName());
                stageBuilder.add("capacity", stage.getCapacity());
                stagesBuilder.add(stageBuilder);
            }

            //saves all artist
            for (Artist artist : this.getArtists()) {
                JsonObjectBuilder artistBuilder = Json.createObjectBuilder();
                artistBuilder.add("name", artist.getName());
                artistBuilder.add("getShowDescription", artist.getDescription());
                artistBuilder.add("genre", artist.getGenre().getFancyName());
                artistsBuilder.add(artistBuilder);
            }

            //saves all shows
            for (Show show : this.getShows()) {
                JsonArrayBuilder showArtistsBuilder = Json.createArrayBuilder();
                JsonObjectBuilder showBuilder = Json.createObjectBuilder();
                JsonObjectBuilder stageBuilder = Json.createObjectBuilder();

                //saves the artist in a show
                for (Artist artist : show.getArtists()) {
                    JsonObjectBuilder artistBuilder = Json.createObjectBuilder();
                    artistBuilder.add("name", artist.getName());
                    artistBuilder.add("getShowDescription", artist.getDescription());
                    artistBuilder.add("genre", artist.getGenre().getFancyName());
                    showArtistsBuilder.add(artistBuilder);
                }
                Stage stage = show.getStage();
                stageBuilder.add("name", stage.getName());
                stageBuilder.add("capacity", stage.getCapacity());

                showBuilder.add("name", show.getName());
                showBuilder.add("artists", showArtistsBuilder);
                showBuilder.add("stage", stageBuilder);
                showBuilder.add("beginTime", show.getBeginTimeString());
                showBuilder.add("endTime", show.getEndTimeString());
                showBuilder.add("getShowDescription", show.getDescription());
                showBuilder.add("genre", show.getGenre().get(0).getFancyName());
                showBuilder.add("expectedPopularity", show.getExpectedPopularity());
                showsBuilder.add(showBuilder);
            }

            plannerBuilder.add("shows", showsBuilder);
            plannerBuilder.add("artists", artistsBuilder);
            plannerBuilder.add("stages", stagesBuilder);

            writer.writeObject(plannerBuilder.build());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Planner{" +
                "shows=" + shows +
                ", stages=" + stages +
                ", artists=" + artists +
                ", saveFileName='" + saveFileName + '\'' +
                '}';
    }
}