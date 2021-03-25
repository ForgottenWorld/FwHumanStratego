package com.gmail.samueler53.fwhumanstratego.message

import com.gmail.samueler53.fwhumanstratego.objects.Game
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

enum class Message(val message: String, private val showPrefix: Boolean = true) {

    ARENA_ALREADY_EXISTS("${ChatColor.RED}L'arena chiamata %s esiste gia'."),
    ARENA_CREATED(
        """
        ${"§e-".repeat(53)}§aL'arena: ${ChatColor.GOLD}%s${ChatColor.GREEN} e' stata creata con successo!
        
        ${"§e-".repeat(53)}
        """.trimIndent()
    ),
    ARENA_CREATION_LOBBY("${ChatColor.GRAY}Spawn della lobby settata!${ChatColor.GOLD}"),
    ARENA_CREATION_TEAMBLUE("${ChatColor.GRAY}Spawn del team blu settato!${ChatColor.GOLD}"),
    ARENA_CREATION_TEAMRED("${ChatColor.GRAY}Spawn del team rosso settato!${ChatColor.GOLD}"),
    ARENA_CREATION_TREASUREBLUE("${ChatColor.GRAY}Tesoro del team blu settato!${ChatColor.GOLD}"),
    ARENA_CREATION_TREASURERED("${ChatColor.GRAY}Tesoro del team rosso settato!${ChatColor.GOLD}"),
    ARENA_LIST("${ChatColor.GRAY}Nome arena: %s"),
    ARENA_NOT_FOUND("${ChatColor.RED}L'arena non e' stata trovata."),
    ARENA_REMOVE("${ChatColor.GREEN}Arena rimossa${ChatColor.GOLD}"),
    GAME_ARENABUSY("${ChatColor.RED}Arena occupata"),
    GAME_ARENAFREE("${ChatColor.RED}L'arena non e' utilizzata da nessuna partita"),
    GAME_BOMB("${ChatColor.RED}La bomba non attacca"),
    GAME_CHOOSEROLE("${ChatColor.RED}Devi scegliere il ruolo, usa il comando /hs role"),
    GAME_CHOOSETEAM("${ChatColor.RED}Devi scegliere il team, usa il comando /hs join team"),
    GAME_DESERTER("${ChatColor.DARK_PURPLE}%s e' un disertore!"),
    GAME_DRAW("${ChatColor.GREEN}La partita e' finita in pareggio"),
    GAME_EDITABLE("${ChatColor.GREEN}Partita modificata con successo"),
    GAME_GAMEFULL("${ChatColor.GREEN}La partita e' piena"),
    GAME_GENERALDEAD("${ChatColor.GREEN}Il generale e' morto, il round e' terminato"),
    GAME_ISSTARTING("${ChatColor.GREEN}La partita sta per iniziare"),
    GAME_JOIN("${ChatColor.GREEN}Sei entrato con successo nella partita!"),
    GAME_LEAVE("${ChatColor.GREEN}Sei stato rimosso dalla partita!"),
    GAME_LEAVEGAMEFIRST("${ChatColor.RED}Prima devi leftare il tuo game attuale"),
    GAME_LEAVEWHENSTARTED("${ChatColor.RED}Non ti permetto di abbandonare i tuoi compagni!"),
    GAME_NEWROUND("${ChatColor.GREEN}Tra poco iniziera' un nuovo round"),
    GAME_NOMOREGAMES("${ChatColor.RED}Non e' possibile creare ulteriori game"),
    GAME_ODDPLAYERS("${ChatColor.RED}C'e' un numero dispari di giocatori oppure meno di 2"),
    GAME_PREPARATIONFASE("${ChatColor.RED}Non puoi colpire gli altri player in fase di preparazione"),
    GAME_RELOAD("${ChatColor.GREEN}Configurazione ricaricata con successo!"),
    GAME_ROLEBUSY("${ChatColor.GREEN}Il ruolo non ha piu' posti disponibili!"),
    GAME_ROLEFULL("${ChatColor.GREEN}Il ruolo non e' disponibile"),
    GAME_ROUNDWINNER("${ChatColor.DARK_GREEN}Il round e' terminato, ha vinto il team ${ChatColor.BOLD}%s"),
    GAME_SAMETEAM("${ChatColor.RED}Non puoi colpire i membri del tuo team"),
    GAME_SETPOINTS("${ChatColor.RED}Devi inserire tutti i punti"),
    GAME_STARTED("${ChatColor.RED}Non puoi modificare una partita gia' iniziata!"),
    GAME_STEALEDWOOLBLUE("${ChatColor.GREEN}%s ha rubato la lana del team blu!"),
    GAME_STEALEDWOOLRED("${ChatColor.GREEN}%s ha rubato la lana del team rosso!"),
    GAME_STEALHISWOOL("${ChatColor.RED}Non puoi rubare la tua stessa lana"),
    GAME_STOPPED("${ChatColor.GREEN}La partita e' stata stoppata!"),
    GAME_TEAMBLUE("${ChatColor.BLUE}Sei stato assegnato al team blu"),
    GAME_TEAMFULL("${ChatColor.GREEN}Il team e' pieno"),
    GAME_TEAMRED("${ChatColor.DARK_RED}Sei stato assegnato al team rosso"),
    GAME_TEAMWINNER("${ChatColor.DARK_PURPLE}La partita e' terminata, ha vinto il team ${ChatColor.BOLD}%s"),
    GAME_TOGGLEARMOR("${ChatColor.RED}Non puoi toglierti l'armor"),
    GAME_TREASURESAVED("${ChatColor.GREEN}Il tesoro che era stato rubato da %s e' stato salvato"),
    GAME_TREASURESTEALED("${ChatColor.GREEN}Il tesoro e' stato rubato"),
    GAME_UNMODIFIABLE("${ChatColor.RED}Non puoi modificare la partita! Ci sono attualmente piu' giocatori di quanti ne hai inseriti"),
    GAME_UNMODIFIABLE2("${ChatColor.RED}Non puoi modificare la partita! La partita sta per iniziare!"),
    GAME_VALUE("${ChatColor.RED}Devi inserire un valore numerico"),
    PREFIX("${ChatColor.DARK_GRAY}[${ChatColor.RED}Fw${ChatColor.WHITE}HumanStratego${ChatColor.DARK_GRAY}]");

    fun send(player: Player, vararg objects: Any) {
        val formatted = message.format(*objects)
        player.sendMessage(if (showPrefix) "${PREFIX.message} $formatted" else formatted)
    }

    fun broadcast(game: Game, vararg objects: Any) {
        game.playersPlaying.mapNotNull(Bukkit::getPlayer).forEach {
            send(it, *objects)
        }
    }
}