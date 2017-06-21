package uk.co.riifactions.shop.common.language;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Data;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Internationalization management class.
 *
 * @author Thortex
 */
@Singleton
@Data
public class I18n {

    /**
     * The name of the messages' resource file.
     */
    public static final String MESSAGES = "messages";

    /**
     * Pattern to avoid double marks in messages.
     */
    public static final Pattern NO_DOUBLE_MARK = Pattern.compile("''");

    private final JavaPlugin plugin;

    private final Locale defaultLocale;
    private final ResourceBundle defaultBundle;

    private final Map<String, MessageFormat> messageFormatCache;

    @Inject
    public I18n(JavaPlugin plugin) {
        this.plugin = plugin;

        defaultLocale = Locale.getDefault();
        defaultBundle = getResourceBundle(MESSAGES + ".properties");

        messageFormatCache = new HashMap<>();
    }

    /**
     * Translate a message registered in the resource bundles.
     * <p>
     * Basic usage:
     * <pre>
     *   Player target;
     *   Player sender;
     *   if (target == null) {
     *     sender.sendMessage(translate("playerOffline", target));
     *     return;
     *   }
     * </pre>
     * <p>
     * <b>NOTE:</b> The message has to be registered in the resource bundles.
     *
     * @param msg  the message
     * @param args the formatting arguments
     * @return the translation
     */
    public String translate(String msg, Object... args) {
        // possibly add multilingual support later on... use default bundle for now
        if (args.length == 0) {
            return I18n.NO_DOUBLE_MARK.matcher(translate(defaultBundle, msg)).replaceAll("'");
        } else {
            return format(defaultBundle, msg, args);
        }
    }

    public String format(ResourceBundle bundle, String msg, Object... args) {
        String format = translate(bundle, msg);
        MessageFormat messageFormat = messageFormatCache.get(format);
        if (messageFormat == null) {
            // Cache the message translateColors to not have to create it again
            try {
                messageFormat = new MessageFormat(format);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().log(Level.SEVERE,
                    "Invalid Translation key for '" + msg + "': " + e.getMessage());
                format = format.replaceAll("\\{(\\D*?)\\}", "\\[$1\\]");
                messageFormat = new MessageFormat(format);
            }
            messageFormatCache.put(format, messageFormat);
        }
        return messageFormat.format(args);
    }

    private String translate(ResourceBundle bundle, String msg) {
        try {
            return bundle.getString(msg);
        } catch (MissingResourceException e) {
            plugin.getLogger().log(Level.WARNING, String
                .format("Missing translation key \"%s\" in translation file %s",
                    e.getKey(), bundle.getLocale().toString()));
            return defaultBundle.getString(msg);
        }
    }

    public ResourceBundle getResourceBundle(String fileName) {
        try {
            return new PropertyResourceBundle(plugin.getResource(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defaultBundle;
    }

}