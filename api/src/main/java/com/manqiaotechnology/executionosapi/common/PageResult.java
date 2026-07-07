package com.manqiaotechnology.executionosapi.common;

import java.util.List;

public record PageResult<T>(List<T> records, long total, long pageNo, long pageSize) {

    public static <T> PageResult<T> of(List<T> records, long pageNo, long pageSize) {
        return new PageResult<>(records, records.size(), pageNo, pageSize);
    }
}
