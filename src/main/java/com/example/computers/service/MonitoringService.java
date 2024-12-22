package com.example.computers.service;

import com.example.computers.model.Component;
import com.example.computers.model.Computer;
import com.example.computers.repository.ComponentRepository;
import com.example.computers.repository.ComputerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MonitoringService {
    private final ComputerRepository computerRepository;
    private final ComponentRepository componentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public MonitoringService(ComputerRepository computerRepository, ComponentRepository componentRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.computerRepository = computerRepository;
        this.componentRepository = componentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    private <T> T findByIdOrThrow(Optional<T> optional, String errorMessage) {
        return optional.orElseThrow(() -> new IllegalArgumentException(errorMessage));
    }

    @KafkaListener(topics = "add-component", groupId = "add-component-group")
    public void handleAddComponentRequest(String correlationId, Component component) {
        Component savedComponent = componentRepository.save(component);
        kafkaTemplate.send("inventory-responses", correlationId, savedComponent);
    }

    @KafkaListener(topics = "add-computer", groupId = "add-computer-group")
    public void handleAddComputerRequest(String correlationId, Computer computer) {
        Computer savedComputer = computerRepository.save(computer);
        kafkaTemplate.send("inventory-responses", correlationId, savedComputer);
    }

    @KafkaListener(topics = "get-all-components", groupId = "get-components-group")
    public void handleGetAllComponentsRequest(String correlationId) {
        List<Component> components = componentRepository.findAll();
        kafkaTemplate.send("inventory-responses", correlationId, components);
    }

    @KafkaListener(topics = "get-all-computers", groupId = "get-computers-group")
    public void handleGetAllComputersRequest(String correlationId) {
        List<Computer> computers = computerRepository.findAll();
        kafkaTemplate.send("inventory-responses", correlationId, computers);
    }

    @KafkaListener(topics = "get-component-by-id", groupId = "get-component-by-id-group")
    public void handleGetComponentByIdRequest(String correlationId, String id) {
        Component component = findByIdOrThrow(componentRepository.findById(id), "Component not found with ID: " + id);
        kafkaTemplate.send("inventory-responses", correlationId, component);
    }

    @KafkaListener(topics = "get-computer-by-id", groupId = "get-computer-by-id-group")
    public void handleGetComputerByIdRequest(String correlationId, String id) {
        Computer computer = findByIdOrThrow(computerRepository.findById(id), "Computer not found with ID: " + id);
        kafkaTemplate.send("inventory-responses", correlationId, computer);
    }

    @KafkaListener(topics = "get-component-by-type", groupId = "get-component-by-type-group")
    public void handleGetComponentByTypeRequest(String correlationId, String type) {
        List<Component> components = componentRepository.findByType(type);
        kafkaTemplate.send("inventory-responses", correlationId, components);
    }

    @KafkaListener(topics = "get-component-by-manufacturer", groupId = "get-component-by-manufacturer-group")
    public void handleGetComponentByManufacturerRequest(String correlationId, String manufacturer) {
        List<Component> components = componentRepository.findByManufacturer(manufacturer);
        kafkaTemplate.send("inventory-responses", correlationId, components);
    }

    @KafkaListener(topics = "get-component-by-free", groupId = "get-component-by-free-group")
    public void handleGetComponentByFreeRequest(String correlationId, String freeStatus) {
        boolean free = Boolean.parseBoolean(freeStatus);
        List<Component> components = componentRepository.findByFree(free);
        kafkaTemplate.send("inventory-responses", correlationId, components);
    }

    @KafkaListener(topics = "get-component-by-computer", groupId = "get-component-by-computer-group")
    public void handleGetComponentByComputerRequest(String correlationId, String computerID) {
        List<Component> components = componentRepository.findByComputerId(computerID);
        kafkaTemplate.send("inventory-responses", correlationId, components);
    }

    @KafkaListener(topics = "assign-component", groupId = "assign-component-group")
    public void handleAssignComponentRequest(String correlationId, Pair<String, String> data) {
        String computerID = data.getFirst();
        String componentID = data.getSecond();

        Component component = findByIdOrThrow(componentRepository.findById(componentID), "Component not found");
        Computer computer = findByIdOrThrow(computerRepository.findById(computerID), "Computer not found");

        if (!component.isFree()) {
            throw new IllegalStateException("Component isn't free.");
        }

        component.setFree(false);
        component.setComputerId(computerID);
        computer.getComponentIds().add(componentID);

        componentRepository.save(component);
        computerRepository.save(computer);

        kafkaTemplate.send("inventory-responses", correlationId, component);
    }

    @KafkaListener(topics = "remove-component", groupId = "remove-component-group")
    public void handleRemoveComponentRequest(String correlationId, Pair<String, String> data) {
        String computerID = data.getFirst();
        String componentID = data.getSecond();

        Component component = findByIdOrThrow(componentRepository.findById(componentID), "Component not found");
        Computer computer = findByIdOrThrow(computerRepository.findById(computerID), "Computer not found");

        if (component.isFree()) {
            throw new IllegalStateException("Component has to be occupied.");
        }

        component.setFree(true);
        component.setComputerId(null);
        computer.getComponentIds().remove(componentID);

        componentRepository.save(component);
        computerRepository.save(computer);

        kafkaTemplate.send("inventory-responses", correlationId, component);
    }
}
