package me.imlukas.devnicschatplugin.channels;

import java.util.UUID;

public enum DefaultChannels {

    GLOBAL(UUID.fromString("a431278f-4228-4915-aa5a-a8afabef4815")),
    LOCAL(UUID.fromString("a431278f-4228-4915-aa5a-a8afabef4816")),
    WHISPER(UUID.fromString("a431278f-4228-4915-aa5a-a8afabef4817")),
    SHOUT(UUID.fromString("a431278f-4228-4915-aa5a-a8afabef4818"));

    public final UUID channelUUID;

    private DefaultChannels(UUID channelUUID){
        this.channelUUID = channelUUID;
    }
}
