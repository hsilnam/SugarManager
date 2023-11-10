package kr.co.sugarmanager.userservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncCongfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);//기본적으로 대기중인 쓰레드의 개수
        executor.setMaxPoolSize(20);//동시에 동작하는 최대 쓰레드 개수
        executor.setQueueCapacity(1000);//CorePool이 초과될 때 기다리는 큐

        executor.setThreadNamePrefix("poke-async-");
        executor.initialize();
        return executor;
    }
}
