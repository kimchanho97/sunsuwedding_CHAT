package com.sunsuwedding.chat.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponse<T> {

    private List<T> data;
    private boolean hasNext; // 다음 페이지가 있는지 여부
}

