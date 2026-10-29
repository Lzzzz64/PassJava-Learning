package org.example.passjava.question.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "题目管理", description = "题目分页、详情与管理接口")
public class QMSQuestionController {
    @Autowired
    private QmsQuestionService qmsQuestionService;

    /**
     * 列表查询题目
     * @return
     */
    @GetMapping
    @Operation(summary = "列表查询题目", description = "列表查询题目")
    public Result<List<QmsQuestion>> list() {
        return Result.success(qmsQuestionService.list());
    }

    /**
     * 根据id查询题目
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询题目", description = "根据id查询题目")
    public Result<QmsQuestion> getById(@PathVariable Long id) {
        return Result.success(qmsQuestionService.getQuestionById(id));
    /**
     * 创建题目
     * @param qmsQuestionDTO
     * @return
     */
    }

    @PostMapping
    @Operation(summary = "创建题目", description = "创建题目")
    public Result<QmsQuestionDTO> create(@Valid @RequestBody QmsQuestionDTO qmsQuestionDTO) {
        qmsQuestionService.saveQuestion(qmsQuestionDTO);
        return Result.success(qmsQuestionDTO,"添加成功");
    }

    /**
     * 更新题目
     * @param id
     * @param qmsQuestionDTO
     * @return
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新题目", description = "更新题目")
    public Result<QmsQuestionDTO> update(@PathVariable Long id, @Valid @RequestBody QmsQuestionDTO qmsQuestionDTO) {
        qmsQuestionService.updateQuestion(id, qmsQuestionDTO);
        return Result.success(qmsQuestionDTO,"更新成功");
    }
    /**
     * 删除题目
     * @param id
     * @return
     */

    @DeleteMapping("/{id}")
    @Operation(summary = "删除题目", description = "删除题目")
    public Result<Long> delete(@PathVariable Long id) {
        qmsQuestionService.deleteQuestion(id);
        return Result.success(id,"删除成功");
    }

    /**
     * 分页查询题目
     * @param query
     * @return
     */
    @Operation(summary = "分页查询题目", description = "分页查询题目")
    @GetMapping("/page")
    public Result<PageVO<QmsQuestionVO>> page(@Valid @RequestBody QmsQuestionQueryDTO query) {
        return Result.success(qmsQuestionService.page(query),"查询成功");
    }
}
