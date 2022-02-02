/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author bipin.gowda
 */

@Document(collection = "Cluster")
public class Cluster {
    
    private String name;
    private String config;
    private String location;
    private String type;
    private String prefecture;
    private double latitude;
    private double longitude;
    private String group;
    
    @Id
    private String id;
    
    public String getId() {
        return id;
    }
    
    public String getConfig() {
        return config;
    }
    
    public String getName() {
        return name;
    }
    
    public String getLocation() {
        return location;
    }
    
    public String getType() {
        return type;
    }
    
    public String getPrefecture() {
        return prefecture;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public String getGroup() {
        return group;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void setConfig(String config) {
        this.config = config;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setPrefecture(String prefecture) {
        this.prefecture = prefecture;
    }
    
    public void setlatitude(String latitude) {
        this.latitude = Double.valueOf(latitude);
    }
    
    public void setLongitude(String longitude) {
        this.longitude = Double.valueOf(longitude);
    }
    
    public void setGroup(String group) {
        this.group = group;
    }
    
    public Cluster(String id, String name, String config, String location, String type, String prefecture, double latitude, double longitude, String group){
        super();
        this.id = id;
        this.name = name;
        this.config = config;
        this.location = location;
        this.type = type;
        this.prefecture = prefecture;
        this.latitude = latitude;
        this.longitude = longitude;
        this.group = group;
    }
}

