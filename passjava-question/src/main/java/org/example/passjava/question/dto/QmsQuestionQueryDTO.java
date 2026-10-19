package org.example.passjava.question.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class QmsQuestionQueryDTO {
    @Min(value = 1, message = "页码不能小于1")
    private Long page = 1L;

    @Min(value = 1, message = "页码不能小于1")
    @Max(value = 100, message = "页码不能大于100")
    private Long size = 10L;

    private Long type;

    @Min(value = 1, message = "等级不能小于1")
    @Max(value = 3, message = "等级不能大于3")
    private Integer level;

    private String keyword;
}