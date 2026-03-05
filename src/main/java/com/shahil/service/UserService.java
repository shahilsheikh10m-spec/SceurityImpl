package com.shahil.service;

import com.shahil.model.User;
import com.shahil.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    private static final Logger logger =
            LoggerFactory.getLogger(UserService.class);
    public User register(User user) {
        logger.info("Password before : " + user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.info("Password after : " + passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        return user;
    }

    public String verify(User user){
        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
        if(authentication.isAuthenticated()){
            return jwtService.generateToken(user.getUsername());



        }


        return "Fail";
    }
}
