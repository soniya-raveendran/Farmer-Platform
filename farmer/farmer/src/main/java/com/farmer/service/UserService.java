package com.farmer.service;

import com.farmer.request.RegisterRequest;
import com.farmer.entity.User;

public interface UserService {

    User register(RegisterRequest request);

    User login(String email, String password);
}
