package com.georlegacy.general.artnetrelayfixtures.objects.core;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class ArtNetRelayFixturesDataStore implements Serializable {

    private final Set<Fixture> fixtures;

    public ArtNetRelayFixturesDataStore() {
        fixtures = new HashSet<Fixture>();
    }

    public Set<Fixture> getFixtures() {
        return fixtures;
    }

    public static ArtNetRelayFixturesDataStore load() {
        File file = new File("store.anrf");
        if (!file.exists()) {
            return new ArtNetRelayFixturesDataStore();
        } else {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (ArtNetRelayFixturesDataStore) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return new ArtNetRelayFixturesDataStore();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void save() {
        File file = new File("store.anrf");
        if (file.exists())
            file.delete();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
