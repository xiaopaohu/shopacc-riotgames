package com.shopacc.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountFilterRequest {

    private String gameType;  // Thay vì categoryId
    private String status;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String keyword;

    // Pagination
    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 20;

    @Builder.Default
    private String sortBy = "listedAt";

    @Builder.Default
    private String sortDirection = "DESC";
}