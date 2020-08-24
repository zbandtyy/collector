package com.iot.video.app.kafka.config;

import com.iot.video.app.kafka.util.PropertyFileReader;

import java.util.Properties;

/**
 * @author ：tyy
 * @date ：Created in 2020/3/14 20:03
 * @description：代码中所有的配置
 * @modified By：
 * @version: $
 */
public class Config {
    //opencv函数库的配置路径 包含库的名字.so
    public final static  String OPENCV_LIB_PATH = "/home/user/Apache/opencv3.4.7-install/lib/libopencv_java347.so";
    //对于KakaKA参数设置的路径,包含文件名
    public final static  String KAFKA_CONFIG_PATH = "./stream-collector.properties";

    public   static int FRAME_HEIGHT = 480;
    public   static int FRAME_WIDTH = 640;

    static {

        try {
            Properties properties = new PropertyFileReader().readPropertyFile();
            String height = properties.getProperty("frame.height");
            if(height != null){
                FRAME_HEIGHT = Integer.valueOf(height);
            }
            String width = properties.getProperty("frame.width");
            if(height != null){
                FRAME_WIDTH = Integer.valueOf(height);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
