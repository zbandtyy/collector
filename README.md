# Video Stream Collector
Video Stream Collector converts video file or webcam feed to JSON messages and sends to Kafka. This application requires following tools and technologies.

- JDK - 1.8
- Maven - 3.3.9
- ZooKeeper - 3.4.8
- Kafka - 2.11-0.10.2.0
- OpenCV - 3.2.0

Please check following configuration before running "Video Stream Collector" application. Run this application after "Video Stream Processor" application has started.
- This application uses OpenCV native libraries (.dll or .so).Set the directory path for these native libraries in system environment variable. For example, for 64-bit windows machine, path of native library file (opencv_java320.dll and opencv_ffmpeg320_64.dll) will be {OpenCV Installation Directory} \build\java\x64.
- Check "camera.url" and "camera.id" properties in "stream-collector.properties" file. 
- Set "stream-collector.log" file path in "log4j.properties" file. 
- Make sure Zookeepr and Kafka servers are up and running.
- Create "video-stream-event" topic using below command.
  ```sh
  kafka-topics.sh --create --zookeeper localhost:2181 --topic video-stream-event --replication-factor 1 --partitions 3
  ```
- Run "mvn clean" command to install opencv-320.jar in local maven repository. 
  ```sh
    mvn clean 
  ```
- Execute below command to start the "Video Stream Collector" application.
  ```sh
  mvn clean package exec:java -Dexec.mainClass="com.iot.video.app.kafka.collector.VideoStreamCollector" -Dexec.cleanupDaemonThreads=false
  ```

## 应用一数据存储格式

640 * 480的jpg格式数据

Json数据新增加一行：

​			obj.addProperty("generateFrameTime",generateFrameTime); 为该帧在  视频中的时间  + 产生该数据的日期

```java
sonObject obj = new JsonObject();
			obj.addProperty("cameraId", cameraId);
			obj.addProperty("timestamp", String.valueOf(time));
			obj.addProperty("generateFrameTime",generateFrameTime);
			obj.addProperty("rows", rows);
			obj.addProperty("cols", cols);
			obj.addProperty("type", type);
			obj.addProperty("data", Base64.getEncoder().encodeToString(data));
			String json = gson.toJson(obj);//发送的数据是已经序列化过的数据，但是kafka会在进行一次序
```

其中图片数据格式为：

```java
Imgproc.resize(mat, smallframe, new Size(Config.FRAME_WIDTH, Config.FRAME_HEIGHT), 0, 0, Imgproc.INTER_CUBIC);
			int cols = smallframe.cols();
			int rows = smallframe.rows();
			int type = smallframe.type();
			MatOfByte buf = new MatOfByte();
			imencode(".jpg",smallframe,buf);
			byte[] data = buf.toArray();
```



