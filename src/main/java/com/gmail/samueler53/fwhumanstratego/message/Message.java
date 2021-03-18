package com.gmail.samueler53.fwhumanstratego.message;

import com.gmail.samueler53.fwhumanstratego.objects.Game;
import com.gmail.samueler53.fwhumanstratego.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.UUID;


public enum Message {

    PREFIX(ChatColor.DARK_GRAY + "[" +
            ChatColor.RED + "Fw" +
            ChatColor.WHITE + "HumanStratego" +
            ChatColor.DARK_GRAY + "]", false),

    /* Arena */
    ARENA_ALREADY_EXISTS(MessageUtils.formatErrorMessage("L'arena chiamata {} esiste gia'."), true),
    ARENA_NOT_FOUND(MessageUtils.formatErrorMessage("L'arena non e' stata trovata."), true),
    ARENA_CREATED(ChatColor.YELLOW + String.join("", Collections.nCopies(53, "-")) +
            ChatColor.GREEN + "L'arena: " +
            ChatColor.GOLD + "{}" +
            ChatColor.GREEN + " e' stata creata con successo!" + "\n \n" +
            ChatColor.YELLOW + String.join("", Collections.nCopies(53, "-")), false),
    ARENA_CREATION_TEAMRED(ChatColor.GRAY + "Spawn del team rosso settato!" + ChatColor.GOLD, true),
    ARENA_CREATION_TEAMBLUE(ChatColor.GRAY + "Spawn del team blu settato!" + ChatColor.GOLD, true),
    ARENA_CREATION_TREASURERED(ChatColor.GRAY + "Tesoro del team rosso settato!" + ChatColor.GOLD, true),
    ARENA_CREATION_TREASUREBLUE(ChatColor.GRAY + "Tesoro del team blu settato!" + ChatColor.GOLD, true),
    ARENA_CREATION_LOBBY(ChatColor.GRAY + "Spawn della lobby settata!" + ChatColor.GOLD, true),
    ARENA_REMOVE(ChatColor.GRAY + MessageUtils.formatSuccessMessage("Arena rimossa") + ChatColor.GOLD, true),
    ARENA_LIST(ChatColor.GRAY + "Nome arena: " + "{}", true),

    GAME_VALUE(MessageUtils.formatErrorMessage("Devi inserire un valore numerico"), true),
    GAME_ARENABUSY(MessageUtils.formatErrorMessage("Arena occupata"), true),
    GAME_ARENAFREE(MessageUtils.formatErrorMessage("L'arena non e' utilizzata da nessuna partita"), true),
    GAME_CHOOSEROLE(MessageUtils.formatErrorMessage("Devi scegliere il ruolo, usa il comando /hs role"), true),
    GAME_CHOOSETEAM(MessageUtils.formatErrorMessage("Devi scegliere il team, usa il comando /hs join team"), true),
    GAME_NEWROUND(ChatColor.GREEN + "Tra poco iniziera' un nuovo round", true),
    GAME_RELOAD(ChatColor.GREEN + "Configurazione ricaricata con successo!", true),
    GAME_DESERTER(ChatColor.DARK_PURPLE + "{}" + " e' un disertore!", true),
    GAME_TREASURESAVED(ChatColor.GREEN + "Il tesoro che era stato rubato da " + "{}" + " e' stato salvato", true),
    GAME_ODDPLAYERS(MessageUtils.formatErrorMessage("C'e' un numero dispari di giocatori oppure meno di 2"), true),
    GAME_SETPOINTS(MessageUtils.formatErrorMessage("Devi inserire tutti i punti"), true),
    GAME_ROLEFULL(ChatColor.GREEN + "Il ruolo non e' disponibile", true),
    GAME_ROLEBUSY(ChatColor.GREEN + "Il ruolo non ha piu' posti disponibili!", true),
    GAME_TEAMWINNER(ChatColor.DARK_PURPLE + "La partita e' terminata, ha vinto il team " + ChatColor.BOLD + "{}", true),
    GAME_ROUNDWINNER(ChatColor.DARK_GREEN + "Il round e' terminato, ha vinto il team " + ChatColor.BOLD + "{}", true),
    GAME_DRAW(ChatColor.GREEN + "La partita e' finita in pareggio", true),
    // GAME_KILLEDBY(ChatColor.GREEN + "Sei stato ucciso da " + "{}", true),
    // GAME_KILLED(ChatColor.GREEN + "Hai ucciso " + "{}", true),
    GAME_STEALEDWOOLRED(ChatColor.GREEN + "{}" + " ha rubato la lana del team rosso!", true),
    GAME_STEALEDWOOLBLUE(ChatColor.GREEN + "{}" + " ha rubato la lana del team blu!", true),
    GAME_TREASURESTEALED(ChatColor.GREEN + "Il tesoro e' stato rubato", true),
    GAME_TEAMFULL(ChatColor.GREEN + "Il team e' pieno", true),
    GAME_GAMEFULL(ChatColor.GREEN + "La partita e' piena", true),
    GAME_STEALHISWOOL(MessageUtils.formatErrorMessage("Non puoi rubare la tua stessa lana"), true),
    GAME_TOGGLEARMOR(MessageUtils.formatErrorMessage("Non puoi toglierti l'armor"), true),
    GAME_SAMETEAM(MessageUtils.formatErrorMessage("Non puoi colpire i membri del tuo team"), true),
    GAME_BOMB(MessageUtils.formatErrorMessage("La bomba non attacca"), true),
    GAME_PREPARATIONFASE(MessageUtils.formatErrorMessage("Non puoi colpire gli altri player in fase di preparazione"), true),
    GAME_TEAMRED(ChatColor.DARK_RED + "Sei stato assegnato al team rosso", true),
    GAME_TEAMBLUE(ChatColor.BLUE + "Sei stato assegnato al team blu", true),
    GAME_GENERALDEAD(ChatColor.GREEN + "Il generale e' morto, il round e' terminato", true),
    GAME_LEAVE(ChatColor.GREEN + "Sei stato rimosso dalla partita!", true),
    GAME_LEAVEGAMEFIRST(MessageUtils.formatErrorMessage("Prima devi leftare il tuo game attuale"), true),
    GAME_LEAVEWHENSTARTED(MessageUtils.formatErrorMessage("Non ti permetto di abbandonare i tuoi compagni!"), true),
    GAME_EDITABLE(ChatColor.GREEN + "Partita modificata con successo", true),
    GAME_UNMODIFIABLE(MessageUtils.formatErrorMessage("Non puoi modificare la partita! Ci sono attualmente piu' giocatori di quanti ne hai inseriti"), true),
    GAME_UNMODIFIABLE2(MessageUtils.formatErrorMessage("Non puoi modificare la partita! La partita sta per iniziare!"), true),
    GAME_STARTED(MessageUtils.formatErrorMessage("Non puoi modificare una partita gia' iniziata!"), true),
    GAME_STOPPED(ChatColor.GREEN + "La partita e' stata stoppata!", true),
    GAME_NOMOREGAMES(MessageUtils.formatErrorMessage("Non e' possibile creare ulteriori game"), true),
    GAME_ISSTARTING(ChatColor.GREEN + "La partita sta per iniziare", true),
    GAME_JOIN(ChatColor.GREEN + "Sei entrato con successo nella partita!", true);


    private final String message;
    private final boolean showPrefix;

    Message(String message, boolean showPrefix) {
        this.message = MessageUtils.rewritePlaceholders(message);
        this.showPrefix = showPrefix;
    }

    public void send(Player player, Object... objects) {
        player.sendMessage(asString(objects));
    }

    public void broadcast(Game game, Object... objects) {
        for (UUID uuid : game.getPlayersPlaying()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return;
            }
            send(player, objects);
        }
    }

//    public void broadcastAll(Object... objects) {
//        Bukkit.broadcastMessage(asString(objects));
//    }

    public String asString(Object... objects) {
        return format(objects);
    }

    private String format(Object... objects) {
        String string = this.message;
        if (this.showPrefix) {
            string = PREFIX.message + " " + this.message;
        }
        for (int i = 0; i < objects.length; i++) {
            Object o = objects[i];
            string = string.replace("{" + i + "}", String.valueOf(o));
        }
        return string;
    }

}
