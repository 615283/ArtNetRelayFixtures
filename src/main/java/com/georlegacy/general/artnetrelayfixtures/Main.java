package com.georlegacy.general.artnetrelayfixtures;

import com.georlegacy.general.artnetrelayfixtures.objects.core.Fixture;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Main extends Application {

    public void start(Stage primaryStage) {
        BorderPane rootPane = new BorderPane();

        VBox fixtureManagement = new VBox();

        final ListView<Fixture> fixtureList = new ListView<Fixture>();

        final Button deleteFixtureButton = new Button();
        deleteFixtureButton.setText("Delete Fixture");
        deleteFixtureButton.setVisible(false);

        fixtureList.setItems(FXCollections.<Fixture>observableArrayList(new Fixture("Test Fixture")));
        fixtureList.setCellFactory(new Callback<ListView<Fixture>, ListCell<Fixture>>() {
            public ListCell<Fixture> call(ListView<Fixture> param) {
                final Label leadLabel = new Label();
                final Tooltip tooltip = new Tooltip();
                final ListCell<Fixture> cell = new ListCell<Fixture>() {
                    @Override
                    protected void updateItem(Fixture item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            leadLabel.setText(item.getName());
                            setText(item.getName() + String.format(" (%d)", item.getDmxChannel()));
                            if (!item.isConfigured())
                                setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                            tooltip.setText(item.isConfigured() ? "" : "This fixture needs configuring!");
                            setTooltip(tooltip);
                        }
                    }
                };
                return cell;
            }
        });
        fixtureList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Fixture>() {
            public void changed(ObservableValue<? extends Fixture> observable, Fixture oldValue, Fixture newValue) {
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
