package kangarko.chatcontrol.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

import kangarko.chatcontrol.config.Settings;
import kangarko.chatcontrol.utils.Common;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

public class VaultHook extends Hook {
	
	private static Chat chat;
	private static Economy economy;

	private VaultHook() {
	}

	public static String getPlayerPrefix(Player pl) {
		if (chat == null)
			return "";

		return chat.getPlayerPrefix(pl);
	}

	public static String getPlayerSuffix(Player pl) {
		if (chat == null)
			return "";

		return chat.getPlayerSuffix(pl);
	}

	@SuppressWarnings("deprecation")
	public static void takeMoney(String player, double amount) {
		if (economy != null)
			economy.withdrawPlayer(player, amount);
	}
	
	static {
		HOOKED = Common.doesPluginExist("Vault");
		
		ServicesManager services = Bukkit.getServicesManager();

		RegisteredServiceProvider<Economy> economyProvider = services.getRegistration(Economy.class);		
		if (economyProvider != null)
			economy = economyProvider.getProvider();
		else
			Common.Log("&cEconomy plugin not found");
		
		RegisteredServiceProvider<Chat> chatProvider = services.getRegistration(Chat.class);		
		if (chatProvider != null)
			chat = chatProvider.getProvider();
		else if (Settings.Chat.Formatter.ENABLED)
			Common.LogInFrame(true, "You have enabled chat formatter", "but no permissions and chat", "plugin was found!", "Run /vault-info and check what is missing");
	}
}
