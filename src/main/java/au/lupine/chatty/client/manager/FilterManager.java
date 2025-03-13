package au.lupine.chatty.client.manager;

import au.lupine.chatty.client.config.ChattyConfig;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterManager {

    private static FilterManager instance;

    private static String lastVoteMessage = "";

    private static final String COMMAND_FEEDBACK_VOTE_MESSAGE = "Click on both links to vote and win gold crates!";
    private static final String MICHELLE_OBAMA_VOTE_MESSAGE = "[Michelle Obama -> me]";

    private FilterManager() {}

    public static FilterManager getInstance() {
        if (instance == null) instance = new FilterManager();
        return instance;
    }

    public void initialise() {
        ClientReceiveMessageEvents.ALLOW_GAME.register((text, overlay) -> doesTextPassFilter(text));
    }

    private boolean doesTextPassFilter(Text text) {
        ChattyConfig config = ChattyConfig.getInstance();
        if (!config.isEnabled) return true;

        Component component;
        try {
            component = MinecraftClientAudiences.of().asAdventure(text);
        } catch (Exception e) {
            return true; // Something went wrong, but we can just ignore it (enchantment fix)
        }

        String message = PlainTextComponentSerializer.plainText().serialize(component);

        if (config.hideEmptyMessages && message.isEmpty()) return false;

        if (config.hideKillMessages && isMessageKillMessage(message)) return false;
        if (config.hideVoteMessages && isMessageVoteMessage(message)) return false;
        if (config.hideObamaMessages && isMessageFromObama(message)) return false;
        if (config.hideStoreMessages && isMessageStoreMessage(message)) return false;

        if (!config.hideDiscordMessages) return true;
        if (!isMessageFromDiscord(message)) return true;

        String filter = config.filteredRanks;

        boolean inverted = config.invertDiscordFilter;
        if (filter.equals("*")) return inverted;

        String playerRank = getPlayerRank(message);
        List<String> filteredRanks = getFilteredRanksAsList(filter);

        if (inverted) {
            return filteredRanks.contains(playerRank);
        } else {
            return !filteredRanks.contains(playerRank);
        }
    }

    private String getPlayerRank(String message) {
        String[] split = message.split(" ");

        List<String> rankParts = new ArrayList<>();

        for (String string : split) {
            if (string.equals("[Discord]")) break;
            rankParts.add(string);
        }

        return String.join(" ", rankParts);
    }

    private boolean isMessageKillMessage(String message) {
        return message.startsWith("☠");
    }

    private boolean isMessageVoteMessage(String message) {
        String[] split = message.split(" ");
        if (split.length <= 1) return false;

        String[] firstArgRemoved = Arrays.copyOfRange(split, 1, split.length);
        String nameRemoved = String.join(" ", firstArgRemoved);

        return nameRemoved.equals("has voted and received a gold crate! /vote");
    }

    private boolean isMessageFromObama(String message) {
        if (message.startsWith(MICHELLE_OBAMA_VOTE_MESSAGE)) {
            lastVoteMessage = MICHELLE_OBAMA_VOTE_MESSAGE;
            return true;
        } else if (message.startsWith(COMMAND_FEEDBACK_VOTE_MESSAGE)) {
            lastVoteMessage = COMMAND_FEEDBACK_VOTE_MESSAGE;
            return false;
        }

        if (!lastVoteMessage.equals(MICHELLE_OBAMA_VOTE_MESSAGE)) return false; // These messages are coming as a result of player command feedback from /vote

        return message.startsWith(">https://minecraftservers.org/vote/383495?username=")
                || message.startsWith(">https://minecraft-mp.com/server/332214/vote/?username=");
    }

    private boolean isMessageStoreMessage(String message) {
        return message.startsWith("[store.earthmc.net -> me] Click here to upgrade to Premium.");
    }

    private boolean isMessageFromDiscord(String message) {
        String[] split = message.split(" ");
        if (split.length < 2) return false;

        return split[1].equals("[Discord]");
    }

    private List<String> getFilteredRanksAsList(String filter) {
        List<String> filteredRanks = new ArrayList<>();

        for (String rank : filter.split(",")) {
            filteredRanks.add(rank.stripLeading().stripTrailing());
        }

        return filteredRanks;
    }
}
