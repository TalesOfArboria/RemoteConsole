package com.jcwhatever.remoteconsole.bukkit;

import com.jcwhatever.nucleus.managed.language.Localized;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;

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
    public static IChatMessage get(CharSequence text, Object... args) {
        return RemoteConsolePlugin.getPlugin().getLanguageContext().get(text, args);
    }
}
