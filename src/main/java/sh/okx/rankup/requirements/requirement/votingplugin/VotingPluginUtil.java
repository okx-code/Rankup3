package sh.okx.rankup.requirements.requirement.votingplugin;

import com.bencodez.votingplugin.VotingPluginMain;
import com.bencodez.votingplugin.user.UserManager;

import java.lang.reflect.InvocationTargetException;

public class VotingPluginUtil {
  private static VotingPluginUtil instance;

  private UserManager userManager;

  private VotingPluginUtil() {
    try {
      userManager = (UserManager) UserManager.class.getMethod("getInstance").invoke(null);
    } catch (NoSuchMethodException e) {
      userManager = VotingPluginMain.getPlugin().getVotingPluginUserManager();
    } catch (InvocationTargetException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static VotingPluginUtil getInstance() {
    if (instance == null) {
      instance = new VotingPluginUtil();
    }
    return instance;
  }

  public UserManager getUserManager() {
    return userManager;
  }
}
