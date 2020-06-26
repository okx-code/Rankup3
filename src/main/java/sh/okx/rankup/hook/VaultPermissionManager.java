package sh.okx.rankup.hook;

import net.milkbowl.vault.permission.Permission;
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
    return new VaultGroupProvider(provider);
  }

  @Override
  public GroupProvider permissionOnlyProvider() {
    return new PermissionGroupProvider();
  }
}
