package GUILogic;

import Enumerators.Genres;
import PlannerData.Artist;
import PlannerData.Show;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.collections.ObservableList;

public class BaseControls {

    private Stage upperStage;
    private Show Selected;
    private ArrayList<String> errorlist = new ArrayList<>();
    private ArrayList<String> timelist = new ArrayList<>();
    private int additionalArtists = 0;
    private Button cancel = new Button("Cancel");
    private Stage popUp = new Stage();
    private TableView<Show> table;
    private ObservableList<Show> data;
    private int stagePopularity = 0;

    /**
     * This is the constructor of the base layout of the windows of the three Menus.
     * The method also sends the user to the correct menu window.
     * @param ScreenNumber
     * @param upperStage
     * @param data
     * @param table
     * @param Selected
     */
    public BaseControls(int ScreenNumber, Stage upperStage, javafx.collections.ObservableList<Show> data, TableView<Show> table, Show Selected) {
        this.upperStage = upperStage;
        this.table = table;
        this.data = data;
        this.Selected = Selected;
        this.popUp.setWidth(400);
        this.popUp.setHeight(450);
        this.popUp.initOwner(this.upperStage);
        this.popUp.initModality(Modality.WINDOW_MODAL);
        try {
            this.stagePopularity = this.Selected.getStage().getCapacity();
        }catch(Exception e){

        }
        cancelsetup();
        if (ScreenNumber==1){
            additionWindow();
        }
        else if (ScreenNumber==2){
            editoryWindow();
        }
        else {
            deletionWindow();
        }
    }

    /**
     * This method allows the user to Add a new Show.
     */
    public void additionWindow() {
        this.additionalArtists = 0;
        BorderPane structure = new BorderPane();

        Label addingNew = new Label("what show do you want to add?");
        structure.setTop(addingNew);

        GridPane inputStructure = new GridPane();
        inputStructure.setHgap(10);
        inputStructure.setVgap(10);

        //showname
        Label showName = new Label("Show name:");
        TextField inputShowName = new TextField();
        inputStructure.add(showName, 1, 1);
        inputStructure.add(inputShowName, 2, 1);

        //time
        inputStructure.add(new Label("Begin time:"), 1, 2);
        inputStructure.add(new Label("End time:"), 1, 3);
        ComboBox beginUur = timeBox();
        ComboBox eindUur = timeBox();
        inputStructure.add(beginUur, 2, 2);
        inputStructure.add(eindUur, 2, 3);

        //stage
        inputStructure.add(new Label("Stage:"), 1, 4);
        ComboBox stage = StageBox();
        inputStructure.add(stage, 2, 4);

        //genre
        inputStructure.add(new Label("Genre:"), 1, 5);
        ComboBox genre = genreBox();
        inputStructure.add(genre, 2, 5);

        //popularity
        inputStructure.add(new Label("Popularity:"), 1, 6);
        Slider popularity = new Slider();
        popularity.setMin(0);
        popularity.setMax(100);
        popularity.setValue(0);
        popularity.setShowTickLabels(true);
        popularity.setShowTickMarks(true);
        popularity.setMajorTickUnit(50);
        popularity.setMinorTickCount(5);
        popularity.setBlockIncrement(10);
        inputStructure.add(popularity, 2, 6);
        Label PopularityLabel = new Label("");
        popularity.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                PopularityLabel.textProperty().setValue(
                        String.valueOf(newValue.intValue()));
            }
        });

        PopularityLabel.textProperty().setValue("0");
        inputStructure.add(PopularityLabel, 3, 6);

        //artists
        inputStructure.add(new Label("Artists:"), 1, 7);
        ComboBox artists = artistBox();
        inputStructure.add(artists, 2, 7);

        //add more artists
        Button showArtistAdder = new Button("+");
        inputStructure.add(showArtistAdder, 3, 7);
        showArtistAdder.setOnAction(event -> {
            this.additionalArtists++;
            ComboBox ArtistAdded = artistBox();
            inputStructure.add(ArtistAdded, 2, 7 + this.additionalArtists);
            ArtistAdded.setOnAction(e -> {
                if (ArtistAdded.getValue().equals("None")) {
                    inputStructure.getChildren().remove(ArtistAdded);
                    this.additionalArtists--;
                }
            });
        });


        ScrollPane ArtistScroller = new ScrollPane();
        ArtistScroller.setContent(inputStructure);
        structure.setCenter(ArtistScroller);

        //buttons
        HBox choice = new HBox();
        Button submit = new Button("Submit");
        submit.setOnAction(event -> {

            control(beginUur, eindUur, stage, genre, inputShowName);
            String showNameAdding = inputShowName.getText();
            LocalTime beginTime = indexToLocalTime(this.timelist.indexOf(beginUur.getValue()));
            LocalTime endTime = indexToLocalTime(this.timelist.indexOf(eindUur.getValue()));
            PlannerData.Stage stageAdded = stringToStage((String) stage.getValue());
            ArrayList<Artist> addedArtists = new ArrayList<>();
            if (artists.getSelectionModel().getSelectedItem() != null) {
                String comboBoxString = artists.getSelectionModel().getSelectedItem().toString();
                boolean containsArtist = false;
                for (Artist artist : addedArtists) {
                    if (artist.getName().equals(comboBoxString)) {
                        containsArtist = true;
                    }
                }
                if (!containsArtist) {
                    Artist artist = stringToArtist(comboBoxString);
                    if (artist != null) {
                        addedArtists.add(artist);
                    }
                }
            } else {
                addedArtists = null;
            }

            Genres addedGenre;
            if (genre.getValue() != null) {
                addedGenre = stringToGenre(genre.getValue().toString());
            } else {
                addedGenre = null;
            }

            int popularityAdded;
            if (stageAdded != null) {
                popularityAdded = (int) (stageAdded.getCapacity() * (popularity.getValue() / 100));
            } else {
                popularityAdded = -1;
            }

            if (addedArtists == null || addedArtists.isEmpty() || stageAdded == null || beginTime == null || endTime == null || showNameAdding.isEmpty() || popularityAdded < 0) {
                return;
            } else {
                ArrayList<Genres> genres = new ArrayList<>();
                if (addedGenre == null) {
                    for (Artist artist : addedArtists) {
                        genres.add(artist.getGenre());
                    }
                } else {
                    genres.add(addedGenre);
                }
                Show show = new Show(beginTime, endTime, addedArtists, showNameAdding, stageAdded, "", genres, popularityAdded);
                DataController.getPlanner().addShow(show);
                this.data.add(show);
            }
        });

        choice.getChildren().add(this.cancel);
        choice.getChildren().add(submit);
        choice.setPadding(new Insets(10));
        choice.setSpacing(20);
        structure.setBottom(choice);

        Scene adderScene = new Scene(structure);
        adderScene.getStylesheets().add("Window-StyleSheet.css");
        this.popUp.setScene(adderScene);
        this.popUp.show();
    }

    /**
     * This method allows the user to edit the selected show.
     */
    public void editoryWindow() {
        BorderPane structure = new BorderPane();

        Label editingThis = new Label("Edit this show:");
        structure.setTop(editingThis);

        GridPane inputStructure = new GridPane();
        inputStructure.setHgap(10);
        inputStructure.setVgap(10);

        Label showName = new Label("Show name:");
        TextField inputShowName = new TextField(this.Selected.getName());
        inputStructure.add(showName, 1, 1);
        inputStructure.add(inputShowName, 2, 1);

        inputStructure.add(new Label("Begin time:"), 1, 2);
        inputStructure.add(new Label("End time:"), 1, 3);
        ComboBox beginUur = timeBox();
        ComboBox eindUur = timeBox();
        inputStructure.add(beginUur, 2, 2);
        inputStructure.add(eindUur, 2, 3);

        inputStructure.add(new Label("Stage:"), 1, 4);
        ComboBox stage = StageBox();
        inputStructure.add(stage, 2, 4);
        // add listener for popularity slider

        inputStructure.add(new Label("Genre:"), 1, 5);
        ComboBox genre = genreBox();
        inputStructure.add(genre, 2, 5);

        inputStructure.add(new Label("Popularity:"), 1, 6);
        Slider popularity = new Slider();
        popularity.setMin(0);
        popularity.setMax(this.stagePopularity);
        popularity.setValue(this.Selected.getExpectedPopularity());
        popularity.setShowTickLabels(true);
        popularity.setShowTickMarks(true);
        popularity.setMajorTickUnit(50);
        popularity.setMinorTickCount(5);
        popularity.setBlockIncrement(10);
        inputStructure.add(popularity, 2, 6);
        Label PopularityLabel = new Label("");
        popularity.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                PopularityLabel.textProperty().setValue(
                        String.valueOf(newValue.intValue()));
            }
        });

        PopularityLabel.textProperty().setValue(""+this.Selected.getExpectedPopularity());

        inputStructure.add(PopularityLabel, 3, 6);

        inputStructure.add(new Label("Artists:"), 1, 7);
        ComboBox artists = artistBox();
        inputStructure.add(artists, 2, 7);
        Button showArtistAdder = new Button("+");
        inputStructure.add(showArtistAdder, 3, 7);
        showArtistAdder.setOnAction(event -> {
            this.additionalArtists++;
            ComboBox ArtistAdded = artistBox();
            inputStructure.add(ArtistAdded, 2, 7 + this.additionalArtists);
            ArtistAdded.setOnAction(e -> {
                if (ArtistAdded.getValue().equals("None")) {
                    inputStructure.getChildren().remove(ArtistAdded);
                    this.additionalArtists--;
                }
            });
        });

        ScrollPane ArtistScroller = new ScrollPane();
        VBox ScrutcureTwo = new VBox();
        ScrutcureTwo.getChildren().add(inputStructure);
        ScrutcureTwo.getChildren().add(new Label("Show Description:"));
        TextArea ShowDescription = new TextArea(this.Selected.getDescription());
        ShowDescription.setPrefWidth(360);
        ScrutcureTwo.getChildren().add(ShowDescription);
        ScrutcureTwo.setSpacing(10);

        ArtistScroller.setContent(ScrutcureTwo);
        structure.setCenter(ArtistScroller);
        HBox choice = new HBox();
        Button submit = new Button("Submit");
        submit.setOnAction(event -> {

            control(beginUur, eindUur, stage, genre, inputShowName);

        });

        choice.getChildren().add(this.cancel);
        choice.getChildren().add(submit);
        choice.setPadding(new Insets(10));
        choice.setSpacing(20);
        structure.setBottom(choice);

        Scene editScene = new Scene(structure);
        editScene.getStylesheets().add("Window-StyleSheet.css");
        this.popUp.setScene(editScene);
        this.popUp.show();
    }

    /**
     * This method allows the user to see the selected show that they wish to delete.
     */
    public void deletionWindow() {
        BorderPane structure = new BorderPane();

        Label deleteThis = new Label("Are you sure you want to delete this show?");
        structure.setTop(deleteThis);

        Label information = new Label("Show: " + this.Selected.getName() + '\n'
                + "From " + this.Selected.getBeginTimeString() + " to " + this.Selected.getEndTimeString() + '\n'
                + "By " + this.Selected.getArtistsNames() + " in the genre of " + this.Selected.getGenre() + '\n'
                + "On stage " + this.Selected.getStageName() + '\n'
                + "Expected popularity is " + this.Selected.getExpectedPopularity() + " people."
        + "with the desciption: " + '\n' + this.Selected.getDescription());

        structure.setCenter(information);

        HBox choice = new HBox();

        Button confirm = new Button("Confirm");
        confirm.setOnAction(event -> {
            if (DataController.getPlanner().deleteShow(this.Selected)){
                this.table.getItems().remove(this.Selected);
                DataController.getPlanner().savePlanner();
                this.popUp.close();
            }
            else {
                this.errorlist.add("The Show was not deleted, please try again later.");
                new ErrorWindow(this.popUp, this.errorlist);
            }
        });

        choice.getChildren().add(this.cancel);
        choice.getChildren().add(confirm);
        choice.setPadding(new Insets(10));
        choice.setSpacing(20);

        structure.setBottom(choice);
        Scene deleteScene = new Scene(structure);
        deleteScene.getStylesheets().add("Window-StyleSheet.css");
        this.popUp.setScene(deleteScene);
        this.popUp.show();
    }

    public void control(ComboBox beginUur, ComboBox eindUur, ComboBox stage, ComboBox genre, TextField showName) {
        this.errorlist.clear();
        int beginIndex = this.timelist.indexOf(beginUur.getValue());
        int endIndex = this.timelist.indexOf(eindUur.getValue());
        if (showName.getText().length() == 0) {
            this.errorlist.add("The show name has not been filled in.");
        }
        if (beginUur.getValue() == null || eindUur.getValue() == null) {
            if (beginUur.getValue() == null) {
                this.errorlist.add("The begintime has not been filled in.");
            }
            if (eindUur.getValue() == null) {
                this.errorlist.add("The endtime has not been filled in.");
            }
        } else {
            if (beginIndex > endIndex) {
                this.errorlist.add("The begintime is later than the endtime.");
            } else if (beginIndex == endIndex) {
                this.errorlist.add("The begintime the same as the endtime.");
            }
        }
        if (stage.getValue() == null) {
            this.errorlist.add("The stage has not been filled in.");
        }
        if (genre.getValue() == null) {
            this.errorlist.add("The genre has not been filled in.");
        }


        if (this.errorlist.isEmpty()) {
//            DataController.getPlanner().addShow(new Show(beginUur.getValue()));
            //TODO: LocalTime stuff when adding new show
            this.popUp.close();
        } else {
            new ErrorWindow(this.popUp, this.errorlist);
        }
    }

    /**
     * This method sets the action of the Cancel button.
     */
    public void cancelsetup() {
        this.cancel.setOnAction(event -> {
            this.popUp.close();
        });
    }

    /**
     * This method makes a ComboBox with all the known Genres. it is not possible to add a Genre.
     * This method is only used once in the Adding menu and once in the Edit menu.
     * @return ComboBox
     */
    public ComboBox genreBox() {
        ComboBox comboBox = new ComboBox();
        comboBox.getItems().add("None");
        for(Genres genre :Genres.values()){
            comboBox.getItems().add(genre.getFancyName());
        }
        return comboBox;
    }

    /**
     * This method makes a ComboBox with all the known stages and allows for the user to create a new Stage.
     * This method is only used once in the Adding Menu and once in the Edit Menu.
     * @return ComboBox
     */
    public ComboBox StageBox() {
        ComboBox comboBox = new ComboBox();

        for (PlannerData.Stage stage : DataController.getPlanner().getStages()) {
            comboBox.getItems().add(stage.getName());
        }

        comboBox.getItems().add("Add new Stage");

        comboBox.setOnAction(event -> {
            if (comboBox.getValue().equals("Add new Stage")) {
                new AddingNewWindow(1,this.popUp);
            }
        });

        return comboBox;
    }

    /**
     * This method allows a ComboBox to be made with the total amount of artists that are known and to add an unknown Artist to the Show.
     * This ComboBox will be at least used once in the Adding menu as in the Edit Menu.
     * @return ComboBox
     */
    public ComboBox artistBox() {
        ComboBox comboBox = new ComboBox();
        comboBox.getItems().add("None");
        for (Artist artist : DataController.getPlanner().getArtists()) {
            comboBox.getItems().add(artist.getName());
        }
        comboBox.getItems().add("Add new Artist");

        comboBox.setOnAction(event -> {
            if (comboBox.getValue().equals("Add new Artist")) {
                new AddingNewWindow(2,this.popUp);
            }
        });

        return comboBox;
    }

    /**
     * In the method of timeBox we can make a timeBox which contains all half and whole hours to plan a show.
     * This box will be used twice in the Adding menu as in the Edit menu.
     * @return ComboBox
     */
    public ComboBox timeBox() {
        ComboBox uurBox = new ComboBox();
        String time = "";
        String halftime = "";
        for (int i = 0; i <= 23; i++) {
            if (i < 10) {
                time = "0" + i;
            } else {
                time = "" + i;
            }
            for (int j = 0; j <= 1; j++) {
                if (j == 0) {
                    halftime = time;
                    time += ":00";
                } else {
                    halftime += ":30";
                    time = halftime;
                }
                if (!this.timelist.contains(time)) {
                    this.timelist.add(time);
                }
                uurBox.getItems().add(time);
            }
        }
        return uurBox;
    }

    public Genres stringToGenre(String genreString){
        for(Genres genre : Genres.values()){
            if(genre.getFancyName().equals(genreString)){
                return genre;
            }
        }
        return null;
    }

    public LocalTime indexToLocalTime(int index){
        LocalTime time = LocalTime.MIDNIGHT;
        int hours = index / 2;
        if(index % 2 == 1){
            time = time.plusMinutes(30);
        }
        time = time.plusHours(hours);
        return time;
    }

    public PlannerData.Stage stringToStage(String stageString){
        if(stageString == null || stageString.isEmpty()){
            return null;
        }
        for(PlannerData.Stage stage : DataController.getPlanner().getStages()){
            if(stageString.equals(stage.getName())){
                return stage;
            }
        }
        return null;
    }

    public Artist stringToArtist(String artistString){
        for(Artist artist : DataController.getPlanner().getArtists()){
            if(artistString.equals(artist.getName())){
                return artist;
            }
        }
        return null;
    }

}
