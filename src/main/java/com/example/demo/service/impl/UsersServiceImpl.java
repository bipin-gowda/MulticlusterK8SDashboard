/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service.impl;

import com.example.demo.entity.Cluster;
import com.example.demo.entity.Users;
import com.example.demo.repository.ClusterRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.UsersService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author bipin.gowda
 */

@Service
public class UsersServiceImpl implements UsersService{
    
    @Autowired
    UsersRepository usersRepository;
    
    public String authenticate(String username, String password){
        
        List<Users> users = usersRepository.findAll();
        for(int i=0; i<users.size(); i++){
            if(users.get(i).getUsername().equals(username)){
                if(users.get(i).getPassword().equals(password)){
                    return "Success";
                }
            }
        }
        return "Failed";
        
    }

    
    
    
    
}
