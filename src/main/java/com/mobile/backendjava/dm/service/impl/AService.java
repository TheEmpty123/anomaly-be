package com.mobile.backendjava.dm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobile.backendjava.dm.utils.TaskLogContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

@Service
public abstract class AService {
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ModelMapper modelMapper;

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public void initLogger() {
        log.info("event=service.initialized service={}", getClass().getSimpleName());
    }

    protected <T> T runTask(String taskName, Supplier<T> task) {
        return runTask(taskName, null, task);
    }

    protected <T> T runTask(String taskName, String taskDetails, Supplier<T> task) {
        String currentTaskId = TaskLogContext.getTaskId();
        String taskId = currentTaskId == null ? TaskLogContext.nextTaskId() : currentTaskId;
        boolean rootTask = currentTaskId == null;
        long startedAt = System.currentTimeMillis();
        if (rootTask) {
            TaskLogContext.setTaskId(taskId);
        }
        log.info("event=task.processing.start message=\"Starting service task\" service={} task={} context={}",
                getClass().getSimpleName(), taskName, contextOrNone(taskDetails));
        try {
            T result = task.get();
            log.info("event=task.processing.finish message=\"Finished service task\" service={} task={} outcome=success durationMs={} context={} result={}",
                    getClass().getSimpleName(), taskName, System.currentTimeMillis() - startedAt,
                    contextOrNone(taskDetails), serializeResult(result));
            return result;
        } catch (RuntimeException | Error ex) {
            log.error("event=task.processing.finish message=\"Service task failed\" service={} task={} outcome=failure durationMs={} context={} errorType={} errorMessage={}",
                    getClass().getSimpleName(), taskName, System.currentTimeMillis() - startedAt,
                    contextOrNone(taskDetails), ex.getClass().getSimpleName(), ex.getMessage(), ex);
            throw ex;
        } finally {
            if (rootTask) {
                TaskLogContext.clear();
            }
        }
    }

    protected void runTask(String taskName, Runnable task) {
        runTask(taskName, () -> {
            task.run();
            return null;
        });
    }

    protected void runTask(String taskName, String taskDetails, Runnable task) {
        runTask(taskName, taskDetails, () -> {
            task.run();
            return null;
        });
    }

    protected <T> T runTaskWithoutLogging(Supplier<T> task) {
        return task.get();
    }

    protected void runTaskWithoutLogging(Runnable task) {
        task.run();
    }

    protected void runSilentTask(String taskName, String taskDetails, Runnable task) {
        try {
            runTask(taskName, taskDetails, task);
        } catch (RuntimeException | Error ex) {
            log.error("event=background-task.failure service={} task={} errorType={} errorMessage={}",
                    getClass().getSimpleName(), taskName, ex.getClass().getSimpleName(), ex.getMessage(), ex);
            throw ex;
        }
    }

    protected String detail(String key, Object value) {
        return key + "=" + value;
    }

    protected String details(String... values) {
        return String.join(" ", values);
    }

    private String contextOrNone(String taskDetails) {
        if (taskDetails == null || taskDetails.isBlank()) {
            return "none";
        }
        return taskDetails;
    }

    private String serializeResult(Object result) {
        if (result == null) {
            return "null";
        }
        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception ex) {
            return String.valueOf(result);
        }
    }
}
