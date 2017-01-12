import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {

    public static final ClassLoader loader = Main.class.getClassLoader();
    private Desktop desktop = Desktop.getDesktop();

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/Main.fxml") );
        Parent parent = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        controller.setStage(primaryStage);
        primaryStage.setTitle("FEM");
        primaryStage.setScene(new Scene(parent, 640, 480));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
