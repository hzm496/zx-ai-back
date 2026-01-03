package com.zm.zx.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 回复反馈DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyFeedbackDTO {
    /**
     * 回复内容
     */
    private String reply;
}


