package sh.okx.rankup;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import sh.okx.rankup.providers.TestEconomyProvider;
import sh.okx.rankup.hook.GroupProvider;
import sh.okx.rankup.providers.TestGroupProvider;
import sh.okx.rankup.providers.TestPermissionManager;

public abstract class RankupTest {
    private final File testResourceFolder;

    public RankupTest() {
        this("default");
    }

    public RankupTest(String testResourceFolder) {
        URL resource = this.getClass().getResource("/" + testResourceFolder);
        if (resource != null) {
            this.testResourceFolder = new File(resource.getPath());
        } else {
            this.testResourceFolder = null;
        }
    }

    protected GroupProvider groupProvider;
    protected ServerMock server;
    protected RankupPlugin plugin;

    @BeforeEach
    public void setup() {
        System.setProperty("RANKUP_TEST", "true");

        try {
            groupProvider = new TestGroupProvider();

            server = MockBukkit.mock();
            plugin = (RankupPlugin) server.getPluginManager()
                .loadPlugin(RankupPlugin.class, new Object[]{
                    new TestPermissionManager(groupProvider),
                    new TestEconomyProvider()
                });

            if (this.testResourceFolder != null) {
                Path testPath = this.testResourceFolder.toPath();
                Path pluginPath = plugin.getDataFolder().toPath();
                Files.walkFileTree(testPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                        Path out = pluginPath.resolve(testPath.relativize(file));
                        System.out.println("Copy " + file + " to " + out);
                        out.getParent().toFile().mkdirs();
                        Files.copy(file, out);
                        return super.visitFile(file, attrs);
                    }
                });
            }

            server.getPluginManager().enablePlugin(plugin);

            // let rankup finish setting up
            server.getScheduler().performTicks(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
        System.clearProperty("RANKUP_TEST");
    }
}
