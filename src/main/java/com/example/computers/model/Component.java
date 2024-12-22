package com.example.computers.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "components")
public class Component {
    @Id
    private String id;
    private String name;
    private String manufacturer;
    private String type;
    private boolean isFree;
    private String computerId;

    public Component() {}
    public Component(String name, String manufacturer, String type) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.type = type;
        this.isFree = true;
    }

    public String getName() {
        return name;
    }

    public boolean isFree() {
        return isFree;
    }

    public String getType() {
        return type;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getComputerId() {
        return computerId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setComputerId(String computerId) {
        this.computerId = computerId;
    }
}
