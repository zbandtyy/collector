package com.iot.video.app.kafka.partition;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.List;
import java.util.Map;

/**
 * @author ：tyy
 * @date ：Created in 2020/7/15 18:14
 * @description：
 * @modified By：
 * @version: $
 */
public class KeyPartitioner  implements Partitioner {


    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        String k = (String) key;
        String str [] = k.split("-");
        if(str.length < 2){
            return 0;
        }else {
            List partitions = cluster.partitionsForTopic(topic);
            int numPartitions = partitions.size();
            Integer par = Integer.valueOf(str[1]);
            return  par%numPartitions;
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {


    }
}
