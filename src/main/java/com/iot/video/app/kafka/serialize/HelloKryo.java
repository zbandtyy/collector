package com.iot.video.app.kafka.serialize;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.*;

public class HelloKryo {

    static public void main (String[] args) throws Exception {
        Kryo kryo = new Kryo();
        kryo.register(Person.class);

        Person object = new Person();
        object.setAge(10);
        object.setId(1);
        object.setName("hello");

        Output output = new Output(new FileOutputStream("file.bin"));
        kryo.writeObject(output, object);
        output.close();



        Input input = new Input(new FileInputStream("file.bin"));
        Person object2 = kryo.readObject(input, Person.class);
        System.out.println(object2);
        input.close();
    }

}
