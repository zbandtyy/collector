package com.iot.video.app.kafka.serialize;

/**
 * @author ：tyy
 * @date ：Created in 2020/7/29 16:19
 * @description：
 * @modified By：
 * @version: $
 */
/**
 * Created by shirukai on 2018/8/25
 */
public class Person {
    private int id;
    private String name;
    private int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
