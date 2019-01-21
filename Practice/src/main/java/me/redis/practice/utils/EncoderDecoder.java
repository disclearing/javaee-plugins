package me.redis.practice.utils;

import net.minecraft.util.org.apache.commons.codec.binary.Base64;

public class EncoderDecoder {
    public static String decode(String encoded) {
        return encoded.replace("$|$", "");
    }
}
