package com.geekwims.hereiam.response;

import com.geekwims.hereiam.constant.BOARD_TYPE;
import com.geekwims.hereiam.constant.USER_TYPE;

import java.io.Serializable;
import java.sql.Timestamp;


public class BoardResponse implements Serializable {
    private int id;
    private String title;
    private String content;
    private int writer;
    private Timestamp createdAt;
    private BOARD_TYPE type;
    private USER_TYPE userType;
    private String writerName;

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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
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

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }
}
