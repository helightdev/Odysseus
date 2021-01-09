package dev.helight.odysseus.entity.debug;

import de.slikey.effectlib.effect.LineEffect;
import dev.helight.odysseus.Odysseus;
import dev.helight.odysseus.entity.CustomEntity;
import dev.helight.odysseus.entity.EntityAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class DebugAbility extends EntityAbility {

    public DebugAbility() {
        super(1000, 0.25);
    }

    @Override
    public void execute(CustomEntity entity, LivingEntity living) {
        LineEffect effect = new LineEffect(Odysseus.getInstance().getEffectManager());
        effect.setLocation(living.getLocation());
        effect.setTargetLocation(living.getLocation().add(0,10,0));
        effect.targetPlayers = (List<Player>) Bukkit.getOnlinePlayers();
        effect.start();
    }
}
