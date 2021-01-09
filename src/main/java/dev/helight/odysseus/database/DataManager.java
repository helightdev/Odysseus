package dev.helight.odysseus.database;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.extract.ITempNaming;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.runtime.Network;
import dev.helight.odysseus.database.codecs.*;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static org.bson.codecs.configuration.CodecRegistries.*;

public class DataManager {

    private static final String FILE = "mongo-connection.string";
    private static final String DEFAULT = "mongodb://localhost:27555";
    public static final Gson gson = new Gson();

    private static MongoDatabase database;

    private static CountDownLatch latch = new CountDownLatch(1);

    public static MongoDatabase database() {
        if (database != null) return database;
        bootstrapMongo();
        System.out.println("Bootstrapped MongoDB");

        CodecRegistry registry = fromRegistries(
                com.mongodb.MongoClient.getDefaultCodecRegistry(),
                fromCodecs(new CustomBlockCodec(), new PlayerPreferenceCodec(), new LocationCodec(), new JsonObjectCodec(), new PlayerCodec(), new CraftPlayerCodec(), new BlockCodec()),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(getConnectionString())
                .codecRegistry(registry)
                .build());

        database = mongoClient.getDatabase("admin");

        System.out.println(database.listCollectionNames().into(new ArrayList<>()));

        return database;
    }

    @SneakyThrows
    private static void bootstrapMongo() {
        new Thread(DataManager::doDatabaseBootstrap).start();
        latch.countDown();
    }

    @SneakyThrows
    private static void doDatabaseBootstrap() {
        String bindIp = "localhost";
        int port = 27555;
        if (!available(port)) {
            System.out.println("Database already running in other plugin or instance");
        }

        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(bindIp, port, Network.localhostIsIPv6()))
                .replication(new Storage("db/", null, 0))
                .build();

        IDirectory artifactStorePath = new FixedPath("db/artifact/");
        ITempNaming executableNaming = new UUIDTempNaming();
        Command command = Command.MongoD;

        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaults(command)
                .artifactStore(new ExtractedArtifactStoreBuilder()
                        .defaults(command)
                        .download(new DownloadConfigBuilder()
                                .defaultsForCommand(command)
                                .artifactStorePath(artifactStorePath))
                        .executableNaming(executableNaming))
                .build();


        MongodStarter starter = MongodStarter.getInstance(runtimeConfig);

        MongodExecutable mongodExecutable = null;
        try {
            mongodExecutable = starter.prepare(mongodConfig);
            MongodProcess mongod = mongodExecutable.start();
            latch.countDown();
            Runtime.getRuntime().addShutdownHook(new Thread(mongod::stop));
            mongod.waitFor();
        } finally {
            if (mongodExecutable != null)
                mongodExecutable.stop();
        }
    }

    @SneakyThrows
    private static ConnectionString getConnectionString() {
        File file = new File(FILE);
        if (file.exists()) {
            return new ConnectionString(FileUtils.readFileToString(file, Charsets.UTF_8));
        } else {
            FileUtils.write(file, DEFAULT, Charsets.UTF_8);
            return new ConnectionString(DEFAULT);
        }
    }

    private static boolean available(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }

}
