package net.visualillusionsent.goodnight.canary;

import net.canarymod.Canary;
import net.canarymod.chat.Colors;
import net.canarymod.chat.TextFormat;
import net.canarymod.commandsys.CommandDependencyException;
import net.canarymod.commandsys.CommandListener;
import net.visualillusionsent.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Visual Illusions Plugin Information command
 *
 * @author Jason (darkdiplomat)
 */
public abstract class VisualIllusionsCanaryPluginInformationCommand implements CommandListener {
    protected final List<String> about;
    protected final VisualIllusionsCanaryPlugin plugin;

    public VisualIllusionsCanaryPluginInformationCommand(VisualIllusionsCanaryPlugin plugin) {
        this.plugin = plugin;
        List<String> pre = new ArrayList<String>();
        pre.add(center(Colors.CYAN + "---" + Colors.LIGHT_GREEN + plugin.getName() + " " + Colors.ORANGE + "v" + plugin.getVersion() + Colors.CYAN + " ---"));
        pre.add("$VERSION_CHECK$");
        pre.add(Colors.CYAN + "Jenkins Build: " + Colors.LIGHT_GREEN + plugin.getBuild());
        pre.add(Colors.CYAN + "Built On: " + Colors.LIGHT_GREEN + plugin.getBuildTime());
        pre.add(Colors.CYAN + "Developer(s): " + Colors.LIGHT_GREEN + plugin.getDevelopers());
        pre.add(Colors.CYAN + "Website: " + Colors.LIGHT_GREEN + plugin.getWikiURL());
        pre.add(Colors.CYAN + "Issues: " + Colors.LIGHT_GREEN + plugin.getIssuesURL());

        // Next line should always remain at the end of the About
        pre.add(center("§BCopyright © 2013 §AVisual §6I§9l§Bl§4u§As§2i§5o§En§7s §6Entertainment"));
        about = Collections.unmodifiableList(pre);
        try {
            Canary.commands().registerCommands(this, plugin, false);
        }
        catch (CommandDependencyException ex) {
        }
    }

    protected final String center(String toCenter) {
        String strColorless = TextFormat.removeFormatting(toCenter);
        return StringUtils.padCharLeft(toCenter, (int)(Math.floor(63 - strColorless.length()) / 2), ' ');
    }
}
