package example;

import arc.Core;
import org.json.*;

import java.util.Optional;

import org.javacord.api.*;
import org.javacord.api.entity.channel.*;

public class Bot {
    public static void run(String _token) {
        // Log the bot in
        DiscordApi api = new DiscordApiBuilder().setToken(_token).login().join();

        Optional<TextChannel> c = api.getTextChannelById("729230699416125440");

        if(c.isPresent()) c.get().sendMessage("teste");
    }
}
