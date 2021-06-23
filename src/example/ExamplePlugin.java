package example;

// Arc imports
import arc.*;
import arc.util.*;

// Mindustry imports
import static mindustry.Vars.state;
import static mindustry.Vars.netServer;
import mindustry.*;
import mindustry.game.EventType;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.Plugin;

// Org.json imports
import org.json.*;

public class ExamplePlugin extends Plugin{
    private String OwnerPrefix = "[sky][Dono][] %1[orange] > [white]%2";
    private String AdminPrefix = "[blue][Admin][] %1[orange] > [white]%2";
    private String UserPrefix = "%1[orange] > [white]%2";

    private JSONObject config;

    public ExamplePlugin() {
        // Send message with special tag
        Events.on(EventType.PlayerChatEvent.class, e -> {
            if (e.player.getInfo().id.equals("UUt6yUMf3wcAAAAA8gH2Ug==")) {
                Call.sendMessage(OwnerPrefix.replace("%1", e.player.name).replace("%2",e.message));
            } else if (e.player.admin) {
                Call.sendMessage(AdminPrefix.replace("%1", e.player.name).replace("%2",e.message));
            } else {
                Call.sendMessage(UserPrefix.replace("%1", e.player.name).replace("%2",e.message));
            }
        });
        
        // Unpause the game if one or more player is connected
        Events.on(PlayerJoin.class, e -> {
        	if (Groups.player.size() >= 1 && state.serverPaused) {
        		state.serverPaused = false;
        		Log.info("auto-pause: " + Groups.player.size() + " jogador conectado -> Jogo despausado...");
        		Call.sendMessage("[scarlet][Server][]: Jogo despausado...");
        	}
        });
        
        // Pause the game if no one is connected
        Events.on(PlayerLeave.class, e -> {
        	if (Groups.player.size()-1 < 1) {
        		state.serverPaused = true;
        		Log.info("auto-pause: nenhum jogador conectado -> Jogo pausado...");
        	}
        });
    }

    // Called when game initializes
    @Override
    public void init() {
        // Create config file
        if (!Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").exists()) {
            JSONObject defaultConfig = new JSONObject();
            defaultConfig.put("owner-id", "");

            JSONObject defaultDiscordConfig = new JSONObject();
            String[] discordStrings = {
                "token",
                "channel_id",
                "log_channel_id",
                "serverstatus_channel_id",
                "admin_role_id",
                "mod_role_id"
            };

            for (String ds : discordStrings) {
                defaultDiscordConfig.put(ds, "");
            }

            Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").writeString(defaultConfig.toString());
        }

        // Chat filter; Block all messages
        netServer.admins.addChatFilter((player, text) -> null);
    }

    //register commands that run on the server
    @Override
    public void registerServerCommands(CommandHandler handler){
        /*
        handler.register("reactors", "List all thorium reactors in the map.", args -> {
            for(int x = 0; x < Vars.world.width(); x++){
                for(int y = 0; y < Vars.world.height(); y++){
                    //loop through and log all found reactors
                    if(Vars.world.tile(x, y).block() == Blocks.thoriumReactor){
                        Log.info("Reactor at @, @", x, y);
                    }
                }
            }
        });
        */
    }

    //register commands that player can invoke in-game
    @Override
    public void registerClientCommands(CommandHandler handler){
        //register a whisper command which can be used to send other players messages
        handler.<Player>register("dm", "<player> <texto...>", "Mande uma mensagem privada para um jogador.", (args, player) -> {
            //find player by name
            Player other = Groups.player.find(p -> p.name.equalsIgnoreCase(args[0]));

            //give error message with scarlet-colored text if player isn't found
            if(other == null){
                player.sendMessage("[scarlet]Nenhum jogador encontrado com esse nome!");
                return;
            }

            //send the other player a message, using [lightgray] for gray text color and [] to reset color
            other.sendMessage("[lightgray](DM) " + player.name + ":[] " + args[1]);
        });
    }
}
