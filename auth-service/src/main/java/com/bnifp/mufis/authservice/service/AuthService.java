package com.bnifp.mufis.authservice.service;

import com.bnifp.mufis.authservice.dto.input.UserInput;
import com.bnifp.mufis.authservice.dto.response.BaseResponse;
import com.bnifp.mufis.authservice.payload.TokenResponse;
import com.bnifp.mufis.authservice.payload.UsernamePassword;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<BaseResponse> addOne (UserInput userInput);
    TokenResponse generateToken(UsernamePassword req);
}
