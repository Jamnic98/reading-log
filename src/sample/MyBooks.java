package sample;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class MyBooks {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty author = new SimpleStringProperty();
    private final DoubleProperty rating = new SimpleDoubleProperty();

    private final StringProperty comments = new SimpleStringProperty();

    private static final ObservableList<MyBooks> readBooksList = FXCollections.observableArrayList();
    private static final FilteredList<MyBooks> flBooks = new FilteredList<>(readBooksList, b -> true);
    private static final SortedList<MyBooks> sortedBooks = new SortedList<>(flBooks);

    private static final ObservableList<MyBooks> unreadBooksList = FXCollections.observableArrayList();
    private static final FilteredList<MyBooks> flUnreadBooks = new FilteredList<>(unreadBooksList, b -> true);
    private static final SortedList<MyBooks> sortedUnreadBooks = new SortedList<>(flUnreadBooks);

    public MyBooks(int id, String title, String author, double rating, String comments) {
        setId(id);
        setTitle(title);
        setAuthor(author);
        setRating(rating);
        setComments(comments);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public StringProperty titleProperty() {
        return this.title;
    }

    public void setTitle(String title) {
        titleProperty().setValue(title);
    }

    public String getTitle() {
        return this.titleProperty().get();
    }

    public StringProperty authorProperty() {
        return this.author;
    }

    public void setAuthor(String author) {
        authorProperty().setValue(author);
    }

    public String getAuthor() {
        return this.authorProperty().get();
    }

    public DoubleProperty ratingProperty() {
        return this.rating;
    }

    public void setRating(double rating) {
        ratingProperty().setValue(rating);
    }

    public double getRating() {
        return this.ratingProperty().get();
    }

    public String getComments() {
        return comments.get();
    }

    public void setComments(String comments) {
        this.comments.set(comments);
    }

    public static ObservableList<MyBooks> getReadBooksList() {
        return readBooksList;
    }
    public static FilteredList<MyBooks> getFlBooks() {
        return flBooks;
    }
    public static SortedList<MyBooks> getSortedBooks() {
        return sortedBooks;
    }

    public static ObservableList<MyBooks> getUnreadBooksList() {
        return unreadBooksList;
    }
    public static FilteredList<MyBooks> getFlUnreadBooks() {
        return flUnreadBooks;
    }
    public static SortedList<MyBooks> getSortedUnreadBooks() {
        return sortedUnreadBooks;
    }
}
