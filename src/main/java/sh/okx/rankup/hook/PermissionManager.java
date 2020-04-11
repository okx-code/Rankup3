package sh.okx.rankup.hook;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import sh.okx.rankup.RankupPlugin;

public class PermissionManager {
  private final RankupPlugin plugin;

  public PermissionManager(RankupPlugin plugin) {
    this.plugin = plugin;
  }

  public PermissionProvider findPermissionProvider() {
    return getVaultPermissionProvider();
  }

  private PermissionProvider getVaultPermissionProvider() {
    RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager()
        .getRegistration(Permission.class);
    if (rsp == null) {
      return null;
    }
    Permission provider = rsp.getProvider();
    if (!provider.hasGroupSupport()) {
      return null;
    }
    return new VaultPermissionProvider(provider);
  }
}
