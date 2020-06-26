package sh.okx.rankup.hook;

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
