package com.example.mirry.chat.bean;

import android.widget.ImageView;

import java.io.File;

/**
 * Created by Mirry on 2017/3/8.
 */

public class Me {
    private String name;
    private String id;
    private int type;
    private String msg;
    private File img;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getImg() {
        return img;
    }

    public void setImg(File img) {
        this.img = img;
    }
}
