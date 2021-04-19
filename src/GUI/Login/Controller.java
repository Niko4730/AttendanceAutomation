package GUI.Login;

import BE.Account;
import BLL.AccountBLL;
import DAL.AccountDAL;
import UTIL.UserAlert;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.SQLException;

public class Controller {
    AccountBLL aBLL = new AccountBLL();

    public JFXTextField loginUsername;
    public JFXPasswordField loginPassword;
    @FXML
    private AnchorPane root;
    private double xOffset = 0;
    private double yOffset = 0;

    public void exit(){
        System.exit(0);
    }

    public void initialize() {
        loginUsername.setText("mads1234");
        loginPassword.setText("123");
    }

    public void minimize() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setIconified(true);
    }

    public void maximize(MouseEvent mouseEvent) {

    }

    public void login() throws IOException {
        String username = loginUsername.getText();
        String password = loginPassword.getText();

        try {
            Account loginUser = aBLL.checkLogin(username, password);

            if(loginUser != null) {
                Stage root1 = (Stage) root.getScene().getWindow();

                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("../Dashboard/View.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.show();

                scene.setOnMousePressed(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        xOffset = event.getSceneX();
                        yOffset = event.getSceneY();
                    }
                });

                scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        stage.setX(event.getScreenX() - xOffset);
                        stage.setY(event.getScreenY() - yOffset);
                        stage.setOpacity(0.8f);
                    }
                });

                scene.setOnMouseDragExited((event) -> {
                    stage.setOpacity(1.0f);
                });

                scene.setOnMouseReleased((event) -> {
                    stage.setOpacity(1.0f);
                });

                GUI.Dashboard.Controller controller = fxmlLoader.getController();

                controller.setAccount(loginUser);

                root1.close();
            } else {
                UserAlert.showAlert("Forkert!","Brugernavn og/eller adgangskode er forkert. Prøv igen!", Alert.AlertType.WARNING);
            }
        } catch(SQLException e) {
            UserAlert.showDatabaseError();
        }
    }
}
