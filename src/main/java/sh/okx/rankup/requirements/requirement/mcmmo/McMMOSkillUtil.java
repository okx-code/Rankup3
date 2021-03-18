package sh.okx.rankup.requirements.requirement.mcmmo;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bukkit.entity.Player;

/**
 * Because mcMMO like changing the name of their skill types.
 * Singleton class to access different mcMMO versions.
 */
public class McMMOSkillUtil {
  private static McMMOSkillUtil instance;

  private Class<?> skillTypeClass;
  private Method values;
  private Method valueOf;
  //private Class<?> userManagerClass;
  private Method getSkillLevel;

  private McMMOSkillUtil() {
    final String pckg = "com.gmail.nossr50.datatypes.skills.";
    try {
      skillTypeClass = Class.forName(pckg + "PrimarySkillType");
    } catch (ClassNotFoundException e0) {
      try {
        skillTypeClass = Class.forName(pckg + "PrimarySkill");
      } catch (ClassNotFoundException e1) {
        try {
          skillTypeClass = Class.forName(pckg + "SkillType");
        } catch (ClassNotFoundException e2) {
          throw new UnsupportedOperationException("mcMMO Skill Type class not found");
        }
      }
    }
    try {
      values = skillTypeClass.getMethod("values");
    } catch (NoSuchMethodException e) {
      throw new UnsupportedOperationException("mcMMO " + skillTypeClass + ".values() not found");
    }
    try {
      valueOf = skillTypeClass.getMethod("valueOf", String.class);
    } catch (NoSuchMethodException e) {
      throw new UnsupportedOperationException("mcMMO" + skillTypeClass + ".valueOf(String) not found");
    }

    /*try {
      userManagerClass = Class.forName("com.gmail.nossr50.util.player.UserManager");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("mcMMO UserManager class not found");
    }*/

    try {
      getSkillLevel = McMMOPlayer.class.getMethod("getSkillLevel", skillTypeClass);
    } catch (NoSuchMethodException e) {
      throw new UnsupportedOperationException("mcMMO UserManager.getSkillLevel(" + skillTypeClass + ") not found");
    }
  }

  public static McMMOSkillUtil getInstance() {
    if (instance == null) {
      instance = new McMMOSkillUtil();
    }
    return instance;
  }

  /*public String[] getSkills() {
    try {
      Enum<?>[] skills = (Enum<?>[]) values.invoke(null);
      String[] stringSkills = new String[skills.length];
      for (int i = 0; i < skills.length; i++) {
        stringSkills[i] = skills[i].name();
      }
      return stringSkills;
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }*/

  public int getSkillLevel(Player player, String skill) {
    McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);
    try {
      Object skillType = skillTypeClass.cast(valueOf.invoke(null, skill.toUpperCase()));
      return (int) getSkillLevel.invoke(mcMMOPlayer, skillType);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
