package com.devteria.profile.repository;

import com.devteria.profile.dto.identity.TokenExchangeParam;
import com.devteria.profile.dto.identity.TokenExchangeResponse;
import com.devteria.profile.dto.identity.UserCreationParam;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IdentityClient {
    @PostMapping(value = "/realms/devteria/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponse exchangeToken(@QueryMap TokenExchangeParam param);

    @PostMapping(value = "/admin/realms/devteria/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(
            @RequestHeader("authorization") String token,
            @RequestBody UserCreationParam param);
    // Dung ResponseEntity vi api tao user cua KeyCloak khong tra ra cai gi
    // Nhung lai tra thong tin ve qua muc Headers -> Location (Xem tren postman)
    // Location tra ve dang: http://localhost:8180/admin/realms/devteria/users/8e25d7b4-a604-4a4b-b694-c087864b557e
    // -> userId: 8e25d7b4-a604-4a4b-b694-c087864b557e
    // -> Nen dung ResponseEntity moi lay thong tin kia ve duoc
}
