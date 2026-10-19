package org.example.passjava.question.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> {
    private long current;
    private long size;
    private long total;
    private long pages;
    private List<T> records;
}
