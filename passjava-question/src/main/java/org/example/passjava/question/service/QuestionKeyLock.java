package org.example.passjava.question.service;


import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

@Component
public class QuestionKeyLock {

    private static final int STRIPES = 256;

    private final ReentrantLock[] locks =
            new ReentrantLock[STRIPES];

    public QuestionKeyLock() {
        for (int i = 0; i < STRIPES; i++) {
            locks[i] = new ReentrantLock();
        }
    }

    public ReentrantLock getLock(Long questionId) {
        int index = Math.floorMod(
                questionId.hashCode(),
                STRIPES
        );
        return locks[index];
    }
}
