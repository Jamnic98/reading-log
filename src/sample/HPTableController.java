package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.controlsfx.control.Rating;

import java.net.URL;
import java.util.ResourceBundle;

public class HPTableController implements Initializable {

    @FXML private TableView<MyBooks> myBooksTableView;
    @FXML private TableColumn<MyBooks, String> titleColumn;
    @FXML private TableColumn<MyBooks, String> authorColumn;
    @FXML private TableColumn<MyBooks, Number> ratingColumn;

    public TableView<MyBooks> getMyBooksTableView() {
        return myBooksTableView;
    }

    public TableColumn<MyBooks, String> getTitleColumn() {
        return titleColumn;
    }

    public TableColumn<MyBooks, String> getAuthorColumn() {
        return authorColumn;
    }

    public TableColumn<MyBooks, Number> getRatingColumn() {
        return ratingColumn;
    }

    public void registerCommentTooltips(TableView<MyBooks> view) {

        view.setRowFactory(tv -> new TableRow<>() {
            private final Tooltip tooltip = new Tooltip();

            @Override
            public void updateItem(MyBooks books, boolean empty) {
                super.updateItem(books, empty);
                if (books == null) {
                    setTooltip(null);
                } else {
                    String comments = books.getComments();
                    if (comments.trim().equals("")) {
                        comments = "NO COMMENTS";
                    }
                    tooltip.setText(comments);
                    setTooltip(tooltip);
                }
            }
        });
    }

    public void registerDeselectRowListener(TableView<MyBooks> view) {

        Callback<TableView<MyBooks>, TableRow<MyBooks>> existingRowFactory = view.getRowFactory();

        view.setRowFactory(tv -> {
            TableRow<MyBooks> row ;
            if (existingRowFactory == null) {
                row = new TableRow<>();
            } else {
                row = existingRowFactory.call(view);
            }

            row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    MainWindowController.lastSelectedRowProperty().set(row);
                }
            });
            return row;
        });

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        registerCommentTooltips(myBooksTableView);
        registerDeselectRowListener(myBooksTableView);

        // Set up the HP table
        titleColumn.setStyle("-fx-alignment:CENTER;");
        authorColumn.setStyle("-fx-alignment:CENTER;");
        ratingColumn.setStyle("-fx-alignment:CENTER;");
        titleColumn.setSortable(true);
        authorColumn.setSortable(true);
        ratingColumn.setSortable(true);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        ratingColumn.setCellValueFactory(cd -> cd.getValue().ratingProperty());
        ratingColumn.setCellFactory(table -> new TableCell<>() {

            private final Rating rating = new Rating(10);
            {
                rating.setDisable(true);
                rating.setScaleX(0.65);
                rating.setScaleY(0.65);
            }

            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    rating.setRating(item.doubleValue());
                    setGraphic(rating);
                }
            }
        });

        myBooksTableView.getSelectionModel().setCellSelectionEnabled(false);
        myBooksTableView.setItems(MyBooks.getSortedBooks());
        MyBooks.getSortedBooks().comparatorProperty().bind(myBooksTableView.comparatorProperty());
        myBooksTableView.getSortOrder().add(titleColumn);
        myBooksTableView.sort();
    }
}
