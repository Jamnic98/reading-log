package sample;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    private static final ObjectProperty<TableRow<MyBooks>> lastSelectedRow = new SimpleObjectProperty<>();

    @FXML public HPTabController hpTabController;
    @FXML public WLTabController wlTabController;

    @FXML public AnchorPane hpTab;
    @FXML public AnchorPane wlTab;
    @FXML private AnchorPane mainAnchor;

    public static ObjectProperty<TableRow<MyBooks>> lastSelectedRowProperty() {
        return lastSelectedRow;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        mainAnchor.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            TableRow<MyBooks> tableRow = lastSelectedRow.get();
            if (tableRow != null) {
                Bounds boundsOfSelectedRow = tableRow
                        .localToScene(tableRow.getLayoutBounds());
                if (!boundsOfSelectedRow.contains(event.getSceneX(), event.getSceneY())) {
                    hpTabController.hpTableController.getMyBooksTableView().getSelectionModel().clearSelection();
                    wlTabController.wlTableController.getBookTableViewWL().getSelectionModel().clearSelection();
                }
            }
        });
    }
}
