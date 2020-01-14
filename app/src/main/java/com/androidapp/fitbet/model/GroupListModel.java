package com.androidapp.fitbet.model;

public class GroupListModel {

    String groupid="";

    String name="";

    String description="";

    String groupimage="";

    String reg_key ="";

    String createdate="";

    String status="";

    String users="";

    private boolean isChecked  = false;



    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupimage() {
        return groupimage;
    }

    public void setGroupimage(String groupimage) {
        this.groupimage = groupimage;
    }

    public String getReg_key() {
        return reg_key;
    }

    public void setReg_key(String reg_key) {
        this.reg_key = reg_key;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public void setSelected(boolean selected) {
        isChecked  = selected;
    }


    public boolean isSelected() {
        return isChecked ;
    }



}
