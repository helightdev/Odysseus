package dev.helight.odysseus.scene;

import dev.helight.odysseus.database.UnspecificCubic;
import dev.helight.odysseus.database.UnspecificLocation;
import dev.helight.odysseus.script.GameObject;
import dev.helight.odysseus.script.ObjectContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class SceneData {

    private Map<String, UnspecificLocation> locationMapping;
    private Map<String, UnspecificCubic> cubicMapping;
    private List<ObjectContext> objects;

    public static SceneData empty() {
        SceneData sceneData = new SceneData();
        sceneData.locationMapping = new HashMap<>();
        sceneData.cubicMapping = new HashMap<>();
        sceneData.objects = new ArrayList<>();
        return sceneData;
    }

}
