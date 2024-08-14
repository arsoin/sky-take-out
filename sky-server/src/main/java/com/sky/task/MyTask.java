package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

//@Component
@Slf4j
public class MyTask {
    /**
     * 每隔5秒执行一次，业务逻辑
     */
    @Scheduled(cron = "0/5 * * * * ? ")
    public void executeTask(){
        log.info("定时任务开始:{}",new Date());
    }
}
