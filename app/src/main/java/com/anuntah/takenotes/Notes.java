package com.anuntah.takenotes;

import java.util.ArrayList;

/**
 * Created by Bhavit Yadav on 14-02-2018.
 */

public class Notes {
    private Boolean isSelected=false;
    private String title;
    private  int id;
    private String description;
    private String datetime;
    private String label;
    public Notes(String title,String description,String datetime){
        this.description=description;
        this.id=-1;
        this.title=title;
        this.datetime=datetime;
    }
    public Notes(String title,String description,int id,String datetime,String label){
        this.title=title;
        this.description=description;
        this.id=id;
        this.datetime=datetime;
        this.label=label;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
