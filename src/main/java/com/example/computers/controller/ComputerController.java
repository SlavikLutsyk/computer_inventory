package com.example.computers.controller;

import com.example.computers.model.Component;
import com.example.computers.model.Computer;
import com.example.computers.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/computers")
public class ComputerController {
    private final InventoryService inventoryService;

    @Autowired
    public ComputerController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<Computer>> getComputers() {
        List<Computer> computers;
        try {
            computers = inventoryService.getAllComputers();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(computers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Computer> getComputerById(@PathVariable String id) {
        Computer computer;
        try {
            computer = inventoryService.getComputerByID(id);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(computer);
    }

    @PostMapping
    public ResponseEntity<Computer> addComputer(@RequestBody Computer computer) {
        Computer createdComputer;
        try {
            createdComputer = inventoryService.addComputer(computer);
            System.out.println(createdComputer);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.status(201).body(createdComputer);
    }

    @PutMapping("/assign-component")
    public ResponseEntity<Component> assignComponent(@RequestParam String computerId, @RequestParam String componentId) {
        Component updatedComponent;
        try {
            updatedComponent = inventoryService.assignComponent(computerId, componentId);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok(updatedComponent);
    }

    @PutMapping("/remove-component")
    public ResponseEntity<Component> removeComponent(@RequestParam String computerId, @RequestParam String componentId) {
        Component updatedComponent;
        try {
            updatedComponent = inventoryService.removeComponent(computerId, componentId);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok(updatedComponent);
    }
}
