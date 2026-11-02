package org.example.passjava.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class FirstJUnitTest {
    @Test
    void test1() {
        int result = 1 + 1;
        assertEquals(2, result);
    }

    @Test
    void test2() {
        int result = 2 + 2;
        assertTrue(result > 3);
    }
}
