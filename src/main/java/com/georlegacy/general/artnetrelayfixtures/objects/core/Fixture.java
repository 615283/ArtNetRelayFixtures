package com.georlegacy.general.artnetrelayfixtures.objects.core;

import java.io.Serializable;

public class Fixture implements Serializable {

    static final long serialUid = 40L;

    private String name;
    private short dmxChannel;
    private short dmxUniverse;
    private short voltage;

    public Fixture(String name) {
        this.name = name;
    }

    public boolean isConfigured() {
        return dmxChannel != 0 &&
                dmxUniverse != 0 &&
                voltage != 0;
    }

    public String getName() {
        return name;
    }

    public short getDmxChannel() {
        return dmxChannel;
    }

    public short getDmxUniverse() {
        return dmxUniverse;
    }

    public short getVoltage() {
        return voltage;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setDmxChannel(short dmxChannel) {
        this.dmxChannel = dmxChannel;
    }

    public void setDmxUniverse(short dmxUniverse) {
        this.dmxUniverse = dmxUniverse;
    }

    public void setVoltage(short voltage) {
        this.voltage = voltage;
    }

}
