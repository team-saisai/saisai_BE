package com.saisai.domain.course.constant;

import static com.saisai.domain.common.exception.ExceptionCode.INVALID_SORT_OPTION;

import com.saisai.domain.common.exception.CustomException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum CourseSortOption {

    LEVEL_DESC("levelDesc", "level", Sort.Direction.DESC),
    LEVEL_ASC("levelAsc", "level", Sort.Direction.ASC),
    PARTICIPANTS_DESC("participantsDesc", "participants", Sort.Direction.DESC),
    END_SOON("endSoon", "challenge_end_date", Sort.Direction.ASC),

    ;

    private final String key;
    private final String sortColumn;
    private final Sort.Direction direction;

    public static CourseSortOption of(String key) {
        return Arrays.stream(values())
            .filter(opt -> opt.key.equalsIgnoreCase(key))
            .findFirst()
            .orElseThrow(() -> new CustomException(INVALID_SORT_OPTION));
    }
}
