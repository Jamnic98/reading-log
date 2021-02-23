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

public class HPEditWindowController implements Initializable {

    Rating rating;
    private int bookID;
    private final static int MAX_TEXT_AREA_CHARACTERS = 200;
    private final static int MAX_TEXT_BOX_CHARACTERS = 50;

    @FXML private TextField titleTextBox;
    @FXML private TextField authorTextBox;
    @FXML private TextArea commentsTextBox;
    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label commentsLabel;
    @FXML private Button submitButton;
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

                // Add new data to database
                String sql = "UPDATE READ SET TITLE='"+updatedTitleText+"',"
                        + "AUTHOR='"+updatedAuthorText+ "'," + "RATING='"+ratingValue+"',"
                        + "COMMENTS='"+updatedCommentsText+"' WHERE ID='"+bookID+"'";

                connection.createStatement().executeUpdate(sql);

                // Import data from database and update table
                sql = "SELECT * FROM READ";
                ResultSet resultSet = connection.createStatement().executeQuery(sql);

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
            submitButton.getScene().getWindow().hide();
        }
    }

    public TextField getTitleTextBox() {
        return titleTextBox;
    }

    public TextField getAuthorTextBox() {
        return authorTextBox;
    }

    public TextArea getCommentsTextBox() {
        return commentsTextBox;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Main.addTextLimitFunctionality(titleTextBox, titleLabel, MAX_TEXT_BOX_CHARACTERS);
        Main.addTextLimitFunctionality(authorTextBox, authorLabel, MAX_TEXT_BOX_CHARACTERS);
        Main.addTextLimitFunctionality(commentsTextBox, commentsLabel, MAX_TEXT_AREA_CHARACTERS);

        // Initialise rating object for popup window
        rating = MyRating.getRating();
        gridPane.add(rating, 0, 6);
    }
}
