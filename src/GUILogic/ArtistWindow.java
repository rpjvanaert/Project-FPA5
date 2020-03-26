package GUILogic;

import Enumerators.Genres;
import PlannerData.Artist;
import PlannerData.Show;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ArtistWindow {

    private Stage currStage = new Stage();
    private ArrayList<String> errorList = new ArrayList<>();
    private Label artistDeleteText = new Label();
    private Artist selectedArtist;
    private ScheduleTab ST;

    /**
     * The constructor of the artist submenu windows
     * This is also where the specific submenu is chosen
     * @param screenNumber submenu selection number
     * @param currParentStage Current parent stage
     * @param ST The schedule tab
     */
    public ArtistWindow(int screenNumber, Stage currParentStage,ScheduleTab ST) {
        this.ST=ST;
        this.currStage.initOwner(currParentStage);
        this.currStage.initModality(Modality.WINDOW_MODAL);
        this.currStage.setResizable(false);
        this.currStage.getIcons().add(new Image("logoA5.jpg"));

        switch (screenNumber) {
            case 1:
                artistAddWindow();
                break;
            case 2:
                artistEditWindow();
                break;
            case 3:
                artistDeleteWindow();
                break;
        }
    }

    /**
     * This method sets the submenu to the Add Artist submenu.
     * The user will be able to fill in all the required fields for an unknown artist.
     */
    public void artistAddWindow() {
        this.currStage.setWidth(275);
        this.currStage.setHeight(450);
        this.currStage.setTitle("Add Artist");

        VBox newArtistList = new VBox();
        newArtistList.setPrefWidth(250);

        // Artist name
        Label artistNameLabel = new Label("Artist name:");
        TextField artistName = new TextField();
        artistName.setPrefWidth(250);

        //genre
        Label artistGenreLabel = new Label("Artist genre:");
        ComboBox genreComboBox = new ComboBox();
        genreComboBox.getItems().add("Select");
        genreComboBox.getSelectionModel().selectFirst();
        for (Genres genre : Enumerators.Genres.values()) {
            genreComboBox.getItems().add(genre.getFancyName());
        }

        //artist getShowDescription
        Label artistDescriptionLabel = new Label("Artist's description:");
        TextArea artistDescription = new TextArea();
        artistDescription.setPrefWidth(250);
        artistDescription.setPrefHeight(200);

        newArtistList.getChildren().addAll(artistNameLabel, artistName, artistGenreLabel, genreComboBox, artistDescriptionLabel, artistDescription);

        //buttons
        HBox cancelConfirmButtons = new HBox();
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> this.currStage.close());
        cancelConfirmButtons.getChildren().add(cancelButton);

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(e -> {
            if (canAddArtist(artistName, artistDescription, genreComboBox.getValue().toString(), artistDescription)) {
                try {
                    DataController.getPlanner().addArtist(artistName.getText(), Genres.getGenre(genreComboBox.getValue().toString()), artistDescription.getText());
                    DataController.getPlanner().savePlanner();
                    this.currStage.close();
                } catch (Exception event) {
                    this.errorList.add("Failed to add the artist.");
                    new ErrorWindow(this.currStage, this.errorList);
                }
            }
        });

        cancelConfirmButtons.getChildren().add(confirmButton);

        cancelConfirmButtons.setPadding(new Insets(10));
        cancelConfirmButtons.setSpacing(20);

        newArtistList.getChildren().add(cancelConfirmButtons);

        ScrollPane artistScroller = new ScrollPane();
        artistScroller.setContent(newArtistList);
        newArtistList.setAlignment(Pos.CENTER);

        Scene artistAddScene = new Scene(artistScroller);
        artistAddScene.getStylesheets().add("Window-StyleSheet.css");
        this.currStage.setScene(artistAddScene);
        this.currStage.show();
    }

    /**
     * The further setup of the submenu for editing an artist
     */
    public void artistEditWindow() {
        this.currStage.setWidth(275);
        this.currStage.setHeight(450);
        this.currStage.setTitle("Edit Artist");

        VBox newArtistList = new VBox();
        Label startEdit = new Label("Which artist do you want to edit?");
        newArtistList.getChildren().add(startEdit);
        ComboBox artistComboBox = new ComboBox();

        artistComboBox.getItems().add("Select artist");
        for (Artist artist : DataController.getPlanner().getArtists()) {
            artistComboBox.getItems().add(artist.getName());
        }

        artistComboBox.getSelectionModel().selectFirst();

        newArtistList.getChildren().add(artistComboBox);
        newArtistList.setPrefWidth(250);

        //artist name
        Label artistNameLabel = new Label("Artist name:");
        TextField artistName = new TextField();
        artistName.setPrefWidth(250);
        newArtistList.getChildren().addAll(artistNameLabel, artistName);

        //genre
        Label artistGenreLabel = new Label("Artist's genres:");
        newArtistList.getChildren().add(artistGenreLabel);
        ComboBox genreComboBox = new ComboBox();

        for (Genres genre : Enumerators.Genres.values()) {
            genreComboBox.getItems().add(genre.getFancyName());
        }

        newArtistList.getChildren().add(genreComboBox);

        //artist getShowDescription
        Label artistDescriptionLabel = new Label("Artist's description:");
        TextArea artistDescription = new TextArea();
        artistDescription.setPrefWidth(250);
        artistDescription.setPrefHeight(150);
        newArtistList.getChildren().addAll(artistDescriptionLabel, artistDescription);

        artistComboBox.setOnAction(event -> {
            if (!artistComboBox.getValue().equals("Select artist")) {
                this.selectedArtist = DataController.getPlanner().getArtist(artistComboBox.getValue().toString());
                artistName.setText(this.selectedArtist.getName());
                artistDescription.setText(this.selectedArtist.getDescription());
                genreComboBox.setValue(this.selectedArtist.getGenre().getFancyName());
            } else {
                artistName.setText("");
                artistDescription.setText("");
                genreComboBox.getSelectionModel().selectFirst();

            }
        });

        //buttons
        HBox choice = new HBox();
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> this.currStage.close());
        choice.getChildren().add(cancelButton);

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(e -> {
            if (!artistComboBox.getValue().toString().equals("Select artist")) {
                if (canAddArtist(artistName, artistDescription, genreComboBox.getValue().toString(), artistDescription)) {
                    try {
                        for (Show show : DataController.getPlanner().getShows()) {
                            for (Artist artist : show.getArtists()) {
                                if (artist.getName().equals(this.selectedArtist.getName())) {
                                    artist.setName(artistName.getText());
                                    artist.setDescription(artistDescription.getText());
                                    artist.setGenre(Genres.getGenre(genreComboBox.getValue().toString()));
                                }
                            }
                        }

                        this.selectedArtist.setName(artistName.getText());
                        this.selectedArtist.setDescription(artistDescription.getText());
                        this.selectedArtist.setGenre(Genres.getGenre(genreComboBox.getValue().toString()));

                        DataController.getPlanner().savePlanner();
                        ST.resetData();
                        this.currStage.close();
                    } catch (Exception event) {
                        this.errorList.add("Failed to edit the artist.");
                        new ErrorWindow(this.currStage, this.errorList);
                    }
                }
            } else {
                this.errorList.clear();
                this.errorList.add("No Artist has been selected.");
                new ErrorWindow(this.currStage,this.errorList);
            }
        });

        choice.getChildren().add(confirmButton);

        choice.setPadding(new Insets(10));
        choice.setSpacing(20);
        newArtistList.getChildren().add(choice);

        ScrollPane artistScrollPane = new ScrollPane();
        artistScrollPane.setContent(newArtistList);
        newArtistList.setAlignment(Pos.CENTER);

        Scene artistAddScene = new Scene(artistScrollPane);
        artistAddScene.getStylesheets().add("Window-StyleSheet.css");
        this.currStage.setScene(artistAddScene);

        this.currStage.show();
    }

    /**
     * The further setup for the submenu to delete an artist
     */
    public void artistDeleteWindow() {
        this.currStage.setTitle("Delete Artist");
        this.currStage.setWidth(275);
        this.currStage.setHeight(400);

        VBox newArtistList = new VBox();
        newArtistList.setPrefWidth(250);
        HBox header = new HBox();
        header.getChildren().add(new Label("Choose the artist you want to delete:"));

        ComboBox artistComboBox = new ComboBox();
        artistComboBox.getItems().add("Select artist");
        for (Artist artist : DataController.getPlanner().getArtists()) {
            artistComboBox.getItems().add(artist.getName());
        }

        artistComboBox.getSelectionModel().selectFirst();
        artistComboBox.setOnAction(event -> {
            if (!artistComboBox.getValue().equals("Select artist")) {
                this.selectedArtist = DataController.getPlanner().getArtist(artistComboBox.getValue().toString());
                if (selectedArtist != null && !selectedArtist.getName().isEmpty()) {
                    this.artistDeleteText.textProperty().setValue("Do you want to delete the artist: " + selectedArtist.getName() + '\n' + " with the genre of " + selectedArtist.getGenre().getFancyName() + '\n' + " with the description: " + selectedArtist.getDescription());
                }
            } else {
                this.artistDeleteText.textProperty().setValue("         " + '\n');

            }
        });

        header.getChildren().add(artistComboBox);

        //buttons
        HBox cancelConfirmButtons = new HBox();
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> this.currStage.close());
        cancelConfirmButtons.getChildren().add(cancelButton);

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(e -> {
            if (!artistComboBox.getValue().toString().equals("Select artist")){
                if (canDeleteArtist()) {
                    try {
                        DataController.getPlanner().deleteArtist(artistComboBox.getValue().toString());
                        DataController.getPlanner().savePlanner();
                        this.currStage.close();
                    } catch (Exception exception) {
                        this.errorList.clear();
                        this.errorList.add("The artist could not be deleted.");
                        new ErrorWindow(this.currStage, this.errorList);
                    }
                }
            }
            else{
                this.errorList.clear();
                this.errorList.add("No artist has been set to delete");
                new ErrorWindow(this.currStage,this.errorList);
            }

        });

        cancelConfirmButtons.getChildren().add(confirmButton);

        cancelConfirmButtons.setPadding(new Insets(10));
        cancelConfirmButtons.setSpacing(20);
        newArtistList.getChildren().add(cancelConfirmButtons);

        BorderPane structure = new BorderPane();
        structure.setTop(header);
        structure.setCenter(this.artistDeleteText);
        structure.setBottom(cancelConfirmButtons);

        Scene artistDeleteScene = new Scene(structure);
        artistDeleteScene.getStylesheets().add("Window-StyleSheet.css");
        this.currStage.setScene(artistDeleteScene);
        this.currStage.show();
    }

    /**
     * This method checks whether the new Artist is valid or not.
     * If it isn't this method will notify the user what he/she needs to repair before submitting again.
     *
     * @param artistName
     * @param artistDescription
     * @param genre
     * @param description
     */
    public boolean canAddArtist(TextField artistName, TextArea artistDescription, String genre, TextArea description) {
        this.errorList.clear();

        if (artistName.getText().length() == 0) {
            this.errorList.add("The artist's name has not been filled in.");
        } else {

            for (Artist artist : DataController.getPlanner().getArtists()) {
                if (this.selectedArtist != null) {
                    if (!this.selectedArtist.equals(artist) && artistName.getText().equals(artist.getName())) {
                        this.errorList.add("This artist already exists.");
                    }
                } else {
                    if (artistName.getText().equals(artist.getName())) {
                        this.errorList.add("This artist already exists.");
                    }
                }
            }
        }
        if (genre == null || genre.equals("Select")) {
            this.errorList.add("The genre has not been filled in.");
        }

        if (artistDescription.getText().length() == 0)
            this.errorList.add("The artist's description has not been filled in.");

        if (this.errorList.isEmpty())
            return true;

        new ErrorWindow(this.currStage, this.errorList);
        return false;
    }

    /**
     * A methode that checks whether an artist can be deleted or if it is being used in a show
     * If it is used in a show it will return false, else it will return true
     * @return A true or false value
     */
    public boolean canDeleteArtist() {
        this.errorList.clear();
        for (Show show : DataController.getPlanner().getShows()) {
            for (Artist artist : show.getArtists()) {
                if (artist.getName().equals(this.selectedArtist.getName())) {
                    this.errorList.add("Artists who are performing cannot be removed from the event.");
                }
            }
        }

        if (this.errorList.isEmpty())
            return true;

        new ErrorWindow(this.currStage, this.errorList);
        return false;
    }
}