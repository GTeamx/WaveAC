package com.xiii.wave.checks;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Packets {
    PacketType.Play.Server[] playServer();
    PacketType.Play.Client[] playClient();
}