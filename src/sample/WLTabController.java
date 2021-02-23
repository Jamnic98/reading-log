package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class WLTabController implements Initializable {

    private static final ObservableList<String> choiceBoxWLItems = FXCollections.observableArrayList("Title", "Author");

    @FXML public WLTableController wlTableController;
    @FXML public TableView<MyBooks> wlTable;

    @FXML private TextField searchField;
    @FXML private ChoiceBox<String> choiceBoxWL;

    public void handleAddButtonWL() {
        wlTableController.getBookTableViewWL().getSelectionModel().clearSelection();
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WLAddWindow.fxml"));
        Parent root;
        try {
            root = loader.load();
            stage.setTitle("Add");
            stage.setScene(new Scene(root, 300, 250));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleEditButtonWL() throws IOException {

        MyBooks book = wlTableController.getBookTableViewWL().getSelectionModel().getSelectedItem();
        if (book != null) {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("WLEditWindow.fxml"));
            Parent root = loader.load();

            WLEditWindowController editWindow = loader.getController();
            editWindow.getEditWindowTitleTextBox().setText(book.getTitle());
            editWindow.getEditWindowAuthorTextBox().setText(book.getAuthor());

            editWindow.setBookID(book.getId());
            stage.setOnCloseRequest(windowEvent -> wlTableController.getBookTableViewWL().getSelectionModel().clearSelection());
            stage.setTitle("Edit");
            stage.setScene(new Scene(root, 300, 250));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        }
    }

    public void handleDeleteButtonWL()  {
        MyBooks book = wlTableController.getBookTableViewWL().getSelectionModel().getSelectedItem();
        if (book != null) {
            int bookID = book.getId();
            MyBooks.getUnreadBooksList().remove(book);

            try (Connection connection = DBConnector.getDBConnection()) {
                String sql = "DELETE FROM UNREAD WHERE ID='"+bookID+"'";
                assert connection != null;
                connection.createStatement().executeUpdate(sql);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    }

    public void handleMoveButtonWL() throws IOException {
        MyBooks book = wlTableController.getBookTableViewWL().getSelectionModel().getSelectedItem();
        if (book != null) {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("WLMoveWindow.fxml"));
            Parent root = loader.load();

            WLMoveWindowController moveWindow = loader.getController();
            moveWindow.getEditWindowTitleTextBox().setText(book.getTitle());
            moveWindow.getEditWindowAuthorTextBox().setText(book.getAuthor());

            moveWindow.setBookID(book.getId());
            stage.setOnCloseRequest(windowEvent -> wlTableController.getBookTableViewWL().getSelectionModel().clearSelection());
            stage.setTitle("Move");
            stage.setScene(new Scene(root, 300, 400));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // WL PAGE
        // Connect to database and load data for table
        try (Connection connection = DBConnector.getDBConnection()) {
            String sql = "SELECT * FROM UNREAD";
            assert connection != null;
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            while (resultSet.next()) {
                MyBooks.getUnreadBooksList().add(new MyBooks(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        0, "")
                );
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        searchField.setOnKeyReleased(keyEvent -> {
            if (choiceBoxWL.getValue() == null) {
                return;
            }
            if (searchField.getText().equals("")) {
                MyBooks.getFlUnreadBooks().setPredicate(null);
                return;
            }
            // Switch choiceBox value
            switch (choiceBoxWL.getValue()) {
                case "Title" -> MyBooks.getFlUnreadBooks().setPredicate(p
                        -> p.getTitle().toLowerCase().startsWith(searchField.getText().toLowerCase().trim()));
                case "Author" -> MyBooks.getFlUnreadBooks().setPredicate(p
                        -> p.getAuthor().toLowerCase().startsWith(searchField.getText().toLowerCase().trim()));
            }
        });

        // Initialise choiceBox
        choiceBoxWL.setItems(choiceBoxWLItems);
        choiceBoxWL.getSelectionModel().selectFirst();
        choiceBoxWL.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            wlTableController.getBookTableViewWL().getSortOrder().clear();
            wlTableController.getTitleColumnWL().setSortType(TableColumn.SortType.ASCENDING);
            wlTableController.getAuthorColumnWL().setSortType(TableColumn.SortType.ASCENDING);
            switch (choiceBoxWL.getValue()) {
                case "Title" ->  wlTableController.getBookTableViewWL().getSortOrder().add(wlTableController.getTitleColumnWL());
                case "Author" -> wlTableController.getBookTableViewWL().getSortOrder().add(wlTableController.getAuthorColumnWL());
            }

            if (newVal != null) {
                searchField.setText("");
                MyBooks.getFlUnreadBooks().setPredicate(null);
            }
        });

        // Set up the WL table
        wlTableController.getTitleColumnWL().setStyle("-fx-alignment:CENTER;");
        wlTableController.getAuthorColumnWL().setStyle("-fx-alignment:CENTER;");
        wlTableController.getTitleColumnWL().setSortable(true);
        wlTableController.getAuthorColumnWL().setSortable(true);
        wlTableController.getTitleColumnWL().setCellValueFactory(new PropertyValueFactory<>("title"));
        wlTableController.getAuthorColumnWL().setCellValueFactory(new PropertyValueFactory<>("author"));

        wlTableController.getBookTableViewWL().getSelectionModel().setCellSelectionEnabled(false);
        wlTableController.getBookTableViewWL().setItems(MyBooks.getSortedUnreadBooks());
        MyBooks.getSortedUnreadBooks().comparatorProperty().bind(wlTableController.getBookTableViewWL().comparatorProperty());
        wlTableController.getBookTableViewWL().getSortOrder().add(wlTableController.getTitleColumnWL());
        wlTableController.getBookTableViewWL().sort();

        wlTableController.getBookTableViewWL().setRowFactory(tableView -> {
            TableRow<MyBooks> row = new TableRow<>();
            row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    MainWindowController.lastSelectedRowProperty().set(row);
                }
            });
            return row;
        });
    }
}
