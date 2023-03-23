package com.example.stock.facade;

import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockStockFacade {

    private RedisLockRepository redisLockRepository;
    private StockService stockService;

    public LettuceLockStockFacade(RedisLockRepository redisLockRepository, StockService stockService) {
        this.redisLockRepository = redisLockRepository;
        this.stockService = stockService;
    }

    public void decrease(Long key, Long quantity) throws InterruptedException {
        // lock 획득에 실패했다면 100ms 뒤에 재시도
        while (!redisLockRepository.lock(key)) {
            Thread.sleep(100);
        }

        try {
            // 재고를 줄여주고
            stockService.decrease(key, quantity);
        } finally {
            // lock을 해제한다.
            redisLockRepository.unlock(key);
        }
    }
}
