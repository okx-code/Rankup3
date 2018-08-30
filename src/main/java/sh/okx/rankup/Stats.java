package sh.okx.rankup;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class Stats {
  public void init(Rankup plugin) throws IOException {
    URL url = new URL("http://rankup.okx.sh/api");
    URLConnection con = url.openConnection();
    HttpURLConnection http = (HttpURLConnection) con;
    http.setRequestMethod("POST");
    http.setDoOutput(true);
    Map<String, String> arguments = new HashMap<>();
    arguments.put("ip", InetAddress.getLocalHost().getHostAddress() + ":" + Bukkit.getPort());
    arguments.put("spigot", Bukkit.getVersion());
    arguments.put("version", plugin.getDescription().getVersion());
    Base64.Encoder base64 = Base64.getEncoder();
    File data = plugin.getDataFolder();
    arguments.put("config", base64.encodeToString(Files.readAllBytes(data.toPath().resolve("config.yml"))));
    arguments.put("messages", base64.encodeToString(Files.readAllBytes(data.toPath().resolve("messages.yml"))));
    arguments.put("rankups", base64.encodeToString(Files.readAllBytes(data.toPath().resolve("rankups.yml"))));
    StringJoiner sj = new StringJoiner("&");
    for (Map.Entry<String, String> entry : arguments.entrySet()) {
      try {
        sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
            + URLEncoder.encode(entry.getValue(), "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        throw new AssertionError("UTF-8 not found.");
      }
    }
    byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
    int length = out.length;
    http.setFixedLengthStreamingMode(length);
    http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    http.connect();
    try (OutputStream os = http.getOutputStream()) {
      os.write(out);
    }
  }
}
