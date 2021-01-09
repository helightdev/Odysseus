package dev.helight.odysseus.block;

import com.google.gson.JsonObject;
import lombok.Data;
import org.bukkit.block.Block;

@Data
public class CustomBlockDBO {

    public CustomBlockDBO() { }

    private String type;
    private JsonObject payload;
    private Block block;

}
