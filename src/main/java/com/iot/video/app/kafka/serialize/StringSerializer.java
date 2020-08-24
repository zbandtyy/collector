package com.iot.video.app.kafka.serialize;

import com.iot.video.app.kafka.datatype.VideoEventData;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * @author ：tyy
 * @date ：Created in 2020/7/29 17:45
 * @description：
 * @modified By：
 * @version: $
 */
public class StringSerializer implements Serializer<String> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, String data) {
        return ProtostuffUtil.serializer(data, String.class);
    }

    @Override
    public void close() {

    }
}
