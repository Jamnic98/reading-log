package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class WLEditWindowController implements Initializable {
    
    private int bookID;
    private final static int MAX_TEXT_BOX_CHARACTERS = 50;

    @FXML private TextField editWindowTitleTextBox;
    @FXML private TextField editWindowAuthorTextBox;
    @FXML private Label editWindowTitleLabel;
    @FXML private Label editWindowAuthorLabel;
    @FXML private Button editWindowSubmitButton;

    public void handleSubmitButton() {

        String titleText = editWindowTitleTextBox.getText().trim();
        String authorText = editWindowAuthorTextBox.getText().trim();

        if (!(titleText.equals("") || authorText.equals(""))) {
            titleText = Main.capitalizeWords(titleText);
            authorText = Main.capitalizeWords(authorText);
            String updatedTitleText = Main.escapeApostrophes(titleText);
            String updatedAuthorText = Main.escapeApostrophes(authorText);
            try (Connection connection = DBConnector.getDBConnection()) {
                assert connection != null;

                // Update database
                String sql = "UPDATE UNREAD SET TITLE='"+updatedTitleText+"',"
                        + "AUTHOR='"+updatedAuthorText+"' WHERE ID='"+bookID+"'";

                connection.createStatement().executeUpdate(sql);

                // Import data from database and update table
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
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
            editWindowSubmitButton.getScene().getWindow().hide();
        }
    }

    public TextField getEditWindowTitleTextBox() {
        return editWindowTitleTextBox;
    }

    public TextField getEditWindowAuthorTextBox() {
        return editWindowAuthorTextBox;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Main.addTextLimitFunctionality(editWindowTitleTextBox, editWindowTitleLabel, MAX_TEXT_BOX_CHARACTERS);
        Main.addTextLimitFunctionality(editWindowAuthorTextBox, editWindowAuthorLabel, MAX_TEXT_BOX_CHARACTERS);
    }
}
