package UTIL;

import javafx.scene.control.Alert;

public class UserAlert {
    public static void showAlert(String title, String text, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(text);
        a.showAndWait();
    }

    public static void showDatabaseError() {
        showAlert("Åh nej!","Der opstod en fejl under forbindelse til databasen. Prøv igen senere!", Alert.AlertType.ERROR);
    }
}
