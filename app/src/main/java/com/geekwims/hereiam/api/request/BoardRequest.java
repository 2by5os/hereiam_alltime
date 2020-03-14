package com.geekwims.hereiam.api.request;


import com.geekwims.hereiam.constant.BOARD_TYPE;
import com.geekwims.hereiam.constant.USER_TYPE;

/**
 * Created by suyoung on 2016-12-07.
 *
 */
public class BoardRequest {
    private int id;
    private String title;
    private String content;
    private int writer;
    private BOARD_TYPE type;
    private USER_TYPE userType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getWriter() {
        return writer;
    }

    public void setWriter(int writer) {
        this.writer = writer;
    }

    public BOARD_TYPE getType() {
        return type;
    }

    public void setType(BOARD_TYPE type) {
        this.type = type;
    }

    public USER_TYPE getUserType() {
        return userType;
    }

    public void setUserType(USER_TYPE userType) {
        this.userType = userType;
    }
}
