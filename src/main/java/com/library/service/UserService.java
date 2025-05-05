package com.library.service;

import com.library.model.User;
import com.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> findUserById(String id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> findUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    public User saveUser(User user) {
        // Set creation date if it's a new user
        if (user.getId() == null) {
            
        }
        return userRepository.save(user);
    }
    
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
    
    public boolean authenticateUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                // Update last login time
                user.setLastLogin(new Date());
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
    
    public long countUsers() {
        return userRepository.count();
    }
    
    public long countUsersByRole(User.Role role) {
        return userRepository.countByRole(role);
    }
}
