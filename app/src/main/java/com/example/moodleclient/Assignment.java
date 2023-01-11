package com.example.moodleclient;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Assignment implements Serializable {
    private String id;
    private String name;

    public Assignment() {
    }

    public Assignment(String id, String name) {
        this.id = id;
        this.name = name;
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
    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("name", name);
        return result;
    }
}
