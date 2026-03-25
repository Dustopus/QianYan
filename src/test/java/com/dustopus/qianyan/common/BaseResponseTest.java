package com.dustopus.qianyan.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseResponseTest {

    @Test
    void testSuccessResponse() {
        BaseResponse<String> response = ResultUtils.success("hello");
        assertEquals(200, response.getCode());
        assertEquals("hello", response.getData());
        assertEquals("ok", response.getMessage());
    }

    @Test
    void testErrorResponse() {
        BaseResponse<?> response = ResultUtils.error(ErrorCode.PARAMS_ERROR);
        assertEquals(40000, response.getCode());
        assertNull(response.getData());
        assertEquals("请求参数错误", response.getMessage());
    }

    @Test
    void testCustomErrorResponse() {
        BaseResponse<?> response = ResultUtils.error(50001, "自定义错误");
        assertEquals(50001, response.getCode());
        assertEquals("自定义错误", response.getMessage());
    }

    @Test
    void testErrorCodeEnum() {
        assertEquals(40000, ErrorCode.PARAMS_ERROR.getCode());
        assertEquals(50000, ErrorCode.SYSTEM_ERROR.getCode());
        assertEquals("包含敏感词，请求被拒绝", ErrorCode.SENSITIVE_WORD_ERROR.getMessage());
    }
}
