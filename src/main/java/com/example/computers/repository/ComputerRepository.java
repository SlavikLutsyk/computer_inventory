package com.example.computers.repository;

import com.example.computers.model.Computer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ComputerRepository  extends MongoRepository<Computer,String> {
}
