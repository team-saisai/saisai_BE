package com.saisai.domain.ride.constant;

import static com.saisai.domain.common.exception.ExceptionCode.INVALID_SORT_OPTION;
import static com.saisai.domain.ride.entity.QRide.ride;

import com.querydsl.core.types.OrderSpecifier;
import com.saisai.domain.common.exception.CustomException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum RideSortOption {

    NEWEST("newest", "modifiedAt", Sort.Direction.DESC) {
        @Override
        public OrderSpecifier<?> toOrderSpecifier() {
            return ride.modifiedAt.desc();
        }
    },
    OLDEST("oldest", "modifiedAt", Sort.Direction.ASC) {
        @Override
        public OrderSpecifier<?> toOrderSpecifier() {
            return ride.modifiedAt.asc();
        }
    },

    ;

    private final String key;
    private final String sortColumn;
    private final Sort.Direction direction;

    // QueryDSL OrderSpecifier 추상 메서드
    public abstract OrderSpecifier<?> toOrderSpecifier();

    public static RideSortOption from(String key) {
        return Arrays.stream(values())
            .filter(opt -> opt.key.equalsIgnoreCase(key))
            .findFirst()
            .orElseThrow(() -> new CustomException(INVALID_SORT_OPTION));
    }
}
