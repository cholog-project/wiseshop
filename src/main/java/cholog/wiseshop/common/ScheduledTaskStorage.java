package cholog.wiseshop.common;

import cholog.wiseshop.db.campaign.Campaign;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTaskStorage {

    private final ConcurrentHashMap<Long, ScheduledFuture<?>> startScheduledTasks;

    public ScheduledTaskStorage() {
        this.startScheduledTasks = new ConcurrentHashMap<>();
    }

    public void saveScheduleTask(ScheduledFuture<?> task, Campaign campaign) {
        startScheduledTasks.put(campaign.getId(), task);
    }

    public void deleteStartTask(Campaign campaign) {
        ScheduledFuture<?> startTask = startScheduledTasks.remove(campaign.getId());
        if (startTask != null) {
            startTask.cancel(true);
        }
    }
}
