package com.gmail.samueler53.fwhumanstratego.message

import org.bukkit.entity.Player

enum class Message(val message: String, private val showPrefix: Boolean = true) {

    ARENA_ALREADY_EXISTS("§cL'arena chiamata %s esiste gia'."),
    ARENA_BUILDER_CREATED("Selezionare le posizioni dell'arena"),
    ARENA_CREATED(
        """
        §e${Message.LINE}§aL'arena: §6%s§a e' stata creata con successo!
        
        §e${Message.LINE}
        """.trimIndent()
    ),
    ARENA_CREATION_LOBBY("§7Spawn della lobby settata!§6"),
    ARENA_CREATION_NOT_EDITING("§cNon stai creando un'arena!"),
    ARENA_CREATION_TEAMBLUE("§7Spawn del team blu settato!§6"),
    ARENA_CREATION_TEAMRED("§7Spawn del team rosso settato!§6"),
    ARENA_CREATION_TREASURE_BLUE("§7Tesoro del team blu settato!§6"),
    ARENA_CREATION_TREASURE_RED("§7Tesoro del team rosso settato!§6"),
    ARENA_LIST("§7Nome arena: %s"),
    ARENA_NOT_FOUND("§cL'arena non e' stata trovata."),
    ARENA_REMOVE("§aArena rimossa§6"),
    GAME_ARENA_BUSY("§cArena occupata"),
    GAME_ARENA_FREE("§cL'arena non e' utilizzata da nessuna partita"),
    THIS_ROLE_CANT_ATTACK("§cQuesto ruolo non può attaccare"),
    GAME_CHOOSE_ROLE("§cDevi scegliere il ruolo, usa il comando /hs role"),
    GAME_CHOOSE_TEAM("§cDevi scegliere il team, usa il comando /hs join team"),
    GAME_DESERTER("§5%s e' un disertore!"),
    GAME_DRAW("§aLa partita e' finita in pareggio"),
    GAME_EDITED_SUCCESS("§aPartita modificata con successo"),
    GAME_IS_FULL("§aLa partita e' piena"),
    GAME_VITAL_CHAR_DEAD("§aUn personaggio vitale è morto, il round e' terminato"),
    GAME_IS_STARTING("§aLa partita sta per iniziare"),
    GAME_JOINED_SUCCESS("§aSei entrato con successo nella partita!"),
    GAME_LEAVE("§aSei stato rimosso dalla partita!"),
    GAME_LEAVE_GAME_FIRST("§cPrima devi leftare il tuo game attuale"),
    GAME_LEAVE_WHEN_STARTED("§cNon ti permetto di abbandonare i tuoi compagni!"),
    GAME_NEW_ROUND("§aTra poco iniziera' un nuovo round"),
    GAME_NO_MORE_GAMES("§cNon e' possibile creare ulteriori game"),
    GAME_ODD_PLAYERS("§cC'e' un numero dispari di giocatori oppure meno di 2"),
    GAME_PREPARATION_PHASE("§cNon puoi colpire gli altri player in fase di preparazione"),
    GAME_RELOAD("§aConfigurazione ricaricata con successo!"),
    GAME_ROLE_FULL("§aIl ruolo non ha piu' posti disponibili!"),
    GAME_ROLE_NO_LONGER_AVAIL("§aIl ruolo non e' disponibile"),
    GAME_ROUND_WINNER("§2Il round e' terminato, ha vinto il team §l%s"),
    GAME_SAME_TEAM("§cNon puoi colpire i membri del tuo team"),
    GAME_STARTED("§cNon puoi modificare una partita gia' iniziata!"),
    GAME_STOLEN_WOOL_BLUE("§a%s ha rubato la lana del team blu!"),
    GAME_STOLEN_WOOL_RED("§a%s ha rubato la lana del team rosso!"),
    GAME_CANT_CHANGE_ROLE_NOW("§cNon puoi cambiare ruolo adesso!"),
    GAME_CANT_STEAL_OWN_WOOL("§cNon puoi rubare la tua stessa lana"),
    GAME_STOPPED("§aLa partita e' stata stoppata!"),
    GAME_ASSIGNED_TEAM_BLUE("§9Sei stato assegnato al team blu"),
    GAME_TEAMFULL("§aIl team e' pieno"),
    GAME_ASSIGNED_TEAM_RED("§4Sei stato assegnato al team rosso"),
    GAME_GAME_OVER_TEAM_WINS("§5La partita e' terminata, ha vinto il team §l%s"),
    GAME_CANT_TAKE_ARMOR_OFF("§cNon puoi toglierti l'armor"),
    GAME_TREASURE_SAVED("§aIl tesoro che era stato rubato da %s e' stato salvato"),
    GAME_TREASURE_STOLEN("§aIl tesoro e' stato rubato"),
    GAME_UNMODIFIABLE("§cNon puoi modificare la partita! Ci sono attualmente piu' giocatori di quanti ne hai inseriti"),
    GAME_UNMODIFIABLE_2("§cNon puoi modificare la partita! La partita sta per iniziare!"),
    GAME_VALUE("§cDevi inserire un valore numerico"),
    PREFIX("§8[§cFw§fHumanStratego§8]");

    fun send(player: Player, vararg objects: Any) {
        val formatted = message.format(*objects)
        player.sendMessage(if (showPrefix) "${PREFIX.message} $formatted" else formatted)
    }

    companion object {
        private val LINE = "-".repeat(53)
    }
}