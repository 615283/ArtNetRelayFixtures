package com.georlegacy.general.artnetrelayfixtures;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    public void start(Stage primaryStage) {
        BorderPane rootPane = new BorderPane();

        VBox fixtureManagement = new VBox();

        final ListView<String> fixtureList = new ListView<String>();

        final Button deleteFixtureButton = new Button();
        deleteFixtureButton.setText("Delete Fixture");
        deleteFixtureButton.setVisible(false);

        fixtureList.setItems(FXCollections.<String>observableArrayList("Test Fixture 1", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2", "Test Fixture 2"));
        fixtureList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                deleteFixtureButton.setVisible(true);
            }
        });

        final HBox fixtureAdditionDeletionBox = new HBox();
        final TextField newFixtureNameInput = new TextField();
        newFixtureNameInput.setPromptText("Fresnel DSL 2");
        final Button addFixtureButton = new Button();
        addFixtureButton.setText("Add Fixture");
        fixtureAdditionDeletionBox.getChildren().add(newFixtureNameInput);
        fixtureAdditionDeletionBox.getChildren().add(addFixtureButton);

        fixtureManagement.getChildren().add(fixtureList);
        fixtureManagement.getChildren().add(fixtureAdditionDeletionBox);
        fixtureManagement.getChildren().add(deleteFixtureButton);

        rootPane.setLeft(fixtureManagement);

        Scene rootScene = new Scene(rootPane, 800, 600);

        primaryStage.setScene(rootScene);
        primaryStage.setTitle("ArtNet Relay Fixtures Interface");
        primaryStage.show();
    }

}
