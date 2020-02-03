package PlannerData;

import Enumerators.Genres;

import java.awt.*;

import javafx.scene.image.Image;

public class Artist {

	private String name;
	private Genres genre;
	private Image image;
	private String description;

	public Artist(String name, Genres genre, Image image, String description) {
		this.name = name;
		this.genre = genre;
		this.image = image;
		this.description = description;
	}

	public Artist(String name, Genres genre, String description) {
		this.name = name;
		this.genre = genre;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public Genres getGenre() {
		return genre;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}
}