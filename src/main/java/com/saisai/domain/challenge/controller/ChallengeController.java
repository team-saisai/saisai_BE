package com.saisai.domain.challenge.controller;

import static com.saisai.domain.common.response.SuccessCode.CHALLENGE_POPULAR_LIST_GET_SUCCESS;

import com.saisai.domain.challenge.service.ChallengeService;
import com.saisai.domain.common.response.ApiResponse;
import com.saisai.domain.course.dto.response.CourseCardRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "챌린지 API")
@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @Operation(summary = "인기 챌린지 리스트 조회",
        description =
            "10개의 인기 챌린지 정보 (코스명, 코스이미지, 난이도, 거리(km), 예상 소요시간(분), 시군, 도전자 수, 챌린지 상태, 챌린지 종료일) 리스트로 반환")
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<CourseCardRes>>> getPopularChallenges() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(CHALLENGE_POPULAR_LIST_GET_SUCCESS, challengeService.getPopularChallenges()));
    }
}
