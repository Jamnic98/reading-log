package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class WLAddWindowController implements Initializable {

    private final static int MAX_TEXT_BOX_CHARACTERS = 50;

    @FXML private TextField titleTextBox;
    @FXML private TextField authorTextBox;
    @FXML private Label addWindowTitleLabel;
    @FXML private Label addWindowAuthorLabel;
    @FXML private Button submitButton;

    public void handleAddWindowSubmitButton() {

        String titleText = titleTextBox.getText().trim();
        String authorText = authorTextBox.getText().trim();

        if (!(titleText.equals("") || authorText.equals(""))) {

            titleText = Main.capitalizeWords(titleText);
            authorText = Main.capitalizeWords(authorText);
            String updatedTitleText = Main.escapeApostrophes(titleText);
            String updatedAuthorText = Main.escapeApostrophes(authorText);

            try (Connection connection = DBConnector.getDBConnection()) {
                assert connection != null;

                // Add new data to database
                String sql = "INSERT INTO UNREAD(TITLE,AUTHOR) VALUES("
                        + "'" + updatedTitleText
                        + "','" + updatedAuthorText + "')";
                connection.createStatement().executeUpdate(sql);

                // Import data from database and update table
                sql = "SELECT * FROM UNREAD";
                ResultSet resultSet = connection.createStatement().executeQuery(sql);
                List<Integer> idList = new ArrayList<>();
                for (MyBooks book: MyBooks.getUnreadBooksList()) {
                    idList.add(book.getId());
                }
                while (resultSet.next()) {
                    int currentInt = resultSet.getInt("id");
                    if (!idList.contains(currentInt)) {
                        MyBooks.getUnreadBooksList().add(new MyBooks(
                                currentInt,
                                resultSet.getString("title"),
                                resultSet.getString("author"),
                                0, ""));
                    }
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
            submitButton.getScene().getWindow().hide();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Main.addTextLimitFunctionality(titleTextBox, addWindowTitleLabel, MAX_TEXT_BOX_CHARACTERS);
        Main.addTextLimitFunctionality(authorTextBox, addWindowAuthorLabel, MAX_TEXT_BOX_CHARACTERS);
    }
}
