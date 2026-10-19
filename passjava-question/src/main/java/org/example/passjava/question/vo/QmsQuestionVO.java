package org.example.passjava.question.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QmsQuestionVO {
    private Long id;
    private String title;
    private Long type;
    private String typeName;
    private Integer level;
}
