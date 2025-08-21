package com.saisai.domain.challenge.controller;

import static com.saisai.domain.common.response.SuccessCode.CHALLENGE_CREATE_SUCCESS;

import com.saisai.domain.challenge.dto.request.CreateChallengeReq;
import com.saisai.domain.challenge.dto.response.CreateChallengeRes;
import com.saisai.domain.challenge.service.AdminChallengeService;
import com.saisai.domain.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ADMIN 전용 API")
@RestController
@RequestMapping("/api/admin/challenges")
@RequiredArgsConstructor
public class AdminChallengeController {

    private final AdminChallengeService adminChallengeService;

    @Operation(summary = "챌린지 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<CreateChallengeRes>> createChallenges(
        @Valid @RequestBody CreateChallengeReq request
    ) {
        CreateChallengeRes response = adminChallengeService.createChallenges(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(CHALLENGE_CREATE_SUCCESS, response));
    }
}
