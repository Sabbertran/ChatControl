package kangarko.chatcontrol;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import kangarko.chatcontrol.config.Settings;
import kangarko.chatcontrol.hooks.HookManager;
import kangarko.chatcontrol.utils.Common;
import kangarko.chatcontrol.utils.CompatProvider;
import kangarko.chatcontrol.utils.Permissions;
import kangarko.chatcontrol.utils.Writer;

public class ChatFormatter implements Listener {

	private final Pattern COLOR_REGEX = Pattern.compile("(?i)&([0-9A-F])");
	private final Pattern MAGIC_REGEN = Pattern.compile("(?i)&([K])");
	private final Pattern BOLD_REGEX = Pattern.compile("(?i)&([L])");
	private final Pattern STRIKETHROUGH_REGEX = Pattern.compile("(?i)&([M])");
	private final Pattern UNDERLINE_REGEX = Pattern.compile("(?i)&([N])");
	private final Pattern ITALIC_REGEX = Pattern.compile("(?i)&([O])");
	private final Pattern RESET_REGEX = Pattern.compile("(?i)&([R])");

	@EventHandler(ignoreCancelled = true)
	public void onChatFormat(AsyncPlayerChatEvent e) {
		Player pl = e.getPlayer();
		String msg = e.getMessage();

		String format = Settings.Chat.Formatter.FORMAT;
		boolean rangedChat = Settings.Chat.Formatter.RANGED_MODE;

		if (rangedChat && msg.startsWith("!") && Common.hasPerm(pl, Permissions.Formatter.GLOBAL_CHAT)) {
			rangedChat = false;
			msg = msg.substring(1);

			format = Settings.Chat.Formatter.GLOBAL_FORMAT;
		}

		msg = formatColor(msg, pl);

		format = format.replace("%message", "%2$s").replace("%displayname", "%1$s");
		format = replaceAllVariables(pl, format);

		e.setFormat(format);
		e.setMessage(msg);

		if (rangedChat) {
			e.getRecipients().clear();
			e.getRecipients().addAll(getLocalRecipients(pl, msg, Settings.Chat.Formatter.RANGE));
		}
	}

	private String replaceAllVariables(Player pl, String msg) {
		msg = formatColor(msg);
		msg = replacePlayerVariables(pl, msg);
		msg = replaceTime(msg);

		return msg;
	}

	public String replacePlayerVariables(Player pl, String msg) {
		msg = msg.replace("%countrycode", HookManager.getCountryCode(pl))
				.replace("%countryname", HookManager.getCountryName(pl))

				.replace("%pl_prefix", formatColor(HookManager.getPlayerPrefix(pl)))
				.replace("%pl_suffix", formatColor(HookManager.getPlayerSuffix(pl)))

				.replace("%player", pl.getName())
				.replace("%world", HookManager.getWorldAlias(pl.getWorld().getName()))
				.replace("%health", formatHealth(pl) + ChatColor.RESET)

				.replace("%town", HookManager.getTownName(pl))
				.replace("%nation", HookManager.getNation(pl))
				.replace("%clan", HookManager.getClanTag(pl));

		return msg;
	}

	private String replaceTime(String msg) {
		Calendar c = Calendar.getInstance();

		if (msg.contains("%h"))
			msg = msg.replace("%h", String.format("%02d", c.get(Calendar.HOUR)));

		if (msg.contains("%H"))
			msg = msg.replace("%H", String.format("%02d", c.get(Calendar.HOUR_OF_DAY)));

		if (msg.contains("%g"))
			msg = msg.replace("%g", Integer.toString(c.get(Calendar.HOUR)));

		if (msg.contains("%G"))
			msg = msg.replace("%G", Integer.toString(c.get(Calendar.HOUR_OF_DAY)));

		if (msg.contains("%i"))
			msg = msg.replace("%i", String.format("%02d", c.get(Calendar.MINUTE)));

		if (msg.contains("%s"))
			msg = msg.replace("%s", String.format("%02d", c.get(Calendar.SECOND)));

		if (msg.contains("%a"))
			msg = msg.replace("%a", c.get(Calendar.AM_PM) == 0 ? "am" : "pm");

		if (msg.contains("%A"))
			msg = msg.replace("%A", c.get(Calendar.AM_PM) == 0 ? "AM" : "PM");

		return msg;
	}

	private String formatColor(String msg) {
		return Common.colorize(msg);
	}

	private String formatColor(String msg, Player pl) {
		if (msg == null)
			return "";

		if (Common.hasPerm(pl, Permissions.Formatter.COLOR))
			msg = COLOR_REGEX.matcher(msg).replaceAll("\u00A7$1");

		if (Common.hasPerm(pl, Permissions.Formatter.MAGIC))
			msg = MAGIC_REGEN.matcher(msg).replaceAll("\u00A7$1");

		if (Common.hasPerm(pl, Permissions.Formatter.BOLD))
			msg = BOLD_REGEX.matcher(msg).replaceAll("\u00A7$1");

		if (Common.hasPerm(pl, Permissions.Formatter.STRIKETHROUGH))
			msg = STRIKETHROUGH_REGEX.matcher(msg).replaceAll("\u00A7$1");

		if (Common.hasPerm(pl, Permissions.Formatter.UNDERLINE))
			msg = UNDERLINE_REGEX.matcher(msg).replaceAll("\u00A7$1");

		if (Common.hasPerm(pl, Permissions.Formatter.ITALIC))
			msg = ITALIC_REGEX.matcher(msg).replaceAll("\u00A7$1");

		msg = RESET_REGEX.matcher(msg).replaceAll("\u00A7$1");

		return msg;
	}

	private String formatHealth(Player pl) {
		int health = (int) pl.getHealth();

		return (health > 10 ? ChatColor.DARK_GREEN : health > 5 ? ChatColor.GOLD : ChatColor.RED) + "" + health;
	}

	private List<Player> getLocalRecipients(Player pl, String msg, double range) {
		List<Player> recipients = new LinkedList<Player>();

		try {
			Location playerLocation = pl.getLocation();
			double squaredDistance = Math.pow(range, 2.0D);

			for (Player receiver : CompatProvider.getAllPlayers()) {
				if (receiver.getWorld().getName().equals(pl.getWorld().getName()))
					if (Common.hasPerm(pl, Permissions.Formatter.OVERRIDE_RANGED_WORLD) || playerLocation.distanceSquared(receiver.getLocation()) <= squaredDistance) {
						recipients.add(receiver);
						continue;
					}

				if (Common.hasPerm(receiver, Permissions.Formatter.SPY))
					Common.tell(receiver, replaceAllVariables(pl, Settings.Chat.Formatter.SPY_FORMAT.replace("%message", msg).replace("%displayname", pl.getDisplayName())));
			}

			return recipients;
		} catch (ArrayIndexOutOfBoundsException ex) {
			Common.Log("(Range Chat) Got " + ex.getMessage() + ", trying (limited) backup.");
			Writer.Write(Writer.ERROR_PATH, "Range Chat", pl.getName() + ": \'" + msg + "\' Resulted in error: " + ex.getMessage());

			if (Common.hasPerm(pl, Permissions.Formatter.OVERRIDE_RANGED_WORLD)) {
				for (Player recipient : CompatProvider.getAllPlayers())
					if (recipient.getWorld().equals(pl.getWorld()))
						recipients.add(recipient);

			} else {
				for (Entity en : pl.getNearbyEntities(range, range, range))
					if (en.getType() == EntityType.PLAYER)
						recipients.add((Player) en);
			}
		}

		return recipients;
	}
}