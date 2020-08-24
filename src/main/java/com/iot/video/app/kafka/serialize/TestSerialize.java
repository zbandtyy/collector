package com.iot.video.app.kafka.serialize;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：tyy
 * @date ：Created in 2020/7/29 16:21
 * @description：
 * @modified By：
 * @version: $
 */
public class TestSerialize {
    public static void main(String[] args) {
        List<Person> students = new ArrayList<Person>();



        Person person = new Person();
        person.setId(1);
        person.setName("personJsonSerialization_" + 1);
        person.setAge(18);
        students.add(person);
        byte[] bytes = ProtostuffUtil.serializer(person,Person.class);
        System.out.println("序列化后: " + bytes.length);

        Person deserializer = ProtostuffUtil.deserializer(bytes, Person.class);
        System.out.println(deserializer);


    }
}
