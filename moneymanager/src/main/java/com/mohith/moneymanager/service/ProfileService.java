package com.mohith.moneymanager.service;

import com.mohith.moneymanager.dto.AuthDto;
import com.mohith.moneymanager.dto.ProfileDto;
import com.mohith.moneymanager.entity.ProfileEntity;
import com.mohith.moneymanager.repository.ProfileRepository;
import com.mohith.moneymanager.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Value("${backend.url}")
    private String backendUrl;

    public ProfileDto registerProfile(ProfileDto profileDto){
        ProfileEntity newProfile = toEntity(profileDto);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);
        String activationLink = backendUrl + "/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Active your money manager account";
        String body = "Click on the link to activate your account : " + activationLink;
        emailService.sendEmail(newProfile.getEmail(), subject,body);
        return toDto(newProfile);
    }

    private ProfileEntity toEntity(ProfileDto profileDto){
        return ProfileEntity.builder()
                .id(profileDto.getId())
                .fullName(profileDto.getFullName())
                .email(profileDto.getEmail())
                .password(passwordEncoder.encode(profileDto.getPassword()))
                .createdAt(profileDto.getCreatedAt())
                .updatedAt(profileDto.getUpdatedAt())
                .build();
    }

    public  ProfileDto toDto(ProfileEntity profileEntity){
        return ProfileDto.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public Boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                }).orElse(false);
    }

    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email"+authentication.getName()));
    }

    public ProfileDto getPublicProfile(String email){
        ProfileEntity currentProfile = null;
        if(email == null){
            currentProfile = getCurrentProfile();
        }else {
            currentProfile = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email"+email));
        }
        return toDto(currentProfile);
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDto authDto) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(),authDto.getPassword()));
            // Generate JWT Tokens
            String token = jwtUtils.generateToken(authDto.getEmail());

            return Map.of(
                    "token",token,
                    "user",getPublicProfile(authDto.getEmail())
            );
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }
}




















