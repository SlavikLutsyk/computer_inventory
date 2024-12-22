package com.example.computers.controller;

import com.example.computers.model.Component;
import com.example.computers.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/components")
public class ComponentController {
    private final InventoryService inventoryService;

    @Autowired
    public ComponentController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<Component>> getComponents() {
        List<Component> components;
        try {
            components = inventoryService.getAllComponents();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(components);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Component> getComponentById(@PathVariable String id) {
        Component component;
        try {
            component = inventoryService.getComponentByID(id);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(component);
    }

    @GetMapping("/type")
    public ResponseEntity<List<Component>> getComponentsByType(@RequestParam String type) {
        List<Component> components;
        try {
            components = inventoryService.getComponentByType(type);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(components);
    }

    @GetMapping("/manufacturer")
    public ResponseEntity<List<Component>> getComponentsByManufacturer(@RequestParam String manufacturer) {
        List<Component> components;
        try {
            components = inventoryService.getComponentByManufacturer(manufacturer);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(components);
    }

    @GetMapping("/free")
    public ResponseEntity<List<Component>> getFreeComponents(@RequestParam boolean free) {
        List<Component> components;
        try {
            components = inventoryService.getComponentByFree(free);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(components);
    }

    @PostMapping
    public ResponseEntity<Component> addComponent(@RequestBody Component component) {
        Component createdComponent;
        try {
            createdComponent = inventoryService.addComponent(component);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.status(201).body(createdComponent);
    }
}
