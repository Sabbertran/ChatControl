package kangarko.chatcontrol.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import fr.xephi.authme.api.API;
import kangarko.chatcontrol.ChatControl;
import kangarko.chatcontrol.config.Settings;
import kangarko.chatcontrol.rules.ChatCeaser.PacketCancelledException;
import kangarko.chatcontrol.utils.Common;
import kangarko.chatcontrol.utils.Permissions;
import kangarko.rushcore.misc.PlayerInfo;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public class HookManager {

	private static AuthMeHook authMe;
	private static EssentialsHook essentials;
	private static MultiverseHook multiverse;
	private static ProtocolLibHook protocolLib;
	private static RushCoreHook rushCore;
	private static SimpleClansHook simpleClans;
	private static TownyHook towny;
	private static VaultHook vault;

	private HookManager() {	
	}

	public static void loadDependencies() {
		if (Common.doesPluginExist("AuthMe", "Country Variables"))
			authMe = new AuthMeHook();

		if (Common.doesPluginExist("Essentials"))
			essentials = new EssentialsHook();

		if (Common.doesPluginExist("Multiverse-Core", "World Alias"))
			multiverse = new MultiverseHook();

		if (Common.doesPluginExist("ProtocolLib", "Packet Features"))
			protocolLib = new ProtocolLibHook();

		if (Common.doesPluginExist("RushCore"))
			rushCore = new RushCoreHook();

		if (Common.doesPluginExist("SimpleClans"))
			simpleClans = new SimpleClansHook();

		if (Common.doesPluginExist("Towny"))
			towny = new TownyHook();

		if (Common.doesPluginExist("Vault"))
			vault = new VaultHook();
	}

	public static boolean isAuthMeLoaded() {
		return authMe != null;
	}

	public static boolean isEssentialsLoaded() {
		return essentials != null;
	}

	public static boolean isMultiverseLoaded() {
		return multiverse != null;
	}

	public static boolean isProtocolLibLoaded() {
		return protocolLib != null;
	}

	public static boolean isRushCoreLoaded() {
		return rushCore != null;
	}

	public static boolean isSimpleClansLoaded() {
		return simpleClans != null;
	}

	public static boolean isTownyLoaded() {
		return towny != null;
	}

	public static boolean isVaultLoaded() {
		return vault != null;
	}

	// ------------------ delegate methods, reason it's here = prevent errors when class loads but plugin is missing

	public static String getCountryCode(Player pl) {
		return isAuthMeLoaded() ? authMe.getCountryCode(pl) : "";
	}

	public static String getCountryName(Player pl) {
		return isAuthMeLoaded() ? authMe.getCountryName(pl) : "";
	}

	public static boolean isLogged(Player pl) {
		return isAuthMeLoaded() ? authMe.isLogged(pl) : true;
	}

	public static boolean isAfk(String pl) {
		return isEssentialsLoaded() ? essentials.isAfk(pl) : false;
	}

	public static Player getReplyTo(String pl) {
		return isEssentialsLoaded() ? essentials.getReplyTo(pl) : null;
	}

	public static String getWorldAlias(String world) {
		return isMultiverseLoaded() ? multiverse.getWorldAlias(world) : world;
	}

	public static void initPacketListening() {
		if (isProtocolLibLoaded())
			protocolLib.initPacketListening();
	}

	public static boolean moznoPrehratZvuk(String hraca) {
		return isRushCoreLoaded() ? rushCore.moznoPrehratZvuk(hraca) : true;
	}

	public static boolean moznoZobrazitSpravu(String hraca) {
		return isRushCoreLoaded() ? rushCore.moznoZobrazitSpravu(hraca) : true;
	}

	public static String getClanTag(Player pl) {
		return isSimpleClansLoaded() ? simpleClans.getClanTag(pl) : "";
	}

	public static String getNation(Player pl) {
		return isTownyLoaded() ? towny.getNation(pl) : "";
	}

	public static String getTownName(Player pl) {
		return isTownyLoaded() ? towny.getTownName(pl) : "";
	}

	public static String getPlayerPrefix(Player pl) {
		return isVaultLoaded() ? vault.getPlayerPrefix(pl) : "";
	}

	public static String getPlayerSuffix(Player pl) {
		return isVaultLoaded() ? vault.getPlayerSuffix(pl) : "";
	}

	public static void takeMoney(String player, double amount) {
		if (isVaultLoaded())
			vault.takeMoney(player, amount);
	}
}

class AuthMeHook {
	
	String getCountryCode(Player pl) {
		String ip = pl.getAddress().toString().replace("/", "");
		
		return API.instance.getCountryCode(ip);
	}

	String getCountryName(Player pl) {
		String ip = pl.getAddress().toString().replace("/", "");
		
		return API.instance.getCountryName(ip);
	}
	
	boolean isLogged(Player pl) {
		return API.isAuthenticated(pl);
	}
}

class EssentialsHook {
	
	private final Essentials ess;
	
	EssentialsHook() {
		ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
	}

	boolean isAfk(String pl) {		
		User user = getUser(pl);
		
		return user != null ? user.isAfk() : false;
	}

	Player getReplyTo(String pl) {
		CommandSource source = getUser(pl).getReplyTo();

		if (source != null && source.isPlayer()) {
			Player player = source.getPlayer();
			if (player != null && player.isOnline())
				return player;
		}

		return null;
	}
	
	private User getUser(String pl) {
		return ess.getUserMap().getUser(pl);
	}

}

class MultiverseHook {

	private final MultiverseCore multiVerse;

	MultiverseHook() {
		multiVerse = (MultiverseCore) Bukkit.getPluginManager().getPlugin("Multiverse-Core");
	}

	String getWorldAlias(String world) {		
		MultiverseWorld mvWorld = multiVerse.getMVWorldManager().getMVWorld(world);

		if (mvWorld != null)
			return mvWorld.getColoredWorldString();

		return world;
	}
}

class RushCoreHook {

	boolean moznoPrehratZvuk(String hraca) {
		return PlayerInfo.of(hraca).zvukRp;
	}
	
	boolean moznoZobrazitSpravu(String hraca) {
		return PlayerInfo.of(hraca).spravyTip;
	}
}

class SimpleClansHook {

	private final SimpleClans clans;

	SimpleClansHook() {
		clans = (SimpleClans) Bukkit.getPluginManager().getPlugin("SimpleClans");
	}

	String getClanTag(Player pl) {
		ClanPlayer clanPl = clans.getClanManager().getClanPlayer(pl);

		if (clanPl != null) {
			Clan clan = clanPl.getClan();

			if (clan != null)
				clan.getColorTag();
		}

		return "";
	}
}

class TownyHook {

	String getNation(Player pl) {
		try {
			Town t = getTown(pl);

			return t != null ? t.getNation().getName() : "";
		} catch (Exception e) {
			return "";
		}
	}

	String getTownName(Player pl) {		
		Town t = getTown(pl);
		
		return t != null ? t.getName() : "";
	}

	private Town getTown(Player pl) {
		try {
			Resident res = TownyUniverse.getDataSource().getResident(pl.getName());

			if (res != null)
				return res.getTown();
		} catch (Throwable e) {
		}

		return null;
	}
}

class ProtocolLibHook {

	private final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
	private final JSONParser parser = new JSONParser();

	void initPacketListening() {

		if (Settings.Packets.TabComplete.DISABLE)
			manager.addPacketListener(new PacketAdapter(ChatControl.instance(), PacketType.Play.Client.TAB_COMPLETE) {

				@Override
				public void onPacketReceiving(PacketEvent e) {
					if (Common.hasPerm(e.getPlayer(), Permissions.Bypasses.TAB_COMPLETE))
						return;

					String msg = e.getPacket().getStrings().read(0);

					if (Settings.Packets.TabComplete.DISABLE_ONLY_IN_CMDS && !msg.startsWith("/"))
						return;

					if (Settings.Packets.TabComplete.ALLOW_IF_SPACE && msg.contains(" "))
						return;

					if (msg.length() > Settings.Packets.TabComplete.IGNORE_ABOVE_LENGTH)
						e.setCancelled(true);
				}
			});

		if (Settings.Rules.CHECK_PACKETS)
			manager.addPacketListener(new PacketAdapter(ChatControl.instance(), PacketType.Play.Server.CHAT) {

				@Override
				public void onPacketSending(PacketEvent e) {
					if (e.getPlayer() == null || !e.getPlayer().isOnline())
						return;

					StructureModifier<WrappedChatComponent> chat = e.getPacket().getChatComponents();

					String raw = chat.read(0).getJson();
					if (raw == null || raw.isEmpty())
						return;

					Object parsed;

					try {
						parsed = parser.parse(raw);
					} catch (Throwable t) {
						return;
					}

					if (!(parsed instanceof JSONObject))
						return;

					JSONObject json = (JSONObject) parsed;					
					String origin = json.toJSONString();

					try {
						ChatControl.instance().chatCeaser.parsePacketRules(e.getPlayer(), json);
					} catch (PacketCancelledException ex) {
						e.setCancelled(true);
						return;
					}

					if (!json.toJSONString().equals(origin))
						chat.write(0, WrappedChatComponent.fromJson(json.toJSONString()));
				}
			});
	}
}

class VaultHook {
	
	private Chat chat;
	private Economy economy;

	VaultHook() {
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

	String getPlayerPrefix(Player pl) {
		if (chat == null)
			return "";

		return chat.getPlayerPrefix(pl);
	}

	String getPlayerSuffix(Player pl) {
		if (chat == null)
			return "";

		return chat.getPlayerSuffix(pl);
	}

	@SuppressWarnings("deprecation")
	void takeMoney(String player, double amount) {
		if (economy != null)
			economy.withdrawPlayer(player, amount);
	}
}