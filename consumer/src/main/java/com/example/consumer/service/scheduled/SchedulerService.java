package com.example.consumer.service.scheduled;

import com.example.consumer.config.Constant;
import com.example.consumer.service.impl.AssignFailedTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final AssignFailedTransactionService assignFailedTransactionService;

    @Scheduled(initialDelayString = Constant.INIT_DELAY_SCHEDULE, fixedDelayString = Constant.CLIENT_CHECK_SCHEDULE)
    public void checkValidClientForTransaction() throws InterruptedException {
            assignFailedTransactionService.assignFailedTransactionToClient();
    }
}
