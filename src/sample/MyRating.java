package sample;

import javafx.scene.input.MouseButton;
import org.controlsfx.control.Rating;

public final class MyRating {

    public static Rating getRating() {

        Rating rating = new Rating(10);
        rating.setRating(0.0);
        rating.setTranslateX(-38.0);
        rating.setScaleX(0.7);
        rating.setScaleY(0.7);

        rating.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    rating.setRating(0.0);
                }
            }
        });
        return rating;
    }
}
