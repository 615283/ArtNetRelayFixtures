package com.georlegacy.general.artnetrelayfixtures.objects.core;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

public class Fixture implements Serializable {

    static final long serialUid = 40L;

    private SimpleStringProperty name;
    private SimpleIntegerProperty dmxChannel;
    private SimpleIntegerProperty dmxUniverse;

    public Fixture(String name) {
        this.name = new SimpleStringProperty();
        this.dmxChannel = new SimpleIntegerProperty();
        this.dmxUniverse = new SimpleIntegerProperty();

        if (name != null)
            this.name.set(name);
    }

    public boolean isConfigured() {
        return dmxChannel.get() != 0 &&
                dmxUniverse.get() != 0;
    }

    public String getName() {
        return name.get();
    }

    public int getDmxChannel() {
        return dmxChannel.get();
    }

    public int getDmxUniverse() {
        return dmxUniverse.get();
    }


    public void setName(String name) {
        this.name.set(name);
    }

    public void setDmxChannel(short dmxChannel) {
        this.dmxChannel.set(dmxChannel);
    }

    public void setDmxUniverse(short dmxUniverse) {
        this.dmxUniverse.set(dmxUniverse);
    }

}
