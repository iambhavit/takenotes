package com.anuntah.takenotes;

/**
 * Created by Bhavit Yadav on 19-02-2018.
 */

public class Label {
    private int id;
    private String label;
    private int notes_id;

    public Label(String label, int notes_id) {
        this.label = label;
        this.notes_id = notes_id;
    }

    public Label(int id, String label, int notes_id) {
        this.id = id;
        this.label = label;
        this.notes_id = notes_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getNotes_id() {
        return notes_id;
    }

    public void setNotes_id(int notes_id) {
        this.notes_id = notes_id;
    }
}
