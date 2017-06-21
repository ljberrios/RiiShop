package uk.co.riifactions.shop.common.config;

import com.google.common.base.Preconditions;
import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Optional;

/**
 * Handles new file configurations.
 *
 * @author Thortex
 */
@Data
public class Configuration {

    protected final JavaPlugin plugin;
    protected File file;

    protected FileConfiguration config;

    public Configuration(JavaPlugin plugin, File file) {
        this(plugin, file, null);
    }

    public Configuration(JavaPlugin plugin, File file, String resource) {
        this.plugin = plugin;
        this.file = file;

        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                if (resource != null) {
                    // Get the resource from the resources folder
                    plugin.saveResource(resource, true);
                }
            }

            config = YamlConfiguration.loadConfiguration(file);
            config.load(file);
        } catch (Throwable e) {
            throw new RuntimeException(String.format("failed to create config '%s'", file.getName()), e);
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        Optional<Reader> stream = getDefaultReader();
        if (stream.isPresent()) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(stream.get());
            config.setDefaults(defConfig);
        }
    }

    public void set(String path, Object value, boolean save) {
        config.set(Preconditions.checkNotNull(path), value);
        if (save) {
            save();
        }
    }

    public void setDefault(String path, Object value) {
        if (!config.contains(path)) {
            config.set(path, value);
            save();
        }
    }

    private Optional<Reader> getDefaultReader() {
        try {
            return Optional.of(new InputStreamReader(plugin.getResource(file.getName()), "UTF8"));
        } catch (UnsupportedEncodingException ignored) {
            // it's utf8, how would it be unsupported
        }
        return Optional.empty();
    }

}
