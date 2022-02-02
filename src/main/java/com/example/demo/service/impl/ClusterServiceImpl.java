/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service.impl;

import com.example.demo.entity.Cluster;
import com.example.demo.repository.ClusterRepository;
import com.example.demo.service.ClusterService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author bipin.gowda
 */

@Service
public class ClusterServiceImpl implements ClusterService{
    
    @Autowired
    ClusterRepository clusterRepository;
    
    public List<Cluster> getAllCluster(){
        return clusterRepository.findAll();
    }
    
    public void addNew(Cluster c){
        clusterRepository.save(c);
    }
}
