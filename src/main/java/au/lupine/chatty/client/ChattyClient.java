package au.lupine.chatty.client;

import au.lupine.chatty.client.config.ChattyConfig;
import au.lupine.chatty.client.manager.FilterManager;
import net.fabricmc.api.ClientModInitializer;

public class ChattyClient implements ClientModInitializer {

    public static final String MOD_ID = "chatty";

    @Override
    public void onInitializeClient() {
        ChattyConfig.HANDLER.load();
        FilterManager.getInstance().initialise();
    }
}
