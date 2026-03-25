package com.dustopus.qianyan.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeToolTest {

    private final TimeTool timeTool = new TimeTool();

    @Test
    void testGetCurrentTimeReturnsValidFormat() {
        String result = timeTool.getCurrentTimeInShanghai();
        assertNotNull(result);
        assertFalse(result.isBlank());
        // Format: yyyy-MM-dd HH:mm:ss EEEE (中国标准时间)
        assertTrue(result.contains("中国标准时间"));
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} .+"));
    }
}
