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
@Document(collection = "Users")
public class Users {
    private String username;
    private String password;
    
    @Id
    private String id;

   
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String username) {
        this.password = password;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String username) {
        this.id = id;
    }
    
    public Users(String username, String password){
        super();
        this.username = username;
        this.password = password;
    }
    
}
