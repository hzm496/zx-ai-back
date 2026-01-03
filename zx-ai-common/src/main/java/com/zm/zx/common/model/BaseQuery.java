package com.zm.zx.common.model;

import lombok.Data;

@Data
public class BaseQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
