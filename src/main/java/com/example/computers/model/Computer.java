package com.example.computers.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "computers")
public class Computer {
    @Id
    private String id;
    private String name;
    private List<String> componentIds = new ArrayList<>();

    public Computer(){}

    public Computer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getComponentIds() {
        return componentIds;
    }
}
