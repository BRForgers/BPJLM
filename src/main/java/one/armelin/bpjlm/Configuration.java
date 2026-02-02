package one.armelin.bpjlm;

import blue.endless.jankson.Comment;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Configuration {
    @Comment(value = """
            Texts configuration
            All texts can be disabled by setting them to an empty string.
            Supports TinyMessage format
            <b></b> | Bold
            <i></i> | Italic
            <u></u> | Underline
            <mono></mono> | Monospace
            <color:#RRGGBB></color> | Color
            More info: https://github.com/Zoltus/TinyMessage
            """)
    public Texts texts = new Texts();

    public static class Texts {
        @Comment(value = """
                Join server message
                Available placeholders:
                {playername} | Player name
                {world} | World name, maybe 'unknown' if not available""")
        public String joinServer = "<b>{playername}</b> connected";

        @Comment(value = """
                Left server message
                Available placeholders:
                {playername} | Player name""")
        public String leftServer = "<b>{playername}</b> disconnected";

        @Comment(value = """
                Join world message
                Available placeholders:
                {playername} | Player name
                {world} | World name""")
        public String joinWorld = "<b>{playername}</b> joined in world <i>{world}</i>";

        @Comment(value = """
                Left world message
                Available placeholders:
                {playername} | Player name
                {world} | World name""")
        public String leftWorld = "<b>{playername}</b> left world <i>{world}</i>";
    }

    /**
     * Load configuration from file or create default if it doesn't exist
     * @return Loaded or default configuration
     */
    public static Configuration getConfig(Path configPath) {
        var jankson = Jankson.builder().build();
        Configuration config;

        try {
            Path configFile = configPath.resolve("config.json5");

            // Try to load existing config
            if (Files.exists(configFile)) {
                JsonObject configJson = jankson.load(configFile.toFile());
                config = jankson.fromJson(configJson, Configuration.class);
                BPJLM.LOGGER.atInfo().log("Configuration loaded from: " + configFile);
            } else {
                config = new Configuration();
                BPJLM.LOGGER.atInfo().log("Creating default configuration at: " + configFile);
            }

            // Save/update config file
            Files.writeString(configFile, jankson.toJson(config).toJson(true, true));

        } catch (IOException | SyntaxError e) {
            BPJLM.LOGGER.atSevere().withCause(e).log("Failed to load configuration, using defaults");
            config = new Configuration();
        }

        return config;
    }
}