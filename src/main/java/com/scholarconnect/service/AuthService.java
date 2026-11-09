package com.scholarconnect.service;

import com.scholarconnect.dto.AuthResponse;
import com.scholarconnect.dto.LoginRequest;
import com.scholarconnect.dto.RegisterRequest;
import com.scholarconnect.dto.UserSummaryDTO;
import com.scholarconnect.entity.Role;
import com.scholarconnect.entity.StudentProfile;
import com.scholarconnect.entity.User;
import com.scholarconnect.exception.BadRequestException;
import com.scholarconnect.exception.ResourceNotFoundException;
import com.scholarconnect.repository.StudentProfileRepository;
import com.scholarconnect.repository.UserRepository;
import com.scholarconnect.security.JwtTokenProvider;
import com.scholarconnect.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final NotificationService notificationService;

    public AuthService(UserRepository userRepository,
                       StudentProfileRepository profileRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       NotificationService notificationService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.notificationService = notificationService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and confirmation password do not match");
        }

        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("An account with email " + request.getEmail() + " already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setMobileNumber(request.getMobileNumber().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setRole(Role.ROLE_STUDENT);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        StudentProfile profile = new StudentProfile();
        profile.setUser(savedUser);
        profile.setCategory(request.getCategory() != null ? request.getCategory() : "General");
        profile.setState(request.getState() != null ? request.getState() : "");
        profile.setDistrict(request.getDistrict() != null ? request.getDistrict() : "");
        profile.setCollegeName(request.getCollegeName() != null ? request.getCollegeName() : "");
        profile.setUniversityName(request.getUniversityName() != null ? request.getUniversityName() : "");
        profile.setCourse(request.getCourse() != null ? request.getCourse() : "");
        profile.setCurrentYear(request.getCurrentYear() != null ? request.getCurrentYear() : 1);
        profile.setAcademicPercentage(request.getAcademicPercentage() != null ? request.getAcademicPercentage() : java.math.BigDecimal.ZERO);
        profile.setAnnualFamilyIncome(request.getAnnualFamilyIncome() != null ? request.getAnnualFamilyIncome() : java.math.BigDecimal.ZERO);
        profile.setAddress(request.getAddress());

        profileRepository.save(profile);

        // Welcome notification
        notificationService.createNotification(savedUser,
                "Welcome to ScholarConnect!",
                "Your student account has been registered successfully. Explore scholarships and check your eligibility now.");

        // Authenticate & generate token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return new AuthResponse(jwt, savedUser.getId(), savedUser.getFullName(), savedUser.getEmail(), savedUser.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new AuthResponse(jwt, user.getId(), user.getFullName(), user.getEmail(), user.getRole().name());
    }

    @Transactional(readOnly = true)
    public UserSummaryDTO getCurrentUser(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserSummaryDTO dto = new UserSummaryDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setRole(user.getRole().name());
        dto.setEnabled(user.isEnabled());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setGender(user.getGender());
        dto.setCreatedAt(user.getCreatedAt());

        if (user.getStudentProfile() != null) {
            dto.setCategory(user.getStudentProfile().getCategory());
            dto.setCollegeName(user.getStudentProfile().getCollegeName());
            dto.setCourse(user.getStudentProfile().getCourse());
        }

        return dto;
    }
}

