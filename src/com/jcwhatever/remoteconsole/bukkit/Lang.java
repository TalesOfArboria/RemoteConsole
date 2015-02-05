package com.jcwhatever.remoteconsole.bukkit;

import com.jcwhatever.nucleus.utils.language.Localized;

/**
 * Static convenience methods for localization.
 */
public class Lang {

    private Lang() {}

    /**
     * Localize text.
     *
     * @param text  The localizable text.
     * @param args  The arguments to insert.
     *
     * @return  Localized text.
     */
    @Localized
    public static String get(String text, Object... args) {
        return RemoteConsolePlugin.getPlugin().getLanguageManager().get(text, args);
    }
}
