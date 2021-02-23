package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class WLTableController implements Initializable {

    @FXML private TableView<MyBooks> bookTableViewWL;
    @FXML private TableColumn<MyBooks, String> titleColumnWL;
    @FXML private TableColumn<MyBooks, String> authorColumnWL;

    public TableView<MyBooks> getBookTableViewWL() {
        return bookTableViewWL;
    }

    public TableColumn<MyBooks, String> getTitleColumnWL() {
        return titleColumnWL;
    }

    public TableColumn<MyBooks, String> getAuthorColumnWL() {
        return authorColumnWL;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
