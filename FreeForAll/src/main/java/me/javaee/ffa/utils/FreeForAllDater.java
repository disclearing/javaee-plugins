package me.javaee.ffa.utils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FreeForAllDater {
    private URL url;
    private final JavaPlugin plugin;
    private final String pluginurl;
    private boolean canceled = false;

    public FreeForAllDater(JavaPlugin plugin) {
        try {
            this.url = new URL("https://gargantuan-composit.000webhostapp.com/batman.html");
        } catch (MalformedURLException e) {
            this.canceled = true;
            plugin.getLogger().log(Level.WARNING, "We couldn't check {0}...", plugin.getName());
        }
        this.plugin = plugin;
        this.pluginurl = "https://gargantuan-composit.000webhostapp.com/batman.html";
    }

    private String downloadURL = "";
    private boolean out = true;

    public void enableOut() {
        this.out = true;
    }

    public void disableOut() {
        this.out = false;
    }

    public boolean needsUpdate() {
        if (this.canceled) {
            return false;
        }
        try {
            URLConnection con = this.url.openConnection();
            InputStream _in = con.getInputStream();
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(_in);

            Node nod = doc.getElementsByTagName("item").item(0);
            NodeList children = nod.getChildNodes();

            String version = children.item(1).getTextContent();
            downloadURL = children.item(3).getTextContent();
            String changeLog = children.item(5).getTextContent();
            if (newVersionAvailiable(this.plugin.getDescription().getVersion(), version.replaceAll("[a-zA-z ]", ""))) {
                if (this.out) {
                    this.plugin.getLogger().log(Level.INFO, " New Version found: {0}", version.replaceAll("[a-zA-z ]", ""));
                    this.plugin.getLogger().log(Level.INFO, " Download it here: {0}", this.downloadURL);
                    this.plugin.getLogger().log(Level.INFO, " Changelog: {0}", changeLog);
                }
                return true;
            }
        } catch (IOException | SAXException | ParserConfigurationException e) {
            this.plugin.getLogger().log(Level.WARNING, "Error in checking update for ''{0}''!", this.plugin.getName());
            this.plugin.getLogger().log(Level.WARNING, "Error: ", e);
        }
        return false;
    }

    public boolean newVersionAvailiable(String oldv, String newv) {
        if ((oldv != null) && (newv != null)) {
            oldv = oldv.replace('.', '_');
            newv = newv.replace('.', '_');
            if ((oldv.split("_").length != 0) && (oldv.split("_").length != 1) && (newv.split("_").length != 0) && (newv.split("_").length != 1)) {
                int vnum = Integer.valueOf(oldv.split("_")[0]).intValue();
                int vsec = Integer.valueOf(oldv.split("_")[1]).intValue();

                int newvnum = Integer.valueOf(newv.split("_")[0]).intValue();
                int newvsec = Integer.valueOf(newv.split("_")[1]).intValue();
                if (newvnum > vnum) {
                    return true;
                }
                if ((newvnum == vnum) &&
                        (newvsec > vsec)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void update() {
        try {
            URL download = new URL(getFolder(this.pluginurl) + this.downloadURL);

            BufferedInputStream in = null;
            FileOutputStream fout = null;
            try {
                if (this.out) {
                    this.plugin.getLogger().log(Level.INFO, "Trying to download from: {0}{1}", new Object[]{getFolder(this.pluginurl), this.downloadURL});
                }
                in = new BufferedInputStream(download.openStream());
                fout = new FileOutputStream("plugins/" + this.downloadURL);

                byte[] data = new byte['?'];
                int count;
                while ((count = in.read(data, 0, 1024)) != -1) {
                    fout.write(data, 0, count);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
            }
            if (this.out) {
                this.plugin.getLogger().log(Level.INFO, "Succesfully downloaded file: {0}", this.downloadURL);
                this.plugin.getLogger().log(Level.INFO, "To install the new features you have to restart your server!");
            }
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.WARNING, "Unable to download update!", e);
        }
    }

    public void externalUpdate() {
        try {
            URL download = new URL(this.downloadURL);

            BufferedInputStream in = null;
            FileOutputStream fout = null;
            try {
                if (this.out) {
                    this.plugin.getLogger().log(Level.INFO, "Trying to download {0} ..", this.downloadURL);
                }
                in = new BufferedInputStream(download.openStream());
                fout = new FileOutputStream("plugins/" + this.plugin.getName());

                byte[] data = new byte['?'];
                int count;
                while ((count = in.read(data, 0, 1024)) != -1) {
                    fout.write(data, 0, count);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
            }
            if (this.out) {
                this.plugin.getLogger().log(Level.INFO, "Succesfully downloaded file {0} !", this.downloadURL);
                this.plugin.getLogger().log(Level.INFO, "To install the new features you have to restart your server!");
            }
        } catch (IOException localIOException) {
        }
    }

    private String getFolder(String s) {
        return s.substring(0, s.lastIndexOf("/") + 1);
    }
}
