package dev.helight.odysseus.asset;

import com.google.common.io.Files;
import com.google.gson.Gson;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Assets {

    public static List<Asset> assets = new ArrayList<>();

    public static Asset getById(String id) {
        return assets.stream()
                .filter(asset -> asset.getId().equals(id))
                .findFirst().orElse(null);
    }

    @SneakyThrows
    public static void loadAssets() {
        Gson gson = new Gson();
        File file = new File("assets.odysseus");
        if (file.exists()) {
            Asset[] assetArray = gson.fromJson(Files.toString(file, StandardCharsets.UTF_8), Asset[].class);
            assets = new ArrayList(Arrays.asList(assetArray));
        }
    }

    @SneakyThrows
    public static void storeAssets() {
        Gson gson = new Gson();
        File file = new File("assets.odysseus");
        if (!file.exists()) file.createNewFile();
        Files.write(gson.toJson(assets), file, StandardCharsets.UTF_8);
    }

}
