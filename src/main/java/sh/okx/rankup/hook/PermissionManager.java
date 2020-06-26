package sh.okx.rankup.hook;

public interface PermisionManager {
    GroupProvider findPermissionProvider();

    GroupProvider permissionOnlyProvider();
}
