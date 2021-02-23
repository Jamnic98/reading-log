package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HPTabController implements Initializable {

    private static final ObservableList<String> choiceBoxHPItems = FXCollections.observableArrayList(
            "Title", "Author", "Rating");

    @FXML public TableView<MyBooks> hpTable;
    @FXML private TextField searchField;
    @FXML private ChoiceBox<String> choiceBox;

    @FXML public HPTableController hpTableController;

    public void handleAddButtonHP() {
        hpTableController.getMyBooksTableView().getSelectionModel().clearSelection();
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HPAddWindow.fxml"));
        Parent root;
        try {
            root = loader.load();
            stage.setTitle("Add");
            stage.setScene(new Scene(root, 300, 400));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleEditButtonHP() throws IOException {

        MyBooks book = hpTableController.getMyBooksTableView().getSelectionModel().getSelectedItem();
        if (book != null) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("HPEditWindow.fxml"));
            Parent root = loader.load();
            HPEditWindowController editWindow = loader.getController();
            editWindow.setBookID(book.getId());
            editWindow.getTitleTextBox().setText(book.getTitle());
            editWindow.getAuthorTextBox().setText(book.getAuthor());
            editWindow.getCommentsTextBox().setText(book.getComments());
            editWindow.rating.setRating(book.getRating());

            Stage stage = new Stage();
            stage.setOnCloseRequest(windowEvent -> hpTableController.getMyBooksTableView().getSelectionModel().clearSelection());
            stage.setTitle("Edit");
            stage.setScene(new Scene(root, 300, 400));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        }
    }

    public void handleDeleteButtonHP()  {

        MyBooks book = hpTableController.getMyBooksTableView().getSelectionModel().getSelectedItem();
        if (book != null) {
            int bookID = book.getId();
            MyBooks.getReadBooksList().remove(book);

            try (Connection connection = DBConnector.getDBConnection()) {
                String sql = "DELETE FROM READ WHERE ID='"+bookID+"'";
                assert connection != null;
                connection.createStatement().executeUpdate(sql);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // HOME PAGE
        // Connect to database and load data for table
        try (Connection connection = DBConnector.getDBConnection()) {
            String sql = "SELECT * FROM READ";
            assert connection != null;

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                MyBooks.getReadBooksList().add(new MyBooks(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getDouble("rating"),
                        resultSet.getString("comments")
                ));
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        // Initialise search box
        searchField.setOnKeyReleased(keyEvent -> {
            if (choiceBox.getValue() == null) {
                return;
            }
            if (searchField.getText().equals("")) {
                MyBooks.getFlBooks().setPredicate(null);
                return;
            }
            if (choiceBox.getValue().equals("Rating")) {
                Pattern pattern = Pattern.compile("[0-9]+");
                Matcher matcher = pattern.matcher(searchField.getText().trim());
                if (!matcher.matches()) {
                    return;
                }
            }
            switch (choiceBox.getValue()) {
                case "Title" -> MyBooks.getFlBooks().setPredicate(p
                        -> p.getTitle().toLowerCase().startsWith(searchField.getText().toLowerCase().trim()));
                case "Author" -> MyBooks.getFlBooks().setPredicate(p
                        -> p.getAuthor().toLowerCase().startsWith(searchField.getText().toLowerCase().trim()));
                case "Rating" -> MyBooks.getFlBooks().setPredicate(p ->
                        p.getRating() >= Double.parseDouble(searchField.getText()));
            }
        });

        // Initialise choiceBox
        choiceBox.setItems(choiceBoxHPItems);
        choiceBox.getSelectionModel().selectFirst();
        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            hpTableController.getMyBooksTableView().getSortOrder().clear();
            hpTableController.getTitleColumn().setSortType(TableColumn.SortType.ASCENDING);
            hpTableController.getAuthorColumn().setSortType(TableColumn.SortType.ASCENDING);
            hpTableController.getRatingColumn().setSortType(TableColumn.SortType.DESCENDING);
            switch (choiceBox.getValue()) {
                case "Title" -> hpTableController.getMyBooksTableView().getSortOrder().add(hpTableController.getTitleColumn());
                case "Author" -> hpTableController.getMyBooksTableView().getSortOrder().add(hpTableController.getAuthorColumn());
                case "Rating" -> hpTableController.getMyBooksTableView().getSortOrder().add(hpTableController.getRatingColumn());
            }

            if (newVal != null) {
                searchField.setText("");
                MyBooks.getFlBooks().setPredicate(null);
            }
        });
    }
}
