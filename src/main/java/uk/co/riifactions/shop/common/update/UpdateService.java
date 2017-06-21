package uk.co.riifactions.shop.common.update;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Data;
import uk.co.riifactions.shop.common.Prioritized;
import uk.co.riifactions.shop.common.service.Service;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

/**
 * When started, it initializes the update task and event.
 *
 * @author Thortex
 */
@Singleton
@Data
@Prioritized
public class UpdateService implements Service {

    private final JavaPlugin plugin;
    private int taskId;

    @Inject
    public UpdateService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void start() {
        taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new UpdateTask(), 1, 1);
    }

    @Override
    public void stop() {
        plugin.getServer().getScheduler().cancelTask(taskId);
    }

    private class UpdateTask implements Runnable {
        private UpdateTask() {
            plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 1L);
        }

        public void run() {
            Arrays.stream(UpdateType.values())
                    .filter(UpdateType::elapsed)
                    .forEach(o -> plugin.getServer().getPluginManager().callEvent(new UpdateEvent(o)));
        }
    }

}
