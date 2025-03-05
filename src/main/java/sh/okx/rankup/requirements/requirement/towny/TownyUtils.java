package sh.okx.rankup.requirements.requirement.towny;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.entity.Player;

public class TownyUtils {
    private static TownyUtils instance;

    public static TownyUtils getInstance() {
        if (instance == null) {
            instance = new TownyUtils();
        }
        return instance;
    }

    public boolean isResident(Player player) {
        try {
            Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
            if (resident == null) {
                return false;
            }
            Town town = resident.getTown();

            return town != null;
        } catch (NotRegisteredException e) {
            return false;
        }
    }

    public Town getTown(Player player) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
        if (resident == null) {
            return null;
        }
        return resident.getTownOrNull();
    }

    public Nation getNation(Player player) {
        Town town = getTown(player);

        try {
            return town == null ? null : town.getNation();
        } catch (NotRegisteredException e) {
            return null;
        }
    }

    public boolean isMayor(Player player) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());
        if (resident == null) {
            return false;
        }
        return resident.isMayor();
    }

    public boolean isKing(Player player) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());
        if (resident == null) {
            return false;
        }
        return resident.isKing();
    }
}
