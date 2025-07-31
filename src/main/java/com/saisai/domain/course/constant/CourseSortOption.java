package com.saisai.domain.course.constant;

import static com.saisai.domain.challenge.entity.QChallenge.challenge;
import static com.saisai.domain.common.exception.ExceptionCode.INVALID_SORT_OPTION;
import static com.saisai.domain.course.entity.QCourse.course;
import static com.saisai.domain.ride.entity.QRide.ride;

import com.querydsl.core.types.OrderSpecifier;
import com.saisai.domain.common.exception.CustomException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum CourseSortOption {

    LEVEL_DESC("levelDesc", "level", Sort.Direction.DESC) {
        @Override
        public OrderSpecifier<?> toOrderSpecifier() {
            return course.level.desc();
        }
    },
    LEVEL_ASC("levelAsc", "level", Sort.Direction.ASC) {
        @Override
        public OrderSpecifier<?> toOrderSpecifier() {
            return course.level.asc();
        }
    },
    PARTICIPANTS_DESC("participantsDesc", "participants", Sort.Direction.DESC){
        @Override
        public OrderSpecifier<?> toOrderSpecifier() {
            return ride.count().desc();
        }
    },
    END_SOON("endSoon", "challenge_end_date", Sort.Direction.ASC) {
        @Override
        public OrderSpecifier<?> toOrderSpecifier() {
            return challenge.endedAt.asc();
        }
    },

    ;

    private final String key;
    private final String sortColumn;
    private final Sort.Direction direction;

    // QueryDSL OrderSpecifier 추상 메서드
    public abstract OrderSpecifier<?> toOrderSpecifier();

    public static CourseSortOption from(String key) {
        return Arrays.stream(values())
            .filter(opt -> opt.key.equalsIgnoreCase(key))
            .findFirst()
            .orElseThrow(() -> new CustomException(INVALID_SORT_OPTION));
    }
}
