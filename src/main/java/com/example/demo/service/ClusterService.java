/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.entity.Cluster;
import java.util.List;

/**
 *
 * @author bipin.gowda
 */


public interface ClusterService {
    
    public List<Cluster> getAllCluster();
    public void addNew(Cluster c);
    
  
    
}
