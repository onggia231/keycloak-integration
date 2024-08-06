package com.devteria.profile.service;

import com.devteria.profile.dto.identity.Credential;
import com.devteria.profile.dto.identity.TokenExchangeParam;
import com.devteria.profile.dto.identity.UserCreationParam;
import com.devteria.profile.dto.request.RegistrationRequest;
import com.devteria.profile.dto.response.ProfileResponse;
import com.devteria.profile.mapper.ProfileMapper;
import com.devteria.profile.repository.IdentityClient;
import com.devteria.profile.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileService {
    ProfileRepository profileRepository;
    ProfileMapper profileMapper;
    IdentityClient identityClient;

    @Value("${idp.client-id}")
    @NonFinal
    private String clientId;

    @Value("${idp.client-secret}")
    @NonFinal
    private String clientSecret;

    public List<ProfileResponse> getAllProfiles() {
        var profiles = profileRepository.findAll();
        return profiles.stream().map(profileMapper::toProfileResponse).toList();
    }

    public ProfileResponse register(RegistrationRequest request) {
        // Create account in KeyCloak

        // Exchange client Token
        var token = identityClient.exchangeToken(TokenExchangeParam.builder()
                .grant_type("client_credentials")
                .client_id(clientId)
                .client_secret(clientSecret)
                .scope("openid")
                .build());

        log.info("TokenInfo {}", token);

        // Create user with client Token and given info

        // Get userId of KeyCloak account
        var creationResponse = identityClient.createUser(
                "Bearer " + token.getAccessToken(),
                UserCreationParam.builder()
                        .username(request.getUsername())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .enabled(true) // de true thi user dang nhap duoc ngay
                        .emailVerified(false) // xac dinh email da verify chua
                        .credentials(List.of(Credential.builder()
                                .type("password")
                                .temporary(false) // ko can doi pass ma dung luon
                                .value(request.getPassword())
                                .build()))
                        .build());

        String userId = extractuserId(creationResponse);
        log.info("UserId {}", userId);

        var profile = profileMapper.toProfile(request);
        profile.setUserId(userId);
        profile = profileRepository.save(profile);

        return profileMapper.toProfileResponse(profile);
    }

    private String extractuserId(ResponseEntity<?> response) {
        String location = response.getHeaders().getFirst("Location");
        String[] splitedStr = location.split("/");
        return splitedStr[splitedStr.length - 1];
    }
}
