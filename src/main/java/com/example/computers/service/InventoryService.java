package com.example.computers.service;

import com.example.computers.model.Component;
import com.example.computers.model.Computer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class InventoryService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<Object>> responseMap = new ConcurrentHashMap<>();

    @Autowired
    public InventoryService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private <T> T sendRequestAndWait(String topic, Object message, Class<T> responseType) throws Exception {
        String correlationId = UUID.randomUUID().toString();
        CompletableFuture<Object> future = new CompletableFuture<>();
        responseMap.put(correlationId, future);

        kafkaTemplate.send(topic, correlationId, message);

        try {
            Object response = future.get(60, TimeUnit.SECONDS);
            System.out.println("Response: " + response);
            return responseType.cast(response);
        } finally {
            responseMap.remove(correlationId);
        }
    }

    @KafkaListener(topics = "inventory-responses", groupId = "response-group")
    public void handleResponse(String correlationId, Object response) {
        CompletableFuture<Object> future = responseMap.get(correlationId);
        if (future != null) {
            future.complete(response);
        }
    }

    public Component addComponent(Component component) throws Exception {
        return sendRequestAndWait("add-component", component, Component.class);
    }

    public Computer addComputer(Computer computer) throws Exception {
        return sendRequestAndWait("add-computer", computer, Computer.class);
    }

    public List<Component> getAllComponents() throws Exception {
        return sendRequestAndWait("get-all-components", "", List.class);
    }

    public List<Computer> getAllComputers() throws Exception {
        return sendRequestAndWait("get-all-computers", "", List.class);
    }

    public Component getComponentByID(String id) throws Exception {
        return sendRequestAndWait("get-component-by-id", id, Component.class);
    }

    public Computer getComputerByID(String id) throws Exception {
        return sendRequestAndWait("get-computer-by-id", id, Computer.class);
    }

    public List<Component> getComponentByType(String type) throws Exception {
        return sendRequestAndWait("get-component-by-type", type, List.class);
    }

    public List<Component> getComponentByManufacturer(String manufacturer) throws Exception {
        return sendRequestAndWait("get-component-by-manufacturer", manufacturer, List.class);
    }

    public List<Component> getComponentByFree(boolean free) throws Exception {
        return sendRequestAndWait("get-component-by-free", String.valueOf(free), List.class);
    }

    public List<Component> getComponentByComputer(String computerID) throws Exception {
        return sendRequestAndWait("get-component-by-computer", computerID, List.class);
    }

    public Component assignComponent(String computerID, String componentID) throws Exception {
        return sendRequestAndWait("assign-component", Pair.of(computerID, componentID), Component.class);
    }

    public Component removeComponent(String computerID, String componentID) throws Exception {
        return sendRequestAndWait("remove-component", Pair.of(computerID, componentID), Component.class);
    }
}
