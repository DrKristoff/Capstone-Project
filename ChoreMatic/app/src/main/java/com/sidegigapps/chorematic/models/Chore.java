package com.sidegigapps.chorematic.models;

/**
 * Created by ryand on 11/12/2016.
 */

public class Chore {

    int _id;
    String description;
    String frequency;
    String effort;
    String room;
    String floor;
    int lastDone;

    public int getNextDue() {
        return nextDue;
    }

    public int get_id() {
        return _id;
    }

    public String getDescription() {
        return description;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getEffort() {
        return effort;
    }

    public String getRoom() {
        return room;
    }

    public String getFloor() {
        return floor;
    }

    public int getLastDone() {
        return lastDone;
    }

    int nextDue;

    public Chore(){

    }
}

