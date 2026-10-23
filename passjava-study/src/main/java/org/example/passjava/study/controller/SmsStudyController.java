package org.example.passjava.study.controller;

import org.example.passjava.study.VO.QmsQuestionVO;
import org.example.passjava.study.VO.SmsStudyRecordVO;
import org.example.passjava.study.common.Result;
import org.example.passjava.study.service.SmsStudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/study")
public class SmsStudyController {
    @Autowired
    private SmsStudyService smsStudyService;

    /**
     * 获取当前用户id
     * @param jwt
     * @return
     */
    private Long currentUserId(Jwt jwt) {
        try {
            return Long.valueOf(jwt.getSubject());
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "无效用户身份");
        }
    }

    /**
     * 获取当前用户名称
     * @param jwt
     * @return
     */
    private String currentUserName(Jwt jwt) {
        try {
            return jwt.getClaim("username").toString();
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "无效用户身份");
        }
    }

    /**
     * 记录学习一次
     * @param jwt
     * @param questionId
     * @return
     */
    @PostMapping("/views/{questionId}")
    public Result<Void> recordView(@AuthenticationPrincipal Jwt jwt, @PathVariable Long questionId) {
        Long userId = currentUserId(jwt);
        smsStudyService.recordView(userId, questionId);
        return Result.success(null,"记录用户" + currentUserName(jwt) + "一次学习"+questionId+"成功");
    }

    /**
     * 查看学习记录
     * @param jwt
     * @return
     */
    @GetMapping("/views")
    public Result<List<SmsStudyRecordVO>> getViews(@AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        List<SmsStudyRecordVO> views = smsStudyService.getViews(userId);
        return Result.success(views,"查看用户"+currentUserName(jwt)+"学习记录成功");
    }

    /**
     * 查看我的收藏列表
     * @param jwt
     * @return
     */
    @GetMapping("/favorites")
    public Result<List<Long>> getFavorites(@AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        List<Long> favorites = smsStudyService.getFavorites(userId);
        return Result.success(favorites,"获取用户"+currentUserName(jwt)+"收藏问题记录成功");
    }

    /**
     * 收藏题目
     * @param jwt
     * @param questionId
     * @return
     */
    @PostMapping("/favorites/{questionId}")
    public Result<Void> getFavoriteQuestions(@AuthenticationPrincipal Jwt jwt, @PathVariable Long questionId) {
        Long userId = currentUserId(jwt);
        smsStudyService.addFavorite(userId, questionId);
        return Result.success(null,"收藏用户"+currentUserName(jwt)+"问题"+questionId+"成功");
    }

    /**
     * 取消收藏
     * @param jwt
     * @param questionId
     * @return
     */
    @DeleteMapping("/favorites/{questionId}")
    public Result<Void> removeFavorite(@AuthenticationPrincipal Jwt jwt, @PathVariable Long questionId) {
        Long userId = currentUserId(jwt);
        smsStudyService.removeFavorite(userId, questionId);
        return Result.success(null,"取消收藏用户"+currentUserName(jwt)+"问题"+questionId+"成功");
    }

    /**
     * 标记错题
     * @param jwt
     * @param questionId
     * @return
     */
    @PostMapping("/wrongs/{questionId}")
    public Result<Void> markWrong(@AuthenticationPrincipal Jwt jwt, @PathVariable Long questionId){
        Long userId = currentUserId(jwt);
        smsStudyService.markWrong(userId, questionId);
        return Result.success(null,"标记用户"+currentUserName(jwt)+"问题"+questionId+"成功");
    }

    /**
     * 取消标记
     * @param jwt
     * @param questionId
     * @return
     */
    @DeleteMapping("/wrongs/{questionId}")
    public Result<Void> removeWrong(@AuthenticationPrincipal Jwt jwt, @PathVariable Long questionId){
        Long userId = currentUserId(jwt);
        smsStudyService.removeWrong(userId, questionId);
        return Result.success(null,"取消标记用户"+currentUserName(jwt)+"问题"+questionId+"成功");
    }

    /**
     * 查看我的错题列表
     * @param jwt
     * @return
     */
    @GetMapping("/wrongs")
    public Result<List<SmsStudyRecordVO>> getWrongs(@AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserId(jwt);
        List<SmsStudyRecordVO> wrongs = smsStudyService.getWrongs(userId);
        return Result.success(wrongs,"获取用户"+currentUserName(jwt)+"错题列表成功");
    }
}
