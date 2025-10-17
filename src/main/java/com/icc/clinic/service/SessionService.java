package com.icc.clinic.service;

import com.icc.clinic.model.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class SessionService {

    private final AtomicReference<User> currentUserRef = new AtomicReference<>();

    public void setCurrentUser(User user) {
        currentUserRef.set(user);
    }

    public User getCurrentUser() {
        return currentUserRef.get();
    }
}



