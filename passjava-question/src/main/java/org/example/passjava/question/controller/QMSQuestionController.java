package org.example.passjava.question.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import org.example.passjava.question.common.Result;
import org.example.passjava.question.dto.QmsQuestionDTO;
import org.example.passjava.question.dto.QmsQuestionQueryDTO;
import org.example.passjava.question.entity.QmsQuestion;
import org.example.passjava.question.service.QmsQuestionService;
import org.example.passjava.question.vo.PageVO;
import org.example.passjava.question.vo.QmsQuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")

public class QMSQuestionController {
    @Autowired
    private QmsQuestionService qmsQuestionService;

    /**
     * 列表查询题目
     * @return
     */
    @GetMapping
    public Result<List<QmsQuestion>> list() {
        return Result.success(qmsQuestionService.list());
    }

    /**
     * 根据id查询题目
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<QmsQuestion> getById(@PathVariable Long id) {
        return Result.success(qmsQuestionService.getById(id));
    /**
     * 创建题目
     * @param qmsQuestionDTO
     * @return
     */
    }

    @PostMapping
    public Result<QmsQuestionDTO> create(@Valid @RequestBody QmsQuestionDTO qmsQuestionDTO) {
        Boolean b = qmsQuestionService.saveQuestion(qmsQuestionDTO);
        if (b)
            return Result.success(qmsQuestionDTO,"添加成功");
        return Result.error(500, "添加失败");
    }

    /**
     * 更新题目
     * @param id
     * @param qmsQuestionDTO
     * @return
     */
    @PutMapping("/{id}")
    public Result<QmsQuestionDTO> update(@PathVariable Long id, @Valid @RequestBody QmsQuestionDTO qmsQuestionDTO) {
        Boolean b = qmsQuestionService.updateQuestion(id, qmsQuestionDTO);
        if (b)
            return Result.success(qmsQuestionDTO,"更新成功");
        return Result.error(500, "更新失败");
    }
    /**
     * 删除题目
     * @param id
     * @return
     */

    @DeleteMapping("/{id}")
    public Result<Long> delete(@PathVariable Long id) {
        boolean b = qmsQuestionService.removeById(id);
        if (b)
            return Result.success(id,"删除成功");
        return Result.error(500, "删除失败");
    }

    /**
     * 分页查询题目
     * @param query
     * @return
     */
    @GetMapping("/page")
    public Result<PageVO<QmsQuestionVO>> page(@Valid @RequestBody QmsQuestionQueryDTO query) {
        return Result.success(qmsQuestionService.page(query),"查询成功");
    }
}
