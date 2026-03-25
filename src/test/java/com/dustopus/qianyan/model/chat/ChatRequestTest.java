package com.dustopus.qianyan.model.chat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatRequestTest {

    @Test
    void testGetActualMessageWithMessage() {
        ChatRequest req = new ChatRequest();
        req.setMessage("hello");
        req.setPrompt("old prompt");
        assertEquals("hello", req.getActualMessage());
    }

    @Test
    void testGetActualMessageFallbackToPrompt() {
        ChatRequest req = new ChatRequest();
        req.setPrompt("old prompt");
        assertEquals("old prompt", req.getActualMessage());
    }

    @Test
    void testGetActualMemoryIdPriority() {
        ChatRequest req = new ChatRequest();
        req.setMemoryId(100L);
        req.setChatId(200L);
        req.setSessionId(300L);
        assertEquals(100L, req.getActualMemoryId());
    }

    @Test
    void testGetActualMemoryIdFallbackToChatId() {
        ChatRequest req = new ChatRequest();
        req.setChatId(200L);
        req.setSessionId(300L);
        assertEquals(200L, req.getActualMemoryId());
    }

    @Test
    void testGetActualMemoryIdFallbackToSessionId() {
        ChatRequest req = new ChatRequest();
        req.setSessionId(300L);
        assertEquals(300L, req.getActualMemoryId());
    }
}
