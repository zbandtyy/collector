package com.iot.video.app.kafka.collector;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.iot.video.app.kafka.config.Config;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import static org.opencv.imgcodecs.Imgcodecs.imencode;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.videoio.Videoio.CAP_PROP_POS_MSEC;

/**
 * Class to convert Video Frame into byte array and generate JSON event using Kafka Producer.
 * 
 * @author abaghel
 *
 */
public class VideoEventGenerator implements Runnable {
	private static final Logger logger = Logger.getLogger(VideoEventGenerator.class);
	private String cameraId;
	private String url;
	private Producer<String, String> producer;
	private String topic;
	
	public VideoEventGenerator(String cameraId, String url, Producer<String, String> producer, String topic) {
		this.cameraId = cameraId;
		this.url = url;
		this.producer = producer;
		this.topic = topic;
	}
	
	//load OpenCV native lib
	static {
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.load(Config.OPENCV_LIB_PATH);
		logger.info("load opencv success");
	}

	@Override
	public void run() {
		logger.info("Processing cameraId "+cameraId+" with url "+url);
		try {
			generateEvent(cameraId,url,producer,topic);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	//generate JSON events for frame
	private void generateEvent(String cameraId, String url, Producer<String, String> producer, String topic) throws Exception{
		logger.info("enter gennerate");
		VideoCapture camera = null;
		if(StringUtils.isNumeric(url)){
			camera = new VideoCapture(Integer.parseInt(url));//使用指定的摄像头 的设备号
		}//check camera working
		logger.info("open success");
		camera = new VideoCapture(url);
        Gson gson = new Gson();//使用json的默认库
		EventGeneratorCallback callback = new EventGeneratorCallback(cameraId);//统一的回调函数
		int framCount = 0;
		Mat mat = new Mat();
		while (camera.read(mat) != false) {
			//resize image before sending
			Mat smallframe = new Mat(Config.FRAME_HEIGHT,Config.FRAME_WIDTH,mat.type());
			Imgproc.resize(mat, smallframe, new Size(Config.FRAME_WIDTH, Config.FRAME_HEIGHT), 0, 0, Imgproc.INTER_CUBIC);
			int cols = smallframe.cols();
			int rows = smallframe.rows();
			int type = smallframe.type();
			MatOfByte buf = new MatOfByte();
			imencode(".jpg",smallframe,buf);
			byte[] data = buf.toArray();
			long milliseconds = (long) camera.get(CAP_PROP_POS_MSEC);
			milliseconds = milliseconds +  getTodayStartTime();
			Timestamp time = new Timestamp(System.currentTimeMillis());
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
			String generateFrameTime = simpleDateFormat.format(new Date(milliseconds));
//			VideoEventata obj = new VideoEventData();
//			obj.setCameraId(cameraId);
//			obj.setCols(cols);
//			obj.setRows(rows);
//			obj.setCols(type);
//			obj.setTimestamp(new Timestamp(System.currentTimeMillis()));
//			obj.setJpgImageBytes(data);
//			producer.send(new ProducerRecord<String, VideoEventData>(topic, cameraId,obj), callback);
			///////////////////////Json初始化///////////////////
			JsonObject obj = new JsonObject();
			obj.addProperty("cameraId", cameraId);
			obj.addProperty("timestamp", String.valueOf(time));
			obj.addProperty("generateFrameTime",generateFrameTime);
			obj.addProperty("rows", rows);
			obj.addProperty("cols", cols);
			obj.addProperty("type", type);
			obj.addProperty("data", Base64.getEncoder().encodeToString(data));
			String json = gson.toJson(obj);//发送的数据是已经序列化过的数据，但是kafka会在进行一次序列化
			//System.out.println(json);
			logger.info("转换成字符串格式之后进行存储:" + Base64.getEncoder().encodeToString(data).length());
		    producer.send(new ProducerRecord<>(topic, cameraId, json), callback);
			//读到的所有数据 全部发送给了同一个属性中的topic  的不同分区，都是json格式的数据,【整张图片的数据]
		}
		logger.info("Generated events for cameraId=" + cameraId  + "gap" + framCount);
		camera.release();
	}

	//发送完一帧图片的反馈方式
	private class EventGeneratorCallback implements Callback {
		private String camId;
		long size = 0;

		public EventGeneratorCallback(String camId) {
			super();
			this.camId = camId;

		}
		@Override
		public void onCompletion(RecordMetadata rm, Exception e) {
			if (rm != null) {

					//keySize=6   valueSize=33177703  （在进行压缩之前）
				logger.info("topic"+ rm.topic()+"cameraId="+ camId +
						" partition=" + rm.partition() + "keySize=" + rm.serializedKeySize()
				+ "valueSize="+ rm.serializedValueSize());

				size = size + rm.serializedValueSize();
				size = size + rm.serializedKeySize();
				System.out.println(size);
			}
			if (e != null) {

				e.printStackTrace();
			}
		}
	}
	/*返回今天的日期
	* */
	private   static  long getTodayStartTime(){

		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.MILLISECOND,0);

		return calendar.getTime().getTime();
	}

}
