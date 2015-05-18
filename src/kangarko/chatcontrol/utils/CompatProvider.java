package kangarko.chatcontrol.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class CompatProvider {

	private static Method getPlayersMethod;
	private static Method getHealthMethod;

	private static boolean isGetPlayersCollection = false;
	private static boolean isGetHealthDouble = false;

	private CompatProvider() {
	}

	public static void setupReflection() {
		try {
			Class.forName("org.bukkit.Sound"); // test for too old craftbukkits

			getPlayersMethod = Bukkit.class.getMethod("getOnlinePlayers");
			isGetPlayersCollection = getPlayersMethod.getReturnType() == Collection.class;

			getHealthMethod = Player.class.getMethod("getHealth");
			isGetHealthDouble = getHealthMethod.getReturnType() == double.class;

		} catch (ReflectiveOperationException ex) {
			throw new UnsupportedOperationException();
		}

		Common.Debug("&7[Reflection] &bUsing " + (isGetPlayersCollection ? "&anew (collection)" : "&cold (array)") + " &bgetter for players");
		Common.Debug("&7[Reflection] &bUsing " + (isGetHealthDouble ? "&anew (double)" : "&cold (int)") + " &bgetter for player health");
	}

	public static YamlConfiguration loadConfiguration(InputStream is) {
		try {
			return YamlConfiguration.loadConfiguration(new InputStreamReader(is, StandardCharsets.UTF_8));
		} catch (NoSuchMethodError ex) {
			return YamlConfiguration.loadConfiguration(is);
		}
	}

	public static int getHealth(Player pl) {
		return isGetHealthDouble ? (int) pl.getHealth() : getHealhLegacy(pl);
	}

	public static Collection<? extends Player> getAllPlayers() {
		return isGetPlayersCollection ? Bukkit.getOnlinePlayers() : Arrays.asList(getPlayersLegacy());
	}

	// ------------------------ Legacy ------------------------

	private static Player[] getPlayersLegacy() {
		try {
			return (Player[]) getPlayersMethod.invoke(null);
		} catch (ReflectiveOperationException ex) {
			throw new RuntimeException("Reflection malfunction", ex);
		}
	}

	private static int getHealhLegacy(Player pl) {
		try {
			return (int) getHealthMethod.invoke(pl);
		} catch (ReflectiveOperationException ex) {
			throw new RuntimeException("Reflection malfunction", ex);
		}
	}
}