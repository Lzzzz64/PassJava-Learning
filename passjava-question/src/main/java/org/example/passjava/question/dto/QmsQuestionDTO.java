package org.example.passjava.question.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@TableName("qms_question")
public class QmsQuestionDTO {
    private String title;

    private String answer;

    private Long type;

    @Min(value = 1, message = "等级不能小于1")
    @Max(value = 3, message = "等级不能大于3")
    private Integer level;
}
