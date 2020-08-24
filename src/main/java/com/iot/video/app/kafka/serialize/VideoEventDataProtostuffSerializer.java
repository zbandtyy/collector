package com.iot.video.app.kafka.serialize;

import com.esotericsoftware.kryo.Kryo;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Created by shirukai on 2018/8/25
 */
public class VideoEventDataProtostuffSerializer implements Serializer<Person> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, Person data) {

        return ProtostuffUtil.serializer(data, Person.class);
    }

    @Override
    public void close() {

    }
}