package sh.okx.rankup.requirements.requirement.towny;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
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
            Town town = TownyUniverse.getDataSource().getResident(player.getName()).getTown();

            return town != null;
        } catch (NotRegisteredException e) {
            return false;
        }
    }

    public Resident getResident(Player player) {
        try {
            return TownyUniverse.getDataSource().getResident(player.getName());
        } catch (NotRegisteredException e) {
            return null;
        }
    }

    public Town getTown(Player player) {
        try {
            return TownyUniverse.getDataSource().getResident(player.getName()).getTown();
        } catch (NotRegisteredException e) {
            return null;
        }
    }

    public Nation getNation(Player player) {
        Town town = getTown(player);

        try {
            return getTown(player) == null ? null : town.getNation();
        } catch (NotRegisteredException e) {
            return null;
        }
    }

    public boolean isMayor(Player player) {
        try {
            return TownyUniverse.getDataSource().getResident(player.getName()).isMayor();
        } catch (NotRegisteredException e) {
            return false;
        }
    }

    public boolean isKing(Player player) {
        try {
            return TownyUniverse.getDataSource().getResident(player.getName()).isKing();
        } catch (NotRegisteredException e) {
            return false;
        }
    }
}
