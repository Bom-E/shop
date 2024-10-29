package com.bom.shop.utility;

import com.bom.shop.user.service.RefreshTokenMangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final RefreshTokenMangeService refreshTokenMangeService;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupExpiredTokens(){
        log.info("Starting expired refresh tokens cleanup - {}", LocalDateTime.now());

        try {
            refreshTokenMangeService.deleteExpiredTokens();
            log.info("Completed refresh tokens cleanup");
        } catch (Exception e) {
            log.error("Error during token cleanup: ", e);
        }
    }
}
