package sh.okx.rankup.util;

import org.bukkit.Material;

public class ItemUtil {
  private static boolean flattened;

  static {
    try {
      Material.valueOf("BLACK_STAINED_GLASS_PANE");
      flattened = true;
    } catch (IllegalArgumentException e) {
      flattened = false;
    }
  }

  /**
   * Determines if a server is post-flattening or pre-flattening.
   * The flattening is the name for the event where, instead of using durability to represent
   * similar items, Mojang decided to use distinct item types for each item.
   * This caused many {@link Material} names to change, making some things incompatible.
   * The flattening happened in 1.13.
   *
   * @return true if the server is post-flattening (server versions 1.13, 1.14, 1.15) or false if it is pre-flattening (1.12, 1.11, 1.10 etc)
   */
  public static boolean isServerFlattened() {
    return flattened;
  }
}
