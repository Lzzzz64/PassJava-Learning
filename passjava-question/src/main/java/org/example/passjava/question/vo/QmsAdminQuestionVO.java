package org.example.passjava.question.vo;

import lombok.Data;

@Data
public class QmsAdminQuestionVO {
    private Long id;
    private String title;
    private Long type;
    private String typeName;
    private Integer level;
    private Long createBy;
    private String createByName;
}
