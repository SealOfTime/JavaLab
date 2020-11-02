package ru.sealoftime.labjava.client.model;

import ru.sealoftime.labjava.core.model.events.Event;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Optional;

public class AccumulatingReader {
    private ByteBuffer buf;
    private int sizeDefaults;

    private int remaining = -1;
    private ByteBuffer data;
    public AccumulatingReader(int size){
        this.sizeDefaults = size;
        buf = ByteBuffer.allocate(size);
    }

    public Optional<Event> read(SocketChannel channel) throws IOException {
        if(remaining < 0){
            var sizeOrNot = readSize(channel);
            if(sizeOrNot.isEmpty())
                return Optional.empty();
            this.remaining = sizeOrNot.get();
            this.buf = ByteBuffer.allocate(this.remaining);
        }

        return this.readEvent(channel);
//        var readBytes = channel.read(buf);
//        var ais = new ByteArrayInputStream(buf.array());
//        var ois = new ObjectInputStream(ais);
//
//        if(remaining <= 0){
//            int size = ois.readInt();
//            readBytes -= 4;
//            this.remaining = size;
//            data = ByteBuffer.allocate(size);
//        }
//        data.put(ais.readAllBytes());
//        remaining -= readBytes;
//
//        if(remaining <= 0){
//            var aiso = new ByteArrayInputStream(data.array());
//            var oiso = new ObjectInputStream(aiso);
//            try {
//                var obj = oiso.readObject();
//                if(obj instanceof Event)
//                    return Optional.of( (Event)obj );
//            }catch (IOException | ClassNotFoundException e){
//                e.printStackTrace();
//            }
//            reset();
//        }
//
//        return Optional.empty();
    }

    public Optional<Integer> readSize(SocketChannel channel) throws IOException{
        var integerBuf = ByteBuffer.allocate(4);
        var readBytes = channel.read(integerBuf);
        if(readBytes < 4){ // State Machine. Может быть такое, что я прочитаю половину интеджера
            return Optional.empty();
        }
        return Optional.of(integerBuf.flip().getInt());
    }

    public Optional<Event> readEvent(SocketChannel channel) throws IOException{
        var readBytes = channel.read(this.buf);
        if(readBytes == -1)
            System.out.println("Stream has ended"); //TODO: end of stream

        this.remaining -= readBytes;
        if(this.remaining == 0){
            var bas = new ObjectInputStream(new ByteArrayInputStream(this.buf.array()));
            try {
                var obj = bas.readObject();
                if(obj instanceof Event)
                    return Optional.of((Event)obj);
                System.out.println("Пришла срань: " + obj.getClass().getSimpleName()); //TODO: logging
            } catch (ClassNotFoundException e) {
                e.printStackTrace(); //TODO: logging
            } finally{
                this.reset();
            }
        }
        return Optional.empty();
    }

    private void reset(){
        //this.buf = ByteBuffer.allocate(this.sizeDefaults);
        this.remaining = -1;
    }

}
