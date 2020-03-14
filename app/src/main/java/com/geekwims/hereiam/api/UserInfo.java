package com.geekwims.hereiam.api;

/**
 * api user info
 */

public class UserInfo {
    private static UserInfo instance = null;

    private String id = null;
    private String name = null;
    private String password = null;

    private UserInfo() {
    }

    public static UserInfo getInstance(String id, String name) {
        if (UserInfo.instance == null)
            UserInfo.instance = new UserInfo();

        UserInfo.instance.setId(id);
        UserInfo.instance.setName(name);

        return UserInfo.instance;
    }

    public static UserInfo getInstance() {
        if (UserInfo.instance == null)
            UserInfo.instance = new UserInfo();

        return UserInfo.instance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLogined() {
        return id != null;

    }
}
