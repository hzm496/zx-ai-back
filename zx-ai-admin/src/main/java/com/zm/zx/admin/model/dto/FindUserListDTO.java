package com.zm.zx.admin.model.dto;

import com.zm.zx.common.model.BaseQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUserListDTO extends BaseQuery {
    private String username;
    private Integer isVip;
    private Integer gender;
    private Integer status;
}
