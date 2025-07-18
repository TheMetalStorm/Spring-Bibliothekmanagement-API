package com.themetalstorm.bibliothekssystem.service;

import com.themetalstorm.bibliothekssystem.model.User;
import com.themetalstorm.bibliothekssystem.model.UserPrincipal;
import com.themetalstorm.bibliothekssystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.themetalstorm.bibliothekssystem.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MyUserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    @Autowired
    public MyUserService(UserRepository userRepository, JWTService jwtService, @Lazy AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User byUsername = userRepository.findByUsername(username);
        if(byUsername == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserPrincipal(byUsername);
    }

    public User loadWholeUserByUsername(String username)  {
        User byUsername = userRepository.findByUsername(username);
        if(byUsername == null) {
            throw new UsernameNotFoundException(username);
        }
        return byUsername;
    }
    public User register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    public String verify(User user) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            User fromDB = userRepository.findByUsername(user.getUsername());
            return jwtService.generateToken(fromDB);
        } else {
            return "fail";
        }
    }

    @Transactional
    public User updateUser(int id, User userDetails) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setUsername(userDetails.getUsername());
        user.setPassword(encoder.encode(userDetails.getPassword()));
        user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }

    public void deletAll() {
        userRepository.deleteAll();
    }

    @Transactional
    public String deleteByName(String username) {
        if(userRepository.existsByUsername(username)){
            userRepository.deleteByUsername(username);
            return "success";
        }
        return "fail";
    }

    public Page<User> getAllUsers(Integer page, Integer size, String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);

        System.out.println("page = " + page);
        System.out.println("size = " + size);
        if(page == null || size == null) {
            return userRepository.findAll(Pageable.unpaged(sort));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable);
    }

    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                "User not found with id: " + id
        ));
    }

    public boolean existsById(int id) {
        return userRepository.existsById(id);
    }

}
