package com.mobile.backendjava.dm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobile.backendjava.dm.utils.LogObj;
import com.mobile.backendjava.dm.utils.TaskLogContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Supplier;

@Service
public abstract class AService {
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ModelMapper modelMapper;

    public final LogObj log = new LogObj();

    public void initLogger() {
        log.setName(this.getClass().getSimpleName());
        log.info("Initializing Logger");
    }

    protected <T> T runTask(String taskName, Supplier<T> task) {
        String currentTaskId = TaskLogContext.getTaskId();
        String taskId = currentTaskId == null ? UUID.randomUUID().toString() : currentTaskId;
        boolean rootTask = currentTaskId == null;
        long startedAt = System.currentTimeMillis();
        if (rootTask) {
            TaskLogContext.setTaskId(taskId);
        }
        log.info(taskName, "START");
        try {
            T result = task.get();
            log.info(taskName, "SUCCESS durationMs=" + (System.currentTimeMillis() - startedAt));
            return result;
        } catch (RuntimeException | Error ex) {
            log.error(taskName, "FAILED durationMs=" + (System.currentTimeMillis() - startedAt)
                    + " error=" + ex.getClass().getSimpleName()
                    + " message=" + ex.getMessage());
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
}
