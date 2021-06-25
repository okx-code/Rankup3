package sh.okx.rankup.providers;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.UUID;
import sh.okx.rankup.hook.GroupProvider;

public class TestGroupProvider implements GroupProvider {
    private Multimap<UUID, String> groups = ArrayListMultimap.create();

    @Override
    public boolean inGroup(UUID uuid, String group) {
        return groups.containsEntry(uuid, group.toLowerCase());
    }

    @Override
    public void addGroup(UUID uuid, String group) {
        groups.put(uuid, group.toLowerCase());
    }

    @Override
    public void removeGroup(UUID uuid, String group) {
        groups.remove(uuid, group.toLowerCase());
    }
}
