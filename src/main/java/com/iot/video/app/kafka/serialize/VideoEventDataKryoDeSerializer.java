package com.iot.video.app.kafka.serialize;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.iot.video.app.kafka.datatype.VideoEventData;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @author ：tyy
 * @date ：Created in 2020/7/29 19:48
 * @description：
 * @modified By：
 * @version: $
 */
public class VideoEventDataKryoDeSerializer implements Deserializer<VideoEventData> {

    static private final ThreadLocal<Kryo> tLocal = new ThreadLocal<Kryo>() {
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(false);
            return kryo;
        };
    };
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public VideoEventData deserialize(String topic, byte[] data) {
        Kryo kryo=tLocal.get();
        //2.1构建流对象
        Input input=new Input(new ByteArrayInputStream(data));
        //2.2对象反序列化
        VideoEventData obj=  kryo.readObject(input,VideoEventData.class);
        //2.3释放资源
        input.close();
        return obj;
    }

    @Override
    public void close() {

    }

    public static void main(String[] args) {

        VideoEventData ed = new VideoEventData("hello",new Timestamp(System.currentTimeMillis()),10,10,3,"fff");
        System.out.println(ed);
        byte[] hellos = new VideoEventDataKryoSerializer().serialize("hello", ed);
        for (int i = 0; i < hellos.length; i++) {
            System.out.print(hellos[i] +" ");
        }
        System.out.println();


        VideoEventData hello = new VideoEventDataKryoDeSerializer().deserialize("hello", hellos);
        System.out.println(hello.getData());
        System.out.println(hello);
    }

}
