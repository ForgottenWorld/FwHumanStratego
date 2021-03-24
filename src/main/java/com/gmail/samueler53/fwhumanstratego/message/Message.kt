package com.gmail.samueler53.fwhumanstratego.message

import com.gmail.samueler53.fwhumanstratego.objects.Game
import com.gmail.samueler53.fwhumanstratego.utils.MessageUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

enum class Message(message: String, private val showPrefix: Boolean = true) {

    ARENA_ALREADY_EXISTS(MessageUtils.formatErrorMessage("L'arena chiamata {} esiste gia'.")),
    ARENA_CREATED(
        """
        §e${"-".repeat(53)}§aL'arena: ${ChatColor.GOLD}{}${ChatColor.GREEN} e' stata creata con successo!
        
        ${ChatColor.YELLOW}${"-".repeat(53)}
        """.trimIndent()
    ),
    ARENA_CREATION_LOBBY("${ChatColor.GRAY}Spawn della lobby settata!${ChatColor.GOLD}"),
    ARENA_CREATION_TEAMBLUE("${ChatColor.GRAY}Spawn del team blu settato!${ChatColor.GOLD}"),
    ARENA_CREATION_TEAMRED("${ChatColor.GRAY}Spawn del team rosso settato!${ChatColor.GOLD}"),
    ARENA_CREATION_TREASUREBLUE("${ChatColor.GRAY}Tesoro del team blu settato!${ChatColor.GOLD}"),
    ARENA_CREATION_TREASURERED("${ChatColor.GRAY}Tesoro del team rosso settato!${ChatColor.GOLD}"),
    ARENA_LIST("${ChatColor.GRAY}Nome arena: {}"),
    ARENA_NOT_FOUND(MessageUtils.formatErrorMessage("L'arena non e' stata trovata.")),
    ARENA_REMOVE("${ChatColor.GRAY}${MessageUtils.formatSuccessMessage("Arena rimossa")}${ChatColor.GOLD}"),
    GAME_ARENABUSY(MessageUtils.formatErrorMessage("Arena occupata")),
    GAME_ARENAFREE(MessageUtils.formatErrorMessage("L'arena non e' utilizzata da nessuna partita")),
    GAME_BOMB(MessageUtils.formatErrorMessage("La bomba non attacca")),
    GAME_CHOOSEROLE(MessageUtils.formatErrorMessage("Devi scegliere il ruolo, usa il comando /hs role")),
    GAME_CHOOSETEAM(MessageUtils.formatErrorMessage("Devi scegliere il team, usa il comando /hs join team")),
    GAME_DESERTER("${ChatColor.DARK_PURPLE}{} e' un disertore!"),
    GAME_DRAW("${ChatColor.GREEN}La partita e' finita in pareggio"),
    GAME_EDITABLE("${ChatColor.GREEN}Partita modificata con successo"),
    GAME_GAMEFULL("${ChatColor.GREEN}La partita e' piena"),
    GAME_GENERALDEAD("${ChatColor.GREEN}Il generale e' morto, il round e' terminato"),
    GAME_ISSTARTING("${ChatColor.GREEN}La partita sta per iniziare"),
    GAME_JOIN("${ChatColor.GREEN}Sei entrato con successo nella partita!"),
    GAME_LEAVE("${ChatColor.GREEN}Sei stato rimosso dalla partita!"),
    GAME_LEAVEGAMEFIRST(MessageUtils.formatErrorMessage("Prima devi leftare il tuo game attuale")),
    GAME_LEAVEWHENSTARTED(MessageUtils.formatErrorMessage("Non ti permetto di abbandonare i tuoi compagni!")),
    GAME_NEWROUND("${ChatColor.GREEN}Tra poco iniziera' un nuovo round"),
    GAME_NOMOREGAMES(MessageUtils.formatErrorMessage("Non e' possibile creare ulteriori game")),
    GAME_ODDPLAYERS(MessageUtils.formatErrorMessage("C'e' un numero dispari di giocatori oppure meno di 2")),
    GAME_PREPARATIONFASE(MessageUtils.formatErrorMessage("Non puoi colpire gli altri player in fase di preparazione")),
    GAME_RELOAD("${ChatColor.GREEN}Configurazione ricaricata con successo!"),
    GAME_ROLEBUSY("${ChatColor.GREEN}Il ruolo non ha piu' posti disponibili!"),
    GAME_ROLEFULL("${ChatColor.GREEN}Il ruolo non e' disponibile"),
    GAME_ROUNDWINNER("${ChatColor.DARK_GREEN}Il round e' terminato, ha vinto il team ${ChatColor.BOLD}{}"),
    GAME_SAMETEAM(MessageUtils.formatErrorMessage("Non puoi colpire i membri del tuo team")),
    GAME_SETPOINTS(MessageUtils.formatErrorMessage("Devi inserire tutti i punti")),
    GAME_STARTED(MessageUtils.formatErrorMessage("Non puoi modificare una partita gia' iniziata!")),
    GAME_STEALEDWOOLBLUE("${ChatColor.GREEN}{} ha rubato la lana del team blu!"),
    GAME_STEALEDWOOLRED("${ChatColor.GREEN}{} ha rubato la lana del team rosso!"),
    GAME_STEALHISWOOL(MessageUtils.formatErrorMessage("Non puoi rubare la tua stessa lana")),
    GAME_STOPPED("${ChatColor.GREEN}La partita e' stata stoppata!"),
    GAME_TEAMBLUE("${ChatColor.BLUE}Sei stato assegnato al team blu"),
    GAME_TEAMFULL("${ChatColor.GREEN}Il team e' pieno"),
    GAME_TEAMRED("${ChatColor.DARK_RED}Sei stato assegnato al team rosso"),
    GAME_TEAMWINNER("${ChatColor.DARK_PURPLE}La partita e' terminata, ha vinto il team ${ChatColor.BOLD}{}"),
    GAME_TOGGLEARMOR(MessageUtils.formatErrorMessage("Non puoi toglierti l'armor")),
    GAME_TREASURESAVED("${ChatColor.GREEN}Il tesoro che era stato rubato da {} e' stato salvato"),
    GAME_TREASURESTEALED("${ChatColor.GREEN}Il tesoro e' stato rubato"),
    GAME_UNMODIFIABLE(MessageUtils.formatErrorMessage("Non puoi modificare la partita! Ci sono attualmente piu' giocatori di quanti ne hai inseriti")),
    GAME_UNMODIFIABLE2(MessageUtils.formatErrorMessage("Non puoi modificare la partita! La partita sta per iniziare!")),
    GAME_VALUE(MessageUtils.formatErrorMessage("Devi inserire un valore numerico")),
    PREFIX("${ChatColor.DARK_GRAY}[${ChatColor.RED}Fw${ChatColor.WHITE}HumanStratego${ChatColor.DARK_GRAY}]");

    val message: String = MessageUtils.rewritePlaceholders(message)

    fun send(player: Player, vararg objects: Any) {
        player.sendMessage(asString(*objects))
    }

    fun broadcast(game: Game, vararg objects: Any) {
        for (uuid in game.playersPlaying) {
            val player = Bukkit.getPlayer(uuid) ?: return
            send(player, *objects)
        }
    }

    private fun asString(vararg objects: Any) = format(*objects)

    private fun format(vararg objects: Any): String {
        var string = message
        if (showPrefix) {
            string = "$message $message"
        }
        for ((i, o) in objects.withIndex()) {
            string = string.replace("{$i}", o.toString())
        }
        return string
    }

}