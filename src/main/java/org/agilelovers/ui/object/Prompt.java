package org.agilelovers.ui.object;


import org.agilelovers.ui.controller.MainController;

import java.util.Date;

public abstract class Prompt implements Comparable<Prompt> {
    String command;
    private String id;
    private Date createdDate;

    public void setCommand(String command) {
        this.command = command;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCommand(){
        return this.command;
    }

    public String getId() {
        return id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setTitle(String newTitle) {
        MainController.instance.refreshLabels();
    }

    public void setBody(String newBody) {
        MainController.instance.refreshLabels();
    }

    public abstract String getTitle();

    public abstract String getBody();

    @Override
    public String toString() {
        return this.getTitle();
    }

    @Override
    public int compareTo(Prompt o) {
        return this.createdDate.compareTo(o.createdDate);
    }
}
