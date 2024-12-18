package fi.septicuss.tooltips.managers.tooltip.tasks;

import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.tooltip.TooltipManager;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.PlayerTooltipData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Manages:
 * - checking which preset should be shown to a given player based on conditions
 * - saving the data provided by conditions (context)
 */
public class ConditionTask extends BukkitRunnable {

    private final TooltipManager manager;

    public ConditionTask(final TooltipManager tooltipManager) {
        this.manager = tooltipManager;
    }

    @Override
    public void run() {

        var onlinePlayers = Bukkit.getOnlinePlayers();
        if (onlinePlayers.isEmpty())
            return;

        for (Player player : onlinePlayers) {

            final PlayerTooltipData data = manager.getPlayerTooltipData(player);
            final Context context = new Context();

            for (var holderEntry : manager.getHolders().entrySet()) {
                var id = holderEntry.getKey();
                var holder = holderEntry.getValue();

                final boolean conditionResult = holder.evaluate(player, context);

                final boolean hasCurrentPreset = data.hasCurrentPreset();
                final boolean isSamePreset = hasCurrentPreset && data.getCurrentPreset().equals(id);

                /* Handling the same preset */

                if (isSamePreset) {
                    // Same preset true again, no actions taken
                    if (conditionResult) {
                        break;
                    }

                    // Same preset false, reset current preset
                    data.setCurrentPreset(null);
                    continue;
                }

                /* Handling a new preset */
                if (conditionResult) {
                    data.setCurrentPreset(id);
                    break;
                }
            }

            data.updatePendingContext(context);


        }
    }


}
