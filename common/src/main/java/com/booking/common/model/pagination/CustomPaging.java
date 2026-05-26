package com.booking.common.model.pagination;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Represents paging parameters for paginated API requests.
 */
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CustomPaging {

    @Min(value = 1, message = "Page number must be bigger than 0")
    private Integer pageNumber;

    @Min(value = 1, message = "Page size must be bigger than 0")
    private Integer pageSize;

    /**
     * Zero-based page index for Spring Data {@link org.springframework.data.domain.PageRequest}.
     * Do not use as a JSON property — {@link #pageNumber} is always 1-based in API payloads.
     */
    public int zeroBasedPageIndex() {
        return pageNumber - 1;
    }

}
