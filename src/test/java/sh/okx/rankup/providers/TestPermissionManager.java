package sh.okx.rankup.providers;

import sh.okx.rankup.hook.GroupProvider;
import sh.okx.rankup.hook.PermissionManager;

public class TestPermissionManager implements PermissionManager {
    private final GroupProvider groupProvider;

    public TestPermissionManager(GroupProvider groupProvider) {
        this.groupProvider = groupProvider;
    }

    @Override
    public GroupProvider findPermissionProvider() {
        return groupProvider;
    }

    @Override
    public GroupProvider permissionOnlyProvider() {
        return groupProvider;
    }
}
