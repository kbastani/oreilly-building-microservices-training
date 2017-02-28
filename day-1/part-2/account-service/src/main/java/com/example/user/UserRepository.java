package com.example.user;

import org.springframework.data.neo4j.repository.GraphRepository;

public interface UserRepository extends GraphRepository<User> {
    User getUserByUserId(Long userId);
}
