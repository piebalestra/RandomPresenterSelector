package main;

import controller.Controller;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Pietro
 */
public class Main extends Application {
    
    private FXMLLoader fxmlLoader;
    
    @Override
    public void start(Stage stage) throws Exception {
        URL resource = getClass().getResource("/view/View.fxml");
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(resource);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

        Parent root = (Parent) fxmlLoader.load(resource.openStream());
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }
    
    @Override
    public void stop() {
        ((Controller) fxmlLoader.getController()).closeDB();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
