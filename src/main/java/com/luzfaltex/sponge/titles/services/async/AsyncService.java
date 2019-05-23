package com.luzfaltex.sponge.titles.services.async;

import java.util.concurrent.*;

public class AsyncService {
    public static <T> T execute(CompletableFuture<T> future) {
        return future.join();
    }
}
