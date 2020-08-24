package com.iot.video.app.kafka.serialize;

import com.iot.video.app.kafka.datatype.VideoEventData;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * @author ：tyy
 * @date ：Created in 2020/7/29 16:32
 * @description：
 * @modified By：
 * @version: $
 */
public class VideoEventDataProtostuffDeserializer implements Deserializer<VideoEventData> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public VideoEventData deserialize(String topic, byte[] data) {
        return  ProtostuffUtil.deserializer(data, VideoEventData.class);
    }

    @Override
    public void close() {

    }
}
