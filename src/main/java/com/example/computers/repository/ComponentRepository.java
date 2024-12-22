package com.example.computers.repository;

import com.example.computers.model.Component;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ComponentRepository extends MongoRepository<Component, String> {
    List<Component> findByType(String type);
    List<Component> findByManufacturer(String manufacturer);
    List<Component> findByFree(boolean isFree);
    List<Component> findByComputerId(String computerId);
}
