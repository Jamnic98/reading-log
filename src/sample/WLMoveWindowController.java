package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.Rating;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class WLMoveWindowController implements Initializable {

    private Rating rating;
    private int bookID;
    private final static int MAX_TEXT_AREA_CHARACTERS = 200;
    private final static int MAX_TEXT_BOX_CHARACTERS = 50;

    @FXML private TextField titleTextBox;
    @FXML private TextField authorTextBox;
    @FXML private TextArea commentsTextBox;
    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label moveWindowCommentsLabel;
    @FXML private Button moveWindowSubmitButton;
    @FXML private GridPane gridPane;

    public void handleSubmitButton() {

        String titleText = titleTextBox.getText().trim();
        String authorText = authorTextBox.getText().trim();
        String comments = commentsTextBox.getText().trim();

        if (!(titleText.equals("") || authorText.equals(""))) {
            titleText = Main.capitalizeWords(titleText);
            authorText = Main.capitalizeWords(authorText);
            String updatedTitleText = Main.escapeApostrophes(titleText);
            String updatedAuthorText = Main.escapeApostrophes(authorText);
            String updatedCommentsText = Main.escapeApostrophes(comments);
            double ratingValue = rating.getRating();

            try (Connection connection = DBConnector.getDBConnection()) {
                assert connection != null;

                // Delete data from unread books database
                String sql = "DELETE FROM UNREAD WHERE ID='"+bookID+"'";
                connection.createStatement().executeUpdate(sql);

                //Delete data from unread books list
                sql = "SELECT * FROM UNREAD";
                ResultSet resultSet = connection.createStatement().executeQuery(sql);
                MyBooks.getUnreadBooksList().removeAll(MyBooks.getUnreadBooksList());
                while (resultSet.next()) {
                    {
                        MyBooks.getUnreadBooksList().add(new MyBooks(
                                resultSet.getInt("id"),
                                resultSet.getString("title"),
                                resultSet.getString("author"),
                                0, ""));
                    }
                }

                // Add data to read books database
                sql = "INSERT INTO READ(TITLE,AUTHOR,RATING,COMMENTS) VALUES("
                        + "'" + updatedTitleText
                        + "','" + updatedAuthorText
                        + "','" + ratingValue
                        + "','" + updatedCommentsText + "')";
                connection.createStatement().executeUpdate(sql);

                // Add book to read books list
                sql = "SELECT * FROM READ";
                resultSet = connection.createStatement().executeQuery(sql);
                MyBooks.getReadBooksList().removeAll(MyBooks.getReadBooksList());
                while (resultSet.next()) {
                    {
                        MyBooks.getReadBooksList().add(new MyBooks(
                                resultSet.getInt("id"),
                                resultSet.getString("title"),
                                resultSet.getString("author"),
                                resultSet.getDouble("rating"),
                                resultSet.getString("comments")));
                    }
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
            moveWindowSubmitButton.getScene().getWindow().hide();
        }
    }

    public TextField getEditWindowTitleTextBox() {
        return titleTextBox;
    }

    public TextField getEditWindowAuthorTextBox() {
        return authorTextBox;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Main.addTextLimitFunctionality(titleTextBox, titleLabel, MAX_TEXT_BOX_CHARACTERS);
        Main.addTextLimitFunctionality(authorTextBox, authorLabel, MAX_TEXT_BOX_CHARACTERS);
        Main.addTextLimitFunctionality(commentsTextBox, moveWindowCommentsLabel, MAX_TEXT_AREA_CHARACTERS);

        // Initialise rating object for popup window
        rating = MyRating.getRating();
        gridPane.add(rating, 0, 6);
    }
}
