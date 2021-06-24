package example;

// Arc imports
import arc.*;
import arc.util.*;

// Mindustry imports
import static mindustry.Vars.state;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.Plugin;

// Org imports
import org.json.*;
import org.javacord.api.*;

public class ExamplePlugin extends Plugin{
    // [orange] > [white]%2
    private String OwnerPrefix = "[sky][Dono][] %1";
    private String AdminPrefix = "[blue][Admin][] %1";
    private String UserPrefix = "%1";

    private JSONObject config;

    public ExamplePlugin() {
        Events.on(PlayerJoin.class, e -> {
            // Check for non-admin players with admin in name
            if (!e.player.admin) {
                if (e.player.name.toLowerCase().contains("admin") || e.player.name.toLowerCase().contains("adm")) {
                    e.player.name = "retardado";
                } else if (e.player.name.toLowerCase().contains("dono")) {
                    e.player.name = "retardado²";
                }
            }

            // Rename players to use the tag system
            if (e.player.getInfo().id.equals("UUt6yUMf3wcAAAAA8gH2Ug==")) {
                e.player.name = OwnerPrefix.replace("%1", e.player.name);
            } else if (e.player.admin) {
                e.player.name = AdminPrefix.replace("%1", e.player.name);
            } else {
                e.player.name = UserPrefix.replace("%1", e.player.name);
            }

            
            // Unpause the game if one or more player is connected
        	if (Groups.player.size() >= 1 && state.serverPaused) {
        		state.serverPaused = false;
        		Log.info("auto-pause: " + Groups.player.size() + " jogador conectado -> Jogo despausado...");
        		Call.sendMessage("[scarlet][Server][]: Jogo despausado...");
        	}
        });
        
        Events.on(PlayerLeave.class, e -> {   
            // Pause the game if no one is connected
        	if (Groups.player.size()-1 < 1) {
        		state.serverPaused = true;
        		Log.info("auto-pause: nenhum jogador conectado -> Jogo pausado...");
        	}
        });
    }

    // Called when game initializes
    @Override
    public void init() {
        // Create config file if it doesn't exist
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

            defaultConfig.put("discord", defaultDiscordConfig);

            Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").writeString(defaultConfig.toString());
        }

        // Load config
        this.config = new JSONObject(Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").readString());

        // Chat filter; Block all messages
        //netServer.admins.addChatFilter((player, text) -> null);
    }

    //register commands that run on the server
    @Override
    public void registerServerCommands(CommandHandler handler){
        handler.register("reloadconfig", "[MindustryBR] Reload plugin config", args -> {
            // Load config
            this.config = new JSONObject(Core.settings.getDataDirectory().child("mods/MindustryBR/config.json").readString());
        });

        handler.register("startbot", "[MindustryBR] Start bot", args -> {
            Bot.run(this.config.getJSONObject("discord").getString("token"));
        });
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
