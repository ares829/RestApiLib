package idv.ares.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ares on 2017/5/19.
 */

class TaskFactory {
    private static final TaskFactory ourInstance = new TaskFactory();

    private Map<String, Runnable> runnableMap;

    static TaskFactory getInstance() {
        return ourInstance;
    }

    private TaskFactory() {
        runnableMap = new ConcurrentHashMap<>();
    }

    String createTask(String taskName, Runnable runnable) {
        runnableMap.put(taskName, runnable);
        return taskName;
    }

    void removeTask(String taskName) {
        runnableMap.remove(taskName);
    }

    Runnable getTask(String taskName) {
        return runnableMap.get(taskName);
    }
}
