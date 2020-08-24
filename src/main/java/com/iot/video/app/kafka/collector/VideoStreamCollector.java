package com.iot.video.app.kafka.collector;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.log4j.Logger;

import com.iot.video.app.kafka.util.PropertyFileReader;

/**
 *  Class to configure Kafka Producer and connect to Video camera url.
 *  
 * @author abaghel
 *
 */
public class VideoStreamCollector {

	private static final Logger logger = Logger.getLogger(VideoStreamCollector.class);



	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		// set producer properties  设置kafka的配置文件
		Properties prop = PropertyFileReader.readPropertyFile();	
		Properties properties = new Properties();
		//必须指定的选项1
		properties.put("bootstrap.servers", prop.getProperty("kafka.bootstrap.servers"));
		properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		//com.iot.video.app.kafka.serialize.VideoEventDataKryoSerializer
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("partitioner.class", "com.iot.video.app.kafka.partition.KeyPartitioner");


		//acks指定必须要有多少个partition副本收到消息，生产者才会认为消息的写入是成功的。   acks=0，生产者不需要等待服务器的响应，以网络能支持的最大速度发送消息，吞吐量高，但是如果broker没有收到消息，生产者是不知道的
		//
		//      acks=1，leader partition收到消息，生产者就会收到一个来自服务器的成功响应
		//
		//      acks=all，所有的partition都收到消息，生产者才会收到一个服务器的成功响应

		properties.put("acks", prop.getProperty("kafka.acks"));
//		4）retries，生产者从服务器收到临时性错误时，生产者重发消息的次数
		properties.put("retries",prop.getProperty("kafka.retries"));
		//发送到同一个partition的消息会被先存储在batch中，该参数指定一个batch可以使用的内存大小，单位是byte。
		// 不一定需要等到batch被填满才能发送
		properties.put("batch.size", prop.getProperty("kafka.batch.size"));
//		linger.ms，生产者在发送消息前等待linger.ms，从而等待更多的消息加入到batch中。
//		如果batch被填满或者linger.ms达到上限，就把batch中的消息发送出去
		properties.put("linger.ms", prop.getProperty("kafka.linger.ms"));
		//有限制 必须手动设置  主题必须存在不然设置不生效
		properties.put("max.request.size", prop.getProperty("kafka.max.request.size"));
		//properties.put("max.request.size", prop.getProperty("kafka.max.request.size"));
		properties.put("compression.type", prop.getProperty("kafka.compression.type"));
		// generate event
		Producer<String, String> producer = new KafkaProducer<String, String>(properties);
		generateIoTEvent(producer,prop.getProperty("kafka.topic"),prop.getProperty("camera.id"),prop.getProperty("camera.url"));

	}

	//对每一个视频的资源创建新 的进程
	private static void generateIoTEvent(Producer<String, String> producer, String topic, String camId, String videoUrl) throws Exception {
		String[] urls = videoUrl.split(",");
		String[] ids = camId.split(",");
//		System.out.println(urls[0]  + urls[1] + urls.length);
//		System.out.println(ids[0] + ids[1] + ids.length);
		if(urls.length != ids.length){
			throw new Exception("There should be same number of camera Id and url");
		}
		logger.info("Total urls to process " + urls.length);
		Thread ts[] = new Thread[urls.length];
		for(int i=0;i<urls.length;i++){
			//创建线程 ，添加Runnable对象
				//trims截取中间的非空白字符
			ts[i] = new Thread(new VideoEventGenerator(ids[i].trim(),urls[i].trim(),producer,topic));
			ts[i].start();
		}
		for(int i = 0; i <urls.length; i++){
			ts[i].join();
		}

	}
}
