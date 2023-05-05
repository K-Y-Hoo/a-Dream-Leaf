package com.DreamCoder.DreamLeaf.controller;

import com.DreamCoder.DreamLeaf.Util.AuthUtil;
import com.DreamCoder.DreamLeaf.dto.SignUpCreateDto;
import com.DreamCoder.DreamLeaf.dto.SignUpDto;
import com.DreamCoder.DreamLeaf.req.SignUpCreateReq;
import com.DreamCoder.DreamLeaf.service.SignUpService;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SignUpController {

    @Autowired
    private final SignUpService signUpService;
    private final AuthUtil authUtil;

    @PostMapping("/login/signUp")
    public ResponseEntity createAccount(@RequestBody SignUpCreateReq signUpCreateReq) throws FirebaseAuthException {
        String firebaseToken = signUpCreateReq.getFirebaseToken();
        int id = authUtil.findUserId(firebaseToken);
        SignUpCreateDto signUpCreateDto = new SignUpCreateDto(signUpCreateReq.getEmail(), id);
        SignUpDto signUpDto = signUpService.create(signUpCreateDto);
        return ResponseEntity.status(201).body(signUpDto);
    }
}