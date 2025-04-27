package com.sunsuwedding.chat.common.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetryUtils {

    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final int DEFAULT_DELAY_MS = 20;

    /**
     * 네트워크 작업을 실행하고 실패 시 재시도하는 유틸리티 메서드
     * 성공 시 즉시 반환하며, 최대 3번까지 시도
     *
     * @param operation  실행할 작업
     * @param logContext 로그에 포함할 컨텍스트 정보
     */
    public static void executeWithRetry(Runnable operation, String logContext) {
        for (int attempt = 0; attempt < DEFAULT_MAX_RETRIES; attempt++) {
            try {
                operation.run();

                if (attempt > 0) {
                    log.info("{} 재시도 성공 (시도: {}/{})", logContext, attempt + 1, DEFAULT_MAX_RETRIES);
                }
                return; // 성공하면 즉시 반환

            } catch (Exception e) {
                if (attempt == DEFAULT_MAX_RETRIES - 1) {
                    log.error("{} 최종 실패 (시도: {}/{}): {}",
                            logContext, attempt + 1, DEFAULT_MAX_RETRIES, e.getMessage());
                } else {
                    log.warn("{} 실패 (시도: {}/{}): {}",
                            logContext, attempt + 1, DEFAULT_MAX_RETRIES, e.getMessage());
                    delayBeforeRetry(attempt);
                }
            }
        }
    }

    private static void delayBeforeRetry(int attempt) {
        try {
            // 지수 백오프 적용: 20ms, 40ms, 80ms, ...
            Thread.sleep((long) DEFAULT_DELAY_MS * (1L << attempt));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("재시도 대기 중 인터럽트 발생", ie);
        }
    }
}