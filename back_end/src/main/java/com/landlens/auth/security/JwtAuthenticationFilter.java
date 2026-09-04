package com.landlens.auth.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.landlens.auth.model.Role;
import com.landlens.auth.repository.RoleRepository;
import com.landlens.user.model.User;
import com.landlens.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                // 1. First, attempt to validate as internal HMAC-signed LandLens JWT
                if (tokenProvider.validateToken(jwt)) {
                    String userId = tokenProvider.getUserIdFromJWT(jwt);
                    String role = tokenProvider.getRoleFromJWT(jwt);
                    setAuthentication(request, userId, role);
                } else {
                    // 2. If not an internal JWT, attempt to verify as Google Firebase ID token
                    try {
                        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(jwt);
                        if (decodedToken != null) {
                            String email = decodedToken.getEmail();
                            String uid = decodedToken.getUid();

                            // Determine user entity and role from Firestore
                            Optional<User> userOpt = Optional.empty();
                            if (email != null && !email.trim().isEmpty()) {
                                userOpt = userRepository.findByEmail(email);
                            }

                            // Also try searching by UID if UID is a valid UUID
                            if (userOpt.isEmpty()) {
                                try {
                                    UUID uuidFromUid = UUID.fromString(uid);
                                    userOpt = userRepository.findById(uuidFromUid);
                                } catch (IllegalArgumentException ignored) {}
                            }

                            String resolvedUserId;
                            String resolvedRole = null;

                            // Extract role claim if present in Firebase token
                            Object roleClaim = decodedToken.getClaims().get("role");
                            if (roleClaim != null) {
                                resolvedRole = roleClaim.toString();
                            }

                            if (userOpt.isPresent()) {
                                User user = userOpt.get();
                                resolvedUserId = user.getId().toString();
                                if (resolvedRole == null && user.getRole() != null) {
                                    resolvedRole = user.getRole().getName();
                                }
                            } else {
                                // Auto-provision user in Firestore if not existing yet
                                User newUser = new User();
                                UUID newId;
                                try {
                                    newId = UUID.fromString(uid);
                                } catch (Exception e) {
                                    newId = UUID.randomUUID();
                                }
                                newUser.setId(newId);
                                newUser.setEmail(email != null ? email : uid + "@firebase.user");
                                String displayName = decodedToken.getName();
                                if (displayName != null && !displayName.trim().isEmpty()) {
                                    String[] parts = displayName.split(" ", 2);
                                    newUser.setFirstName(parts[0]);
                                    newUser.setLastName(parts.length > 1 ? parts[1] : "");
                                } else {
                                    newUser.setFirstName("Provider");
                                    newUser.setLastName("User");
                                }

                                if (resolvedRole == null) {
                                    resolvedRole = "PROVIDER";
                                }

                                final String finalRole = resolvedRole;
                                Role roleEntity = roleRepository.findByName(finalRole.toUpperCase())
                                        .orElseGet(() -> {
                                            Role r = new Role();
                                            r.setName(finalRole.toUpperCase());
                                            r.setDescription("System Role: " + finalRole);
                                            return roleRepository.save(r);
                                        });
                                newUser.setRole(roleEntity);
                                User saved = userRepository.save(newUser);
                                resolvedUserId = saved.getId().toString();
                            }

                            if (resolvedRole == null) {
                                resolvedRole = "PROVIDER";
                            }

                            setAuthentication(request, resolvedUserId, resolvedRole);
                            log.debug("Successfully authenticated Firebase user {} with role {}", email, resolvedRole);
                        }
                    } catch (Exception firebaseEx) {
                        log.warn("Bearer token is neither valid LandLens JWT nor valid Firebase ID Token: {}", firebaseEx.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(HttpServletRequest request, String userId, String role) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (role != null) {
            String upperRole = role.toUpperCase();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + upperRole));
            // Interoperability between PROVIDER and SELLER
            if ("PROVIDER".equals(upperRole)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
            } else if ("SELLER".equals(upperRole)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_PROVIDER"));
            }
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userId, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
