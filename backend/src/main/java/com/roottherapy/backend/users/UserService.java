package com.roottherapy.backend.users;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;


// basic UserService class scaffolding
// more to add later!
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User has not been found using id"));
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User has not been found using email"));
    }

    public boolean emailExists(String email){
        return userRepository.existsByEmailIgnoreCase(email);
    }

    // return UserResponse Record in the future,
    // done to avoid exposing the passwordHash over the API responses
    public User save(User user){
        return userRepository.save(user);
    }
}
