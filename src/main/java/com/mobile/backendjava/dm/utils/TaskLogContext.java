package com.mobile.backendjava.dm.utils;

import java.util.concurrent.atomic.AtomicLong;

public final class TaskLogContext {
    private static final ThreadLocal<String> CURRENT_TASK_ID = new ThreadLocal<>();
    private static final AtomicLong TASK_ID_SEQUENCE = new AtomicLong(0);

    private TaskLogContext() {
    }

    public static void setTaskId(String taskId) {
        CURRENT_TASK_ID.set(taskId);
    }

    public static String nextTaskId() {
        return String.valueOf(TASK_ID_SEQUENCE.updateAndGet(current -> current == Long.MAX_VALUE ? 1 : current + 1));
    }

    public static String getTaskId() {
        return CURRENT_TASK_ID.get();
    }

    public static void clear() {
        CURRENT_TASK_ID.remove();
    }
}
