package com.gmail.samueler53.fwhumanstratego.data

import com.charleskorn.kaml.Yaml
import com.gmail.samueler53.fwhumanstratego.FwHumanStratego
import com.gmail.samueler53.fwhumanstratego.objects.Arena
import com.gmail.samueler53.fwhumanstratego.utils.launchAsync
import kotlinx.serialization.builtins.ListSerializer
import java.io.File
import java.io.IOException

class Data(arenas: List<Arena>) {

    var arenas = arenas.toMutableList()
        set(value) {
            field = value
            saveData()
        }

    private fun saveData() {
        val saveData = Yaml.default.encodeToString(ListSerializer(Arena.serializer()), arenas)
        launchAsync {
            try {
                File(FwHumanStratego.dataSavePath).writeText(saveData)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {

        fun load(): Data {
            val serialized = File(FwHumanStratego.dataSavePath).readText()
            val deserialized = Yaml.default.decodeFromString(ListSerializer(Arena.serializer()), serialized)
            return Data(deserialized)
        }
    }
}