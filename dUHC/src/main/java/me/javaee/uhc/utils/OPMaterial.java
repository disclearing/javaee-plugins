package me.javaee.uhc.utils;

import java.util.StringTokenizer;
import org.bukkit.Material;

public class OPMaterial {
    private boolean _blockValid = true;
    private boolean _dataValid = false;
    private int _id;
    private int _data;

    public OPMaterial(String string) {
        try {
            Material material = Material.valueOf((String)string.toUpperCase());
            this._id = material.getId();
            return;
        }
        catch (Exception exception) {
            try {
                this._id = Integer.parseInt(string);
                return;
            }
            catch (Exception exception2) {
                try {
                    StringTokenizer stringTokenizer = new StringTokenizer(string);
                    Material material = Material.valueOf((String)stringTokenizer.nextToken(":").toUpperCase());
                    this._id = material.getId();
                    this._data = Integer.parseInt(stringTokenizer.nextToken(":"));
                    this._dataValid = true;
                    return;
                }
                catch (Exception exception3) {
                    try {
                        StringTokenizer stringTokenizer = new StringTokenizer(string);
                        this._id = Integer.parseInt(stringTokenizer.nextToken(":"));
                        this._data = Integer.parseInt(stringTokenizer.nextToken(":"));
                        this._dataValid = true;
                        return;
                    }
                    catch (Exception exception4) {
                        this._blockValid = false;
                        return;
                    }
                }
            }
        }
    }

    public OPMaterial(int n) {
        this._id = n;
    }

    public OPMaterial(int n, int n2) {
        this._id = n;
        this._data = n2;
        this._dataValid = true;
    }

    public boolean isBlockValid() {
        return this._blockValid;
    }

    public boolean isDataValid() {
        return this._dataValid;
    }

    public int getBlockId() {
        return this._id;
    }

    public int getBlockData() {
        return this._data;
    }
}

