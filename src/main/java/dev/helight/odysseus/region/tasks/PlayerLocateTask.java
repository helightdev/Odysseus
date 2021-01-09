package dev.helight.odysseus.region.tasks;

import com.destroystokyo.paper.Title;
import dev.helight.odysseus.region.Region;
import dev.helight.odysseus.region.RegionManager;
import dev.helight.odysseus.session.PlayerSession;
import dev.helight.odysseus.task.AbstractRoutine;
import dev.helight.odysseus.task.annotation.Routine;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Routine(value = "Core.Region.Locate", singleton = true, delay = 2500, repeat = 2500)
public class PlayerLocateTask extends AbstractRoutine {

    @Override
    public void run() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            PlayerSession session = PlayerSession.session(onlinePlayer);
            List<String> regions = RegionManager.getIntersecting(onlinePlayer.getLocation()).stream().map(Region::getIdentifier).collect(Collectors.toList());

            List<String> sessionAdditions = new ArrayList<>();
            for (String region : regions) {
                if (!session.getRegions().contains(region)) {
                    sessionAdditions.add(region);
                    Region r = RegionManager.getRegion(region);
                    if (r.getPayload().has("name") && r.getPayload().has("title")) {
                        String name = r.getPayload().get("name").getAsString();
                        boolean title = r.getPayload().get("title").getAsBoolean();
                        if (title) {
                            onlinePlayer.sendTitle(Title.builder()
                                    .title("§e" + name)
                                    .subtitle("Du betrittst")
                                    .stay(20 * 2)
                                    .build());
                        }
                    }
                }
            }
            session.getRegions().addAll(sessionAdditions);

            List<String> sessionRemoves = new ArrayList<>();
            for (String region : session.getRegions()) {
                if (!regions.contains(region)) {
                    sessionRemoves.add(region);
                    Region r = RegionManager.getRegion(region);
                    if (r.getPayload().has("name") && r.getPayload().has("title")) {
                        String name = r.getPayload().get("name").getAsString();
                        boolean title = r.getPayload().get("title").getAsBoolean();
                        if (title) {
                            onlinePlayer.sendTitle(Title.builder()
                                    .title("§e" + name)
                                    .subtitle("Du verlässt")
                                    .stay(20 * 2)
                                    .build());
                        }
                    }
                }
            }
            session.getRegions().removeAll(sessionRemoves);
        }
    }

}
