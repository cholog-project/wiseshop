package cholog.wiseshop.common;

import cholog.wiseshop.db.campaign.Campaign;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTaskStorage {

    private final ConcurrentHashMap<Long, ScheduledFuture<?>> startScheduledTasks;
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> endScheduledTasks;

    public ScheduledTaskStorage() {
        this.startScheduledTasks = new ConcurrentHashMap<>();
        this.endScheduledTasks = new ConcurrentHashMap<>();
    }

    public void saveToStart(ScheduledFuture<?> task, Campaign campaign) {
        startScheduledTasks.put(campaign.getId(), task);
    }

    public void saveToEnd(ScheduledFuture<?> task, Campaign campaign) {
        endScheduledTasks.put(campaign.getId(), task);
    }

    public void deleteAll(Campaign campaign) {
        ScheduledFuture<?> startTask = startScheduledTasks.remove(campaign.getId());
        if (startTask != null && !startTask.isDone()) {
            startTask.cancel(false);
        }

        ScheduledFuture<?> endTask = endScheduledTasks.remove(campaign.getId());
        if (endTask != null && !endTask.isDone()) {
            endTask.cancel(false);
        }
    }
}
