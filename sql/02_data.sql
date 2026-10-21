USE passjava_question;

INSERT INTO qms_type (id, type, comments)
VALUES
    (1, 'Java基础', 'Java基本语法与集合'),
    (2, 'MySQL', '数据库基础与索引'),
    (3, 'Redis', '缓存与Redis原理'),
    (4, 'Spring Boot', 'Spring Boot框架');

INSERT INTO qms_question
(id, title, answer, level, type, create_by)
VALUES
    (1, '什么是Java多态？',
     '同一个方法调用可以表现出不同的行为。', 1, 1, 1),

    (2, 'MySQL索引有什么作用？',
     '索引可以加快数据查询，但会增加维护开销。', 2, 2, 1),

    (3, 'Redis为什么这么快？',
     '主要与内存操作、高效的数据结构和事件处理机制有关。', 2, 3, 1),

    (4, '什么是Spring Boot自动配置？',
     '根据依赖和配置条件自动创建相应的Spring Bean。', 2, 4, 1);
