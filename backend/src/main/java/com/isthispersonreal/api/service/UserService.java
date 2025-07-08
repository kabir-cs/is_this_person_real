package com.isthispersonreal.api.service;

import com.isthispersonreal.api.model.User;
import com.isthispersonreal.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Loads a user by their username for authentication
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
    
    public User createUser(String username, String email, String password, User.Role role) {
        // Creates a new user with the given credentials and role
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User(username, email, passwordEncoder.encode(password), role);
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        // Finds a user by username
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        // Finds a user by email
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findById(Long id) {
        // Finds a user by ID
        return userRepository.findById(id);
    }
    
    public List<User> getAllUsers() {
        // Returns all users in the system
        return userRepository.findAll();
    }
    
    public User updateLastLogin(String username) {
        // Updates the last login timestamp for a user
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            return userRepository.save(user);
        }
        return null;
    }
    
    public boolean existsByUsername(String username) {
        // Checks if a username already exists
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        // Checks if an email already exists
        return userRepository.existsByEmail(email);
    }
    
    public long getAdminCount() {
        // Returns the number of admin users
        return userRepository.countAdmins();
    }
    
    public List<User> getActiveUsers() {
        // Returns users ordered by last login
        return userRepository.findActiveUsersOrderByLastLogin();
    }
    
    public User updateUserRole(Long userId, User.Role newRole) {
        // Updates the role of a user
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(newRole);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }
    
    public void deleteUser(Long userId) {
        // Deletes a user by ID
        userRepository.deleteById(userId);
    }
} 