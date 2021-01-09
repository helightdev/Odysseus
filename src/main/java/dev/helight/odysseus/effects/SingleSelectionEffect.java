package dev.helight.odysseus.effects;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import dev.helight.odysseus.Raycast;
import dev.helight.odysseus.region.MathUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class SingleSelectionEffect extends Effect {

    public String actionbar = "";

    public SingleSelectionEffect(EffectManager effectManager) {
        super(effectManager);
        type = EffectType.REPEATING;
        period=5;
        iterations=-1;
        color=Color.SILVER;
    }


    @Override
    public void onRun() {
        display(Particle.REDSTONE, getLocation(), color);

        Location a = getLocation().clone().subtract(3, 3, 3);
        Location b = getLocation().clone().add(3, 3, 3);

        if (MathUtils.containsBox(a,b,targetPlayer.getLocation())) {
            double dist = Raycast.maxVectorDistance(
                    targetPlayer.getEyeLocation().getDirection(),
                    Raycast.between(targetPlayer.getEyeLocation(), getLocation())
            );

            if (dist <= 0.5) {
                targetPlayer.sendActionBar(actionbar);
            }
        }
    }
}
