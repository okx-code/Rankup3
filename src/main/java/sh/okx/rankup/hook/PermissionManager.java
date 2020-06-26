package sh.okx.rankup.hook;

public interface PermissionManager {

    GroupProvider findPermissionProvider();

    GroupProvider permissionOnlyProvider();
}
