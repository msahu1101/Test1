package com.mgmresorts.rcxplatform;

import com.google.inject.Inject;
import com.mgmresorts.common.config.Runtime;
import com.mgmresorts.loyalty.service.IRcxService;
import com.mgmresorts.rcxplatform.pojo.GetMembersResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RcxExecutorService {

    private final int rcxPoolSize = Runtime.get().getInt("rcx.thread.pool.size", 10);

    private final ExecutorService executorService = Executors.newFixedThreadPool(rcxPoolSize);

    private List<Future> futures = new ArrayList<>();

    @Inject
    private IRcxService rcxService;

    public Future<GetMembersResponse> getPlayerBalanceAsync(String playerId) {
        return executorService.submit(() -> rcxService.getPlayerBalances(playerId));
    }

    public Future<Double> getPendingPlayerBalanceAsync(String playerId) {
        return executorService.submit(() -> rcxService.rcxGetPendingPoints(playerId));
    }
}
