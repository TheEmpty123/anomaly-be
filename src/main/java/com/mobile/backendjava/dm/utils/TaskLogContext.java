package com.mobile.backendjava.dm.utils;

public final class TaskLogContext {
    private static final ThreadLocal<String> CURRENT_TASK_ID = new ThreadLocal<>();

    private TaskLogContext() {
    }

    public static void setTaskId(String taskId) {
        CURRENT_TASK_ID.set(taskId);
    }

    public static String getTaskId() {
        return CURRENT_TASK_ID.get();
    }

    public static void clear() {
        CURRENT_TASK_ID.remove();
    }
}
