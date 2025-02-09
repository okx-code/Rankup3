package sh.okx.rankup.hook;

import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import sh.okx.rankup.RankupPlugin;

public class VaultPermissionManager implements PermissionManager {
  private final RankupPlugin plugin;

  public VaultPermissionManager(RankupPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public GroupProvider findPermissionProvider() {
    return getVaultPermissionProvider();
  }

  private GroupProvider getVaultPermissionProvider() {
    RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager()
        .getRegistration(Permission.class);
    if (rsp == null) {
      return null;
    }
    Permission provider = rsp.getProvider();
    if (!provider.hasGroupSupport()) {
      return null;
    }
    String lpContext = plugin.getConfig().getString("luckperms-context");
    boolean useLuckPermsGroupNames = plugin.getConfig().getBoolean("use-luckperms-group-names", false);
    if (useLuckPermsGroupNames || (lpContext != null && !lpContext.isEmpty())) {
      RegisteredServiceProvider<LuckPerms> lpProvider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
      if (lpProvider != null) {
        return LuckPermsGroupProvider.createFromString(lpProvider.getProvider(), lpContext);
      }
    }

    return new VaultGroupProvider(provider);
  }

  @Override
  public GroupProvider permissionOnlyProvider() {
    return new PermissionGroupProvider();
  }
}
