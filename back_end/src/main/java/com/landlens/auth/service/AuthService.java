package com.landlens.auth.service;

import com.landlens.auth.dto.LoginRequest;
import com.landlens.auth.dto.RefreshTokenRequest;
import com.landlens.auth.dto.RegisterRequest;
import com.landlens.auth.dto.TokenResponse;
import com.landlens.auth.model.LoginHistory;
import com.landlens.auth.model.RefreshToken;
import com.landlens.auth.model.Role;
import com.landlens.auth.repository.LoginHistoryRepository;
import com.landlens.auth.repository.RefreshTokenRepository;
import com.landlens.auth.repository.RoleRepository;
import com.landlens.auth.security.JwtTokenProvider;
import com.landlens.user.model.User;
import com.landlens.user.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import com.landlens.common.exception.ResourceNotFoundException;
import com.landlens.common.exception.InvalidRequestException;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Value("${landlens.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Value("${firebase.api.key:#{null}}")
    private String firebaseApiKey;

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidRequestException("Email already in use");
        }

        Role role = roleRepository.findByName(request.getRole().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRole()));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(role);

        User savedUser = userRepository.save(user);

        try {
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(savedUser.getEmail())
                    .setPassword(request.getPassword())
                    .setDisplayName(savedUser.getFirstName() + " " + savedUser.getLastName())
                    .setUid(savedUser.getId().toString());
            FirebaseAuth.getInstance().createUser(createRequest);
        } catch (Exception e) {
            // Clean up created user in Firestore if Firebase registration fails to keep consistency
            userRepository.delete(savedUser);
            throw new RuntimeException("Failed to register user in Firebase Authentication: " + e.getMessage(), e);
        }

        return savedUser;
    }

    public TokenResponse login(LoginRequest request, String ipAddress, String userAgent) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            throw new InvalidRequestException("Bad credentials");
        }

        User user = userOpt.get();

        try {
            verifyFirebaseCredentials(request.getEmail(), request.getPassword());
        } catch (InvalidRequestException ex) {
            LoginHistory loginHistory = new LoginHistory();
            loginHistory.setUser(user);
            loginHistory.setIpAddress(ipAddress);
            loginHistory.setUserAgent(userAgent);
            loginHistory.setStatus("FAILED");
            loginHistoryRepository.save(loginHistory);
            throw ex;
        }

        // Save successful login history
        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setUser(user);
        loginHistory.setIpAddress(ipAddress);
        loginHistory.setUserAgent(userAgent);
        loginHistory.setStatus("SUCCESS");
        loginHistoryRepository.save(loginHistory);

        // Generate Access Token
        String accessToken = tokenProvider.generateToken(user);

        // Generate Refresh Token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));
        refreshTokenRepository.save(refreshToken);

        return new TokenResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                user.getRole().getName(),
                user.getId()
        );
    }

    private void verifyFirebaseCredentials(String email, String password) {
        if (firebaseApiKey == null || firebaseApiKey.trim().isEmpty()) {
            throw new RuntimeException("Firebase API Key is not configured in application properties.");
        }
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + firebaseApiKey;
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);
        requestBody.put("password", password);
        requestBody.put("returnSecureToken", true);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestBody, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return;
            }
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().value() == 400) {
                throw new InvalidRequestException("Bad credentials");
            }
            throw new RuntimeException("Error verifying credentials with Firebase REST API: " + ex.getResponseBodyAsString(), ex);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to Firebase Authentication service", e);
        }
        throw new InvalidRequestException("Bad credentials");
    }

    public TokenResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidRequestException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidRequestException("Refresh token expired");
        }

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw new InvalidRequestException("Refresh token revoked");
        }

        User user = refreshToken.getUser();
        String accessToken = tokenProvider.generateToken(user);

        return new TokenResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                user.getRole().getName(),
                user.getId()
        );
    }

    public void logout(String refreshTokenStr) {
        refreshTokenRepository.findByToken(refreshTokenStr).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    public void forgotPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found registered with email: " + email));

        try {
            UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(user.getId().toString())
                    .setPassword(newPassword);
            FirebaseAuth.getInstance().updateUser(updateRequest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reset password in Firebase Authentication: " + e.getMessage(), e);
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}

