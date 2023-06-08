package org.agilelovers.ui.object;


import java.util.Date;

public abstract class Prompt implements Comparable<Prompt> {
    private String command;
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

    public abstract void setTitle(String newTitle);

    public abstract void setBody(String newBody);
    public abstract String getTitle();

    public abstract String getBody();

    @Override
    public String toString() {
        return this.command;
    }

    @Override
    public int compareTo(Prompt o) {
        return this.createdDate.compareTo(o.createdDate);
    }
}
