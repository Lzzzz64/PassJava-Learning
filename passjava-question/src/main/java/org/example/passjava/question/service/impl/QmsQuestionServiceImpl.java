package org.example.passjava.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.passjava.question.dto.QmsQuestionDTO;
import org.example.passjava.question.dto.QmsQuestionQueryDTO;
import org.example.passjava.question.entity.QmsQuestion;
import org.example.passjava.question.entity.QmsType;
import org.example.passjava.question.service.QmsQuestionCacheService;
import org.example.passjava.question.service.QmsQuestionService;
import org.example.passjava.question.mapper.QmsQuestionMapper;
import org.example.passjava.question.service.QmsTypeService;
import org.example.passjava.question.service.QuestionKeyLock;
import org.example.passjava.question.vo.PageVO;
import org.example.passjava.question.vo.QmsQuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
* @author 123456
* @description 针对表【qms_question】的数据库操作Service实现
* @createDate 2026-10-19 14:55:51
*/
@Service
public class QmsQuestionServiceImpl extends ServiceImpl<QmsQuestionMapper, QmsQuestion>
    implements QmsQuestionService{

    @Autowired
    private QmsQuestionMapper qmsQuestionMapper;
    @Autowired
    private QmsTypeService qmsTypeMapper;
    @Autowired
    private QmsQuestionCacheService qmsQuestionCacheService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private QuestionKeyLock questionKeyLock;

    @Override
    public QmsQuestion getQuestionById(Long id) {
        // 1. 第一次检查缓存，不需要加锁
        String cache = qmsQuestionCacheService.get(id);
        if(cache != null){
            log.debug("第一次从缓存中获取题目，id=" + id);
            if(cache.equals(QmsQuestionCacheService.NULL_VALUE)){
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "问题不存在"
                );
            }

            try{
                QmsQuestion qmsQuestionCache = objectMapper.readValue(cache, QmsQuestion.class);
                return qmsQuestionCache;
            }catch (JsonProcessingException e){
                log.debug("题目缓存序列化失败，进入mysql查找，id=" + id + ", e=" + e);
                qmsQuestionCacheService.evictAndIncrementVersion(id);
            }
        }

        // 2. 只对缓存未命中的请求加锁
        ReentrantLock lock = questionKeyLock.getLock(id);
        lock.lock();
        try{
            // 3. 第二次检查缓存
            String cache2 = qmsQuestionCacheService.get(id);
            if(cache2 != null){
                log.debug("第二次从缓存中获取题目，id=" + id);

                if(cache2.equals(QmsQuestionCacheService.NULL_VALUE)){
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "问题不存在"
                    );
                }

                try{
                    QmsQuestion qmsQuestionCache = objectMapper.readValue(cache2, QmsQuestion.class);
                    return qmsQuestionCache;
                }catch (JsonProcessingException e){
                    log.debug("题目缓存序列化失败，进入mysql查找，id=" + id + ", e=" + e);
                    qmsQuestionCacheService.evictAndIncrementVersion(id);
                }
            }

            // 4. 从数据库中获取数据
            log.debug("从数据库中获取题目，id=" + id);
            // 查询mysql前，先获取世代号
            String generation = qmsQuestionCacheService.getGeneration(id);
            QmsQuestion qmsQuestion = qmsQuestionMapper.selectById(id);
            if(qmsQuestion == null){
                boolean saved = qmsQuestionCacheService.saveNullIfUnchanged(id, generation);
                if(!saved){
                    // 检测到版本变化时重新调用直接数据库查询方法，然后返回结果，不缓存
                    return queryLatestOrNotFound(id);
                }
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "问题不存在"
                );
            }

            try{
                boolean saved = qmsQuestionCacheService.saveIfUnchanged(id, generation,
                        objectMapper.writeValueAsString(qmsQuestion));
                if(!saved){
                    // 检测到版本变化时重新调用直接数据库查询方法，然后返回结果，不缓存
                    return queryLatestOrNotFound(id);
                }
            }catch (JsonProcessingException e){
                log.debug("缓存序列化失败：" + id);
            }

            return qmsQuestion;
        }finally {
            // 5. 解锁
            lock.unlock();
        }
    }

    @Override
    public void saveQuestion(QmsQuestionDTO qmsQuestionDTO) {
        QmsQuestion qmsQuestion = new QmsQuestion();
        qmsQuestion.setTitle(qmsQuestionDTO.getTitle());
        qmsQuestion.setAnswer(qmsQuestionDTO.getAnswer());
        qmsQuestion.setLevel(qmsQuestionDTO.getLevel());
        qmsQuestion.setType(qmsQuestionDTO.getType());
        qmsQuestion.setEnable(1);
        qmsQuestion.setDelFlag(0);
        qmsQuestion.setCreateTime(new Date());
        qmsQuestion.setUpdateTime(new Date());
        int i = qmsQuestionMapper.insert(qmsQuestion);

        if(i != 1)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "问题不存在"
            );

        qmsQuestionCacheService.evictAndIncrementVersion(qmsQuestion.getId());
    }

    @Override
    public void updateQuestion(Long id, QmsQuestionDTO qmsQuestionDTO) {
        QmsQuestion qmsQuestion = qmsQuestionMapper.selectById(id);
        if(qmsQuestion == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "问题不存在"
            );
        }
        qmsQuestion.setTitle(qmsQuestionDTO.getTitle());
        qmsQuestion.setAnswer(qmsQuestionDTO.getAnswer());
        qmsQuestion.setLevel(qmsQuestionDTO.getLevel());
        qmsQuestion.setType(qmsQuestionDTO.getType());
        qmsQuestion.setUpdateTime(new Date());
        int i = qmsQuestionMapper.updateById(qmsQuestion);

        if(i != 1){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "问题不存在"
            );
        }

        qmsQuestionCacheService.evictAndIncrementVersion(qmsQuestion.getId());
    }

    @Override
    public void deleteQuestion(Long id) {
        int i = qmsQuestionMapper.deleteById(id);

        if(i != 1)
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "问题不存在"
            );

        qmsQuestionCacheService.evictAndIncrementVersion(id);
    }

    @Override
    public PageVO<QmsQuestionVO> page(QmsQuestionQueryDTO query) {
        IPage<QmsQuestion> page = new Page<>(
                query.getPage(),
                query.getSize()
        );
        LambdaQueryWrapper<QmsQuestion> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(query.getKeyword() != null)
            lambdaQueryWrapper.like(QmsQuestion::getTitle, query.getKeyword());
        if(query.getType() != null)
            lambdaQueryWrapper.eq(QmsQuestion::getType, query.getType());
        if(query.getLevel() != null)
            lambdaQueryWrapper.eq(QmsQuestion::getLevel, query.getLevel());

        IPage<QmsQuestion> result = qmsQuestionMapper.selectPage(page, lambdaQueryWrapper);

        List<QmsQuestion> questions = result.getRecords();
        Set<Long> typeIdSet = new HashSet<>();
        for (QmsQuestion question : questions) {
            if (question.getType() != null) {
                typeIdSet.add(question.getType());
            }
        }
        List<Long> typeIds = new ArrayList<>(typeIdSet);


        Map<Long, String> typeNames = new HashMap<>();
        if (!typeIds.isEmpty()) {
            List<QmsType> types = qmsTypeMapper.listByIds(typeIds);
            for (QmsType type : types)
                typeNames.put(type.getId(), type.getType());
        }

        // 将 Entity 转换为 VO
        List<QmsQuestionVO> records = result.getRecords()
                .stream()
                .map(entity -> new QmsQuestionVO(
                        entity.getId(),
                        entity.getTitle(),
                        entity.getType(),
                        typeNames.get(entity.getType()),
                        entity.getLevel()
                ))
                .toList();

        return new PageVO<>(
                result.getCurrent(),
                result.getSize(),
                result.getTotal(),
                result.getPages(),
                records);
    }

    private QmsQuestion queryLatestOrNotFound(Long id) {

        QmsQuestion question =
                qmsQuestionMapper.selectById(id);

        if (question == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "问题不存在"
            );
        }

        return question;
    }
}




