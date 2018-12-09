package com.georlegacy.general.artnetrelayfixtures.tasks;

import ch.bildspur.artnet.ArtNetClient;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;

@Deprecated
public class UpdateUniverseDisplayTask implements Runnable {

    private final ArtNetClient client;
    private final FlowPane parent;

    public UpdateUniverseDisplayTask(ArtNetClient client, FlowPane parent) {
        this.client = client;
        this.parent = parent;
    }

    @Override
    public void run() {
        System.out.println("Running");
        for (Label channel : (Label[]) parent.getChildren().stream().filter(n -> n instanceof Label).toArray()) {
            int id = Integer.parseInt(channel.getId());
            byte[] rawData = client.readDmxData(0, 1);
            int data = (int) rawData[id - 1] >= 0 ? (int) rawData[id - 1] : 256 + (int) rawData[id -1];
            channel.setBackground(new Background(new BackgroundFill(data != 0 ?
                    Color.BLACK : Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            channel.setTextFill(data != 0 ? Color.WHITE : Color.BLACK);
            channel.setText(String.format("  %d  ", data));
        }
    }

}
