package com.saisai.domain.reward.controller;

import static com.saisai.domain.common.response.SuccessCode.REWARD_EVENT_CREATE_SUCCESS;

import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.reward.dto.request.RewardEventReq;
import com.saisai.domain.reward.service.RewardEventService;
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
@RequestMapping("/api/admin/reward-events")
@RequiredArgsConstructor
public class RewardEventController {

    private final RewardEventService rewardEventService;

    @Operation(summary = "리워드 이벤트 등록", description = "시간 형식 (yyyy-MM-ddTHH:mm:ss)\n\n type - MULTIPLIER(배수 이벤트, 기본값), BONUS_FIXED(더하기 이벤트)")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createRewardEvent(
        @Valid @RequestBody RewardEventReq rewardEventReq
    ) {
        rewardEventService.createRewardEvent(rewardEventReq);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(REWARD_EVENT_CREATE_SUCCESS));
    }

}
