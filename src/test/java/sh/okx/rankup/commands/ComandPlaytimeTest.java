package sh.okx.rankup.commands;

import static org.junit.jupiter.api.Assertions.*;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.junit.jupiter.api.Test;
import sh.okx.rankup.RankupTest;

public class ComandPlaytimeTest extends RankupTest {
  @Test
  public void testAdd() {
    PlayerMock player = server.addPlayer();

    player.setStatistic(Statistic.PLAY_ONE_MINUTE, ticks(10));

    player.addAttachment(plugin, "rankup.playtime", true);
    player.performCommand("pru playtime add " + player.getName() + " 20");

    assertEquals(ticks(30), player.getStatistic(Statistic.PLAY_ONE_MINUTE));
  }

  @Test
  public void testSet() {
    PlayerMock player = server.addPlayer();

    player.setStatistic(Statistic.PLAY_ONE_MINUTE, ticks(20));

    player.addAttachment(plugin, "rankup.playtime", true);
    player.performCommand("pru playtime set " + player.getName() + " 25");

    assertEquals(ticks(25), player.getStatistic(Statistic.PLAY_ONE_MINUTE));
  }

  @Test
  public void testGetSelf() {
    PlayerMock player = server.addPlayer();

    player.setStatistic(Statistic.PLAY_ONE_MINUTE, ticks(5));

    player.addAttachment(plugin, "rankup.playtime.get", true);
    player.performCommand("pru playtime get " + player.getName());

    player.assertSaid(ChatColor.LIGHT_PURPLE + "You have played for 5 minutes.");
    player.assertNoMoreSaid();
  }

  private int ticks(int minutes) {
    return minutes * 20 * 60;
  }

  private int minutes(int ticks) {
    return ticks / 20 / 60;
  }
}
