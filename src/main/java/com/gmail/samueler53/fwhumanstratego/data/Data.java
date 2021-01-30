package com.gmail.samueler53.fwhumanstratego.data;

import com.gmail.samueler53.fwhumanstratego.objects.Arena;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Data implements Serializable {
    private static transient final long serialVersionUID = 1681012206529286330L;
    private transient String fileName;
    private List<Arena> arene = new ArrayList<>();

    public Data(String fileName) {
        this.fileName = fileName;
    }

    public boolean saveData() {
        try {
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(fileName)));
            out.writeObject(this);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Data loadData(String filePath) {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(filePath)));
            Data data = (Data) in.readObject();
            in.close();
            data.setFileName(filePath);
            data.saveData();
            return data;
        } catch (ClassNotFoundException | IOException e) {
            return new Data(filePath);
        }
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<Arena> getArene() {
        return arene;
    }

    public void setArene(List<Arena> arene) {
        this.arene = arene;
        saveData();
    }
}
