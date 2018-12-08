package com.georlegacy.general.artnetrelayfixtures;

import com.georlegacy.general.artnetrelayfixtures.objects.core.ArtNetRelayFixturesDataStore;
import com.georlegacy.general.artnetrelayfixtures.objects.core.Fixture;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

@SuppressWarnings("unchecked")
public class Main extends Application {

    private final ArtNetRelayFixturesDataStore dataStore;

    public Main() {
        dataStore = ArtNetRelayFixturesDataStore.load();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dataStore.getFixtures().clear();
            dataStore.getFixtures().addAll(fixtureTable.getItems());
            dataStore.save();
        }));
        try {
            Runtime.getRuntime().exec("cmd /c assoc .anrf=ArtNet Relay Fixtures Data Store");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final TableView<Fixture> fixtureTable = new TableView<Fixture>();

    public void start(Stage primaryStage) {
//        ArtNetClient client = new ArtNetClient();
//        client.start();
//        System.out.println(Arrays.toString(client.readDmxData(0, 1)));


        BorderPane rootPane = new BorderPane();

        VBox fixtureManagement = new VBox();



        fixtureTable.setItems(FXCollections.observableArrayList(dataStore.getFixtures()));

        fixtureTable.setEditable(false);
        TableColumn fixtureNameColumn = new TableColumn("Fixture Name");
        fixtureNameColumn.setCellValueFactory(new PropertyValueFactory<Fixture, String>("name"));
        TableColumn universeColumn = new TableColumn("DMX Universe");
        universeColumn.setCellValueFactory(new PropertyValueFactory<Fixture, Integer>("dmxUniverse"));
        TableColumn channelColumn = new TableColumn("DMX Channel");
        channelColumn.setCellValueFactory(new PropertyValueFactory<Fixture, Integer>("dmxChannel"));
        fixtureTable.getColumns().addAll(fixtureNameColumn, universeColumn, channelColumn);

        fixtureTable.setPlaceholder(new Label("There are currently no fixtures, add one below."));

        fixtureTable.setRowFactory(new Callback<TableView<Fixture>, TableRow<Fixture>>() {
            @Override
            public TableRow<Fixture> call(TableView<Fixture> param) {
                return new TableRow<Fixture>(){
                    @Override
                    protected void updateItem(Fixture item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            if (item.getDmxChannel() == 0 || item.getDmxUniverse() == 0 || item.getDmxChannel() > 512) {
                                setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                            }
                        }
                    }
                };
            }
        });

        final Button deleteFixtureButton = new Button();
        deleteFixtureButton.setText("Delete Fixture");
        deleteFixtureButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fixtureTable.getItems().remove(fixtureTable.getSelectionModel().getSelectedItem());
                deleteFixtureButton.setVisible(false);
            }
        });
        deleteFixtureButton.setVisible(false);

        final Button configureFixtureButton = new Button();
        configureFixtureButton.setText("Configure Fixture");
        configureFixtureButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage fixtureConfigurationStage = new Stage();
                Fixture fixture = fixtureTable.getSelectionModel().selectedItemProperty().get();
                fixtureConfigurationStage.setTitle("Configure Fixture \"" +
                        fixture.getName() + "\"");

                VBox root = new VBox();
                root.setAlignment(Pos.CENTER);
                Label alertLabel = new Label("No Alerts To Show.");
                alertLabel.setVisible(false);
                Label nameLabel = new Label("Fixture Name");
                TextField nameField = new TextField(fixture.getName());
                Label universeLabel = new Label("DMX Universe");
                TextField universeField = new TextField(String.valueOf(fixture.getDmxUniverse()));
                Label channelLabel = new Label("DMX Channel");
                TextField channelField = new TextField(String.valueOf(fixture.getDmxChannel()));

                HBox buttons = new HBox();
                buttons.setAlignment(Pos.CENTER);
                Button saveButton = new Button("Save");
                saveButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        fixture.setName(nameField.getText());
                        alertLabel.setVisible(false);
                        try {
                            fixture.setDmxUniverse(Integer.parseInt(universeField.getText()));
                            fixture.setDmxChannel(Integer.parseInt(channelField.getText()));
                            fixtureConfigurationStage.close();
                            configureFixtureButton.setVisible(false);
                            fixtureTable.refresh();
                        } catch (NumberFormatException ex) {
                            alertLabel.setText("Universe and Channel fields need to be integers.");
                            alertLabel.setVisible(true);
                        }
                    }
                });
                Button cancelButton = new Button("Cancel");
                cancelButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        fixtureConfigurationStage.close();
                        configureFixtureButton.setVisible(false);
                    }
                });
                buttons.getChildren().addAll(saveButton, cancelButton);

                root.getChildren().addAll(alertLabel, nameLabel, nameField, universeLabel, universeField, channelLabel,
                        channelField, buttons);
                fixtureConfigurationStage.setScene(new Scene(root, 300, 300));
                fixtureConfigurationStage.show();
            }
        });
        configureFixtureButton.setVisible(false);

        fixtureTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                deleteFixtureButton.setVisible(false);
                configureFixtureButton.setVisible(false);
            } else {
                deleteFixtureButton.setVisible(true);
                configureFixtureButton.setVisible(true);
            }
        });

        final HBox fixtureAdditionDeletionBox = new HBox();
        final TextField newFixtureNameInput = new TextField();
        newFixtureNameInput.setPromptText("Fresnel DSL 2");
        final Button addFixtureButton = new Button();
        addFixtureButton.setText("Add Fixture");
        addFixtureButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fixtureTable.getItems().add(new Fixture(newFixtureNameInput.getText()));
            }
        });
        fixtureAdditionDeletionBox.getChildren().add(newFixtureNameInput);
        fixtureAdditionDeletionBox.getChildren().add(addFixtureButton);

        fixtureManagement.getChildren().add(fixtureTable);
        //fixtureManagement.getChildren().add(fixtureList);
        fixtureManagement.getChildren().add(fixtureAdditionDeletionBox);
        fixtureManagement.getChildren().add(deleteFixtureButton);
        fixtureManagement.getChildren().add(configureFixtureButton);

        VBox controlButtons = new VBox();
        controlButtons.setAlignment(Pos.BOTTOM_CENTER);
        Button openCurrentDMXInPanelButton = new Button();
        openCurrentDMXInPanelButton.setText("Current DMX Input");
        controlButtons.getChildren().add(openCurrentDMXInPanelButton);

        Button hideInterfaceButton = new Button();
        hideInterfaceButton.setText("Hide Interface");
        controlButtons.getChildren().add(hideInterfaceButton);

        Button openSettingsPanelButton = new Button();
        openSettingsPanelButton.setText("Settings");
        controlButtons.getChildren().add(openSettingsPanelButton);

        rootPane.setLeft(fixtureManagement);
        rootPane.setRight(controlButtons);

        Scene rootScene = new Scene(rootPane, 800, 600);

        primaryStage.setScene(rootScene);
        primaryStage.setTitle("ArtNet Relay Fixtures Interface");
        primaryStage.show();
    }

}
