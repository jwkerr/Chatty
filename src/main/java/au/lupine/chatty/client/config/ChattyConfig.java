package au.lupine.chatty.client.config;

import au.lupine.chatty.client.ChattyClient;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public class ChattyConfig {

    public static final ConfigClassHandler<ChattyConfig> HANDLER = ConfigClassHandler.createBuilder(ChattyConfig.class)
            .id(Identifier.of(ChattyClient.MOD_ID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("chatty.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();

    private static final String SETTINGS = "settings";
    private static final String NOTIFICATIONS = "notifications";
    private static final String DISCORD = "discord";

    public static ChattyConfig getInstance() {
        return HANDLER.instance();
    }

    public Screen getScreen(Screen parent) {
        return HANDLER.generateGui().generateScreen(parent);
    }

    @SerialEntry
    @AutoGen(category = SETTINGS)
    @Boolean(colored = true)
    public boolean isEnabled = true;

    @SerialEntry
    @AutoGen(category = SETTINGS, group = NOTIFICATIONS)
    @Boolean(colored = true)
    public boolean hideKillMessages = false;

    @SerialEntry
    @AutoGen(category = SETTINGS, group = NOTIFICATIONS)
    @Boolean(colored = true)
    public boolean hideVoteMessages = false;

    @SerialEntry
    @AutoGen(category = SETTINGS, group = NOTIFICATIONS)
    @Boolean(colored = true)
    public boolean hideObamaMessages = false;

    @SerialEntry
    @AutoGen(category = SETTINGS, group = DISCORD)
    @Boolean(colored = true)
    public boolean hideDiscordMessages = false;
    
    @SerialEntry
    @AutoGen(category = SETTINGS, group = DISCORD)
    @StringField
    public String filteredRanks = "*";

    @SerialEntry
    @AutoGen(category = SETTINGS, group = DISCORD)
    @Boolean(colored = true)
    public boolean invertDiscordFilter = false;
}
