package com.devteria.profile.exception;

import com.devteria.profile.dto.identity.KeyCloakError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class ErrorNormalizer {
    private final ObjectMapper objectMapper;
    private final Map<String, ErrorCode> errorCodeMap;

    public ErrorNormalizer() {
        objectMapper = new ObjectMapper();
        errorCodeMap = new HashMap<>();

        // Cac: User exists with same username, User exists with same email, User name is missing la loi KeyCloak tra ve
        // ErrorCode.USER_EXISTED, ErrorCode.EMAIL_EXISTED, ErrorCode.USERNAME_IS_MISSING la loi custom minh muon tra response
        errorCodeMap.put("User exists with same username", ErrorCode.USER_EXISTED);
        errorCodeMap.put("User exists with same email", ErrorCode.EMAIL_EXISTED);
        errorCodeMap.put("User name is missing", ErrorCode.USERNAME_IS_MISSING);
    }

    public AppException handleKeyCloakException(FeignException exception) {

        try {
            log.warn("Cannot complete request", exception);
            var response = objectMapper.readValue(exception.contentUTF8(), KeyCloakError.class);

            // Kiem tra xem ma loi co null ko va co nam trong Map khong
            if (Objects.nonNull(response.getErrorMessage()) && Objects.nonNull(errorCodeMap.get(response.getErrorMessage()))) {
                return new AppException(errorCodeMap.get(response.getErrorMessage()));
            }
        } catch (JsonProcessingException e) {
            log.error("Cannot deserialize content", e);
        }

        return new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
    }

}
