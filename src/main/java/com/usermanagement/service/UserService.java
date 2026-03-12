package com.usermanagement.service;
import com.usermanagement.dto.CreateUserRequest;
import com.usermanagement.dto.UpdateUserRequest;
import com.usermanagement.dto.UserResponse;
import java.util.List;
public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse getUserById(Long id);
    UserResponse getUserByEmail(String email);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
}