package com.georlegacy.general.artnetrelayfixtures.objects.core;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Fixture implements Serializable {

    static final long serialUid = 40L;

    private transient SimpleStringProperty name;
    private transient SimpleIntegerProperty dmxChannel;
    private transient SimpleIntegerProperty dmxUniverse;

    private String srName;
    private int srDmxChannel;
    private int srDmxUniverse;

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        name = new SimpleStringProperty(srName);
        dmxChannel = new SimpleIntegerProperty(srDmxChannel);
        dmxUniverse = new SimpleIntegerProperty(srDmxUniverse);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        srName = name.get();
        srDmxChannel = dmxChannel.get();
        srDmxUniverse = dmxUniverse.get();
        out.defaultWriteObject();
    }

    public Fixture(String name) {
        this.name = new SimpleStringProperty();
        this.dmxChannel = new SimpleIntegerProperty();
        this.dmxUniverse = new SimpleIntegerProperty();

        if (name != null)
            this.name.set(name);
    }

    public boolean isConfigured() {
        return dmxChannel.get() > 0 &&
                dmxChannel.get() <= 512 &&
                dmxUniverse.get() > 0;
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

    public void setDmxChannel(int dmxChannel) {
        this.dmxChannel.set(dmxChannel);
    }

    public void setDmxUniverse(int dmxUniverse) {
        this.dmxUniverse.set(dmxUniverse);
    }

}
