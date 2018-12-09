package com.georlegacy.general.artnetrelayfixtures;

import ch.bildspur.artnet.ArtNetClient;
import com.georlegacy.general.artnetrelayfixtures.objects.core.ArtNetRelayFixturesDataStore;
import com.georlegacy.general.artnetrelayfixtures.objects.core.Fixture;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@SuppressWarnings("unchecked")
public class Main extends Application {

    private final ArtNetRelayFixturesDataStore dataStore;
    private final ScheduledExecutorService scheduledExecutorService;

    public Main() {
        dataStore = ArtNetRelayFixturesDataStore.load();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dataStore.getFixtures().clear();
            dataStore.getFixtures().addAll(fixtureTable.getItems());
            dataStore.save();
        }));
        try {
            Runtime.getRuntime().exec("cmd /c assoc .anrf=ArtNet Relay Fixtures Data Store");
            Runtime.getRuntime().exec("reg add hkcr\\ArtNet Relay Fixtures Data Store\\DefaultIcon /ve /d D:\\anrf.ico");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final TableView<Fixture> fixtureTable = new TableView<Fixture>();

    public void start(Stage primaryStage) {
        ArtNetClient client = new ArtNetClient();
        client.start();


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
                return new TableRow<Fixture>() {
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
        fixtureManagement.getChildren().add(fixtureAdditionDeletionBox);
        fixtureManagement.getChildren().add(deleteFixtureButton);
        fixtureManagement.getChildren().add(configureFixtureButton);

        VBox controlButtons = new VBox();
        controlButtons.setAlignment(Pos.BOTTOM_CENTER);
        Button openCurrentDMXInPanelButton = new Button();
        openCurrentDMXInPanelButton.setText("Current DMX Input");
        openCurrentDMXInPanelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage currentDMXInPanelStage = new Stage();
                currentDMXInPanelStage.setTitle("Current DMX Input");
                StackPane root = new StackPane();
                VBox universeDisplays = new VBox();
                FlowPane universe1 = new FlowPane();
                int i = 0;
                for (byte b : client.readDmxData(0, 1)) {
                    i++;
                    int data = (int) b >= 0 ? (int) b : 256 + (int) b;
                    Label channel = new Label(String.format("  %d  ", data));
                    channel.setId(String.valueOf(i));
                    channel.setBackground(new Background(new BackgroundFill(data != 0 ?
                            Color.BLACK : Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                    channel.setTextFill(data != 0 ? Color.WHITE : Color.BLACK);
                    universe1.getChildren().add(channel);
                }
                ScheduledService<Void> service = new ScheduledService<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                try {
                                    for (Node node : universe1.getChildren()) {
                                        Label channel = (Label) node;
                                        int id = Integer.parseInt(channel.getId());
                                        byte[] rawData = client.readDmxData(0, 1);
                                        int channelData = (int) rawData[id - 1] >= 0 ? (int) rawData[id - 1] : 256 + (int) rawData[id - 1];
                                        Platform.runLater(() ->
                                                channel.setText(String.format("  %d  ", channelData)));
                                        channel.setBackground(new Background(new BackgroundFill(channelData != 0 ?
                                                Color.BLACK : Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                                        channel.setTextFill(channelData != 0 ? Color.WHITE : Color.BLACK);
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                return null;
                            }
                        };
                    }
                };
                service.setPeriod(Duration.millis(100));
                service.start();
                universeDisplays.getChildren().add(universe1);
                root.getChildren().add(universeDisplays);
                currentDMXInPanelStage.setScene(new Scene(root, 400, 500));
                currentDMXInPanelStage.setOnCloseRequest(ev -> service.cancel());
                currentDMXInPanelStage.show();
            }
        });
        controlButtons.getChildren().add(openCurrentDMXInPanelButton);

        if (SystemTray.isSupported()) {
            Platform.setImplicitExit(false);

            EventHandler closeHandler = event -> {
                Platform.runLater(primaryStage::hide);

                TrayIcon trayIcon = null;
                Image img = null;
                try {
                    img = ImageIO.read(Main.class.getClassLoader().getResourceAsStream("anrf_white.png"));
                    img = img.getScaledInstance(16, 16, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PopupMenu menu = new PopupMenu();
                menu.setName("ANRF Popup");
                MenuItem header = new MenuItem("ArtNet Relay Fixtures");
                header.setEnabled(false);
                MenuItem showItem = new MenuItem("Open");
                showItem.addActionListener(e -> {
                    Platform.runLater(primaryStage::show);

                    Optional<TrayIcon> optionalTrayIcon = Arrays.stream(SystemTray.getSystemTray().getTrayIcons())
                            .filter(ti -> ti.getPopupMenu().getName().equals("ANRF Popup")).findFirst();
                    optionalTrayIcon.ifPresent(trayIcon1 -> SystemTray.getSystemTray().remove(trayIcon1));
                });
                MenuItem quitItem = new MenuItem("Quit");
                quitItem.addActionListener(ev -> {
                    System.exit(0);
                    Optional<TrayIcon> optionalTrayIcon = Arrays.stream(SystemTray.getSystemTray().getTrayIcons())
                            .filter(ti -> ti.getPopupMenu().getName().equals("ANRF Popup")).findFirst();
                    optionalTrayIcon.ifPresent(trayIcon1 -> SystemTray.getSystemTray().remove(trayIcon1));
                });
                menu.add(header);
                menu.addSeparator();
                menu.add(showItem);
                menu.add(quitItem);
                trayIcon = new TrayIcon(Objects.requireNonNull(img), "ArtNet Relay Fixtures", menu);
                trayIcon.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            Platform.runLater(primaryStage::show);

                            Optional<TrayIcon> optionalTrayIcon = Arrays.stream(SystemTray.getSystemTray().getTrayIcons())
                                    .filter(ti -> ti.getPopupMenu().getName().equals("ANRF Popup")).findFirst();
                            optionalTrayIcon.ifPresent(trayIcon1 -> SystemTray.getSystemTray().remove(trayIcon1));
                        }
                    }

                    @Override public void mousePressed(MouseEvent e) { }
                    @Override public void mouseReleased(MouseEvent e) { }
                    @Override public void mouseEntered(MouseEvent e) { }
                    @Override public void mouseExited(MouseEvent e) { }
                });
                try {
                    SystemTray.getSystemTray().add(trayIcon);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            };

            primaryStage.setOnCloseRequest(closeHandler);

            Button hideInterfaceButton = new Button();
            hideInterfaceButton.setText("Hide Interface");
            hideInterfaceButton.setOnAction(closeHandler);
            controlButtons.getChildren().add(hideInterfaceButton);
        }

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
