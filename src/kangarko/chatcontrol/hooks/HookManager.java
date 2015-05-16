package kangarko.chatcontrol.hooks;

import kangarko.chatcontrol.utils.Common;

public class HookManager {

	private static final HookManager instance = new HookManager();
	
	public static HookManager getInstance() {
		return instance;
	}
	
	public AuthMeHook authMe;
	public EssentialsHook essentials;
	public MultiverseHook multiverse;
	public ProtocolLibHook protocolLib;
	public RushCoreHook rushCore;
	public SimpleClansHook simpleClans;
	public TownyHook towny;
	public VaultHook vault;
	
	private HookManager() {		
		loadDependencies();
	}
	
	private void loadDependencies() {
		if (Common.doesPluginExist("AuthMe"))
			authMe = new AuthMeHook();
		
		if (Common.doesPluginExist("Essentials"))
			essentials = new EssentialsHook();
		
		if (Common.doesPluginExist("Multiverse-Core"))
			multiverse = new MultiverseHook();
		
		if (Common.doesPluginExist("ProtocolLib"))
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
	
	public boolean isAuthMeLoaded() {
		return authMe != null;
	}
	
	public boolean isEssentialsIs() {
		return essentials != null;
	}
	
	public boolean isMultiverseLoaded() {
		return multiverse != null;
	}
	
	public boolean isProtocolLibLoaded() {
		return protocolLib != null;
	}
	
	public boolean isRushCoreLoaded() {
		return rushCore != null;
	}
	
	public boolean isSimpleClansLoaded() {
		return simpleClans != null;
	}
	
	public boolean isTownyLoaded() {
		return towny != null;
	}
	
	public boolean isVaultLoaded() {
		return vault != null;
	}
}
