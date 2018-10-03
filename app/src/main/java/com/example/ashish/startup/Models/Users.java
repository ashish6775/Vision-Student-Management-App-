package com.example.ashish.startup.Models;

import java.util.Comparator;

public class Users {
    private String Name, Username;
    private boolean isSelected;

public Users(){

}
    public static final Comparator<Users> BY_NAME_ALPHABETICAL = new Comparator<Users>() {
    @Override
    public int compare(Users users, Users t1) {
        return users.Username.compareTo(t1.Username);
    }
};


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public Users(String name, String username) {
        Name = name;
        Username = username;
    }
}
