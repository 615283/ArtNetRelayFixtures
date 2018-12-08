package com.georlegacy.general.artnetrelayfixtures;

import com.georlegacy.general.artnetrelayfixtures.objects.core.Fixture;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

@SuppressWarnings("unchecked")
public class Main extends Application {

    public void start(Stage primaryStage) {
//        ArtNetClient client = new ArtNetClient();
//        client.start();
//        System.out.println(Arrays.toString(client.readDmxData(0, 1)));


        BorderPane rootPane = new BorderPane();

        VBox fixtureManagement = new VBox();


        final TableView<Fixture> fixtureTable = new TableView<Fixture>();

        fixtureTable.setItems(FXCollections.observableArrayList(new Fixture("test")));

        fixtureTable.setEditable(false);
        TableColumn fixtureNameColumn = new TableColumn("Fixture Name");
        fixtureNameColumn.setCellValueFactory(new PropertyValueFactory<Fixture, String>("name"));
        TableColumn universeColumn = new TableColumn("DMX Universe");
        universeColumn.setCellValueFactory(new PropertyValueFactory<Fixture, Integer>("dmxUniverse"));
        TableColumn channelColumn = new TableColumn("DMX Channel");
        channelColumn.setCellValueFactory(new PropertyValueFactory<Fixture, Integer>("dmxChannel"));
        fixtureTable.getColumns().addAll(fixtureNameColumn, universeColumn, channelColumn);

        fixtureTable.setRowFactory(new Callback<TableView<Fixture>, TableRow<Fixture>>() {
            @Override
            public TableRow<Fixture> call(TableView<Fixture> param) {
                return new TableRow<Fixture>(){
                    @Override
                    protected void updateItem(Fixture item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            if (item.getDmxChannel() == 0 || item.getDmxUniverse() == 0) {
                                setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                            }
                        }
                    }
                };
            }
        });

        final ListView<Fixture> fixtureList = new ListView<Fixture>();

        final Button deleteFixtureButton = new Button();
        deleteFixtureButton.setText("Delete Fixture");
        deleteFixtureButton.setVisible(false);

        final Button configureFixtureButton = new Button();
        configureFixtureButton.setText("Configure Fixture");
        configureFixtureButton.setVisible(false);

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
                configureFixtureButton.setVisible(true);
            }
        });

        final HBox fixtureAdditionDeletionBox = new HBox();
        final TextField newFixtureNameInput = new TextField();
        newFixtureNameInput.setPromptText("Fresnel DSL 2");
        final Button addFixtureButton = new Button();
        addFixtureButton.setText("Add Fixture");
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
