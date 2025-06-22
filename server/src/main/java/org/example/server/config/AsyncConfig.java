package org.example.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author Aleksey
 */
@Configuration
public class AsyncConfig {

    @Bean(name = "excelTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);       // Число потоков по умолчанию
        executor.setMaxPoolSize(8);       // Максимальное число потоков
        executor.setQueueCapacity(100);    // Очередь задач
        executor.setThreadNamePrefix("AsyncExcel-");
        executor.initialize();
        return executor;
    }
}
