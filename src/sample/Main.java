package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main extends Application {

    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 500;

    public static String capitalizeWords(String str) {
        return Arrays.stream(str.split("\\s+"))
                .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1))
                .collect(Collectors.joining(" "));
    }

    public static String escapeApostrophes(String str) {
        return str.replaceAll("'", "\\\\'");
    }

    public static void addTextLimitFunctionality(
            TextInputControl textArea, Label label, int characterCount) {

        String defaultLabel = label.getText();

        textArea.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= characterCount ? change : null));

        textArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (obs.getValue().length() == 0) {
                label.setText(defaultLabel);
            }
        });

        textArea.setOnKeyTyped(keyEvent -> {
            if (!(textArea.getText().length() == 0)) {
                label.setText(defaultLabel + " ("
                        + (characterCount - textArea.getText().length()) + " characters remaining)");
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Reading Log");
        Image applicationIcon = new Image(getClass().getResourceAsStream("icon.png"));
        primaryStage.getIcons().add(applicationIcon);
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
