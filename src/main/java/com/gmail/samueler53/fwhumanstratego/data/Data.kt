package com.gmail.samueler53.fwhumanstratego.data

import com.gmail.samueler53.fwhumanstratego.objects.Arena
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.Serializable
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class Data(@field:Transient private var fileName: String) : Serializable {

    var arene = mutableListOf<Arena>()
        set(value) {
            field = value
            saveData()
        }

    fun saveData() {
        try {
            BukkitObjectOutputStream(GZIPOutputStream(FileOutputStream(fileName))).use {
                it.writeObject(this)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {

        @Transient
        private val serialVersionUID = 1681012206529286330L

        fun loadData(filePath: String) = try {
            BukkitObjectInputStream(
                GZIPInputStream(FileInputStream(filePath))
            ).use {
                it.readObject() as Data
            }.apply {
                fileName = filePath
                saveData()
            }
        } catch (e: ClassNotFoundException) {
            Data(filePath)
        } catch (e: IOException) {
            Data(filePath)
        }
    }
}