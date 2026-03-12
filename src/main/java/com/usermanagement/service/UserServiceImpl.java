package com.usermanagement.service;
import com.usermanagement.dto.CreateUserRequest;
import com.usermanagement.dto.UpdateUserRequest;
import com.usermanagement.dto.UserResponse;
import com.usermanagement.entity.User;
import com.usermanagement.exception.EmailAlreadyExistsException;
import com.usermanagement.exception.UserNotFoundException;
import com.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword()) // plain text for now — we'll hash in Phase 5.3 (Security)
                .role(request.getRole())
                .active(true)
                .build();
        User saved = userRepository.save(user);
        log.info("User created with id: {}", saved.getId());
        return UserResponse.from(saved);
    }
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserResponse.from(user);
    }
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return UserResponse.from(user);
    }
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }
    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole());
        user.setActive(request.getActive());
        User updated = userRepository.save(user);
        log.info("User updated with id: {}", updated.getId());
        return UserResponse.from(updated);
    }
    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setActive(false); // soft delete — never hard delete users
        userRepository.save(user);
        log.info("User soft-deleted with id: {}", id);
    }
}