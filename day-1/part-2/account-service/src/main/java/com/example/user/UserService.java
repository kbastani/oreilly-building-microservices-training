package com.example.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@Transactional
public class UserService {

    private RestTemplate restTemplate;

    @Value("${user-service.url}")
    private String userServiceUrl;

    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public User getUser(Long userId) {
        User user = null;

        ResponseEntity<User> response =
                restTemplate.getForEntity(URI.create(userServiceUrl + "/users/" + userId), User.class);

        if (response.getStatusCode().is2xxSuccessful())
            user = response.getBody();

        return user;
    }
}
