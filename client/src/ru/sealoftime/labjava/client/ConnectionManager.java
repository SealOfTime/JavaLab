package ru.sealoftime.labjava.client;

import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.util.FormattedString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.Optional;

public class ConnectionManager {
    private SocketAddress address;
    private SocketChannel channel;
    private ApplicationContext context;

    private AccumulatingReader accumulatingReader;

    public ConnectionManager(String hostname, int port, ApplicationContext context){
        this.address = new InetSocketAddress(hostname, port);
        this.accumulatingReader = new AccumulatingReader(1024);
        this.context = context;
    }

    public void receive(){
        try {
            var event = accumulatingReader.read(channel);
//            while(event.isEmpty())
//                event = accumulatingReader.read(channel);
            event.ifPresent((e)->
                    context.getEventBus().notify(e)
            );
        }catch(IOException e){
            System.out.println("Server is temporarily dead, reconnect in 5s");//todo: logging
            try {
                Thread.sleep(5000);
                var errs = this.connect();
                if(errs.isEmpty()) System.out.println("We are back.");
            }catch(InterruptedException ie){ ie.printStackTrace(); }
        }
    }

    public void send(Request req){
        var baos = new ByteArrayOutputStream();
        try (var oos = new ObjectOutputStream(baos)){

            oos.writeObject(req);
            var rawReq = baos.toByteArray();

            var message = ByteBuffer.allocate(rawReq.length + 4)
                                    .putInt(rawReq.length)
                                    .put(rawReq)
                                    .flip();
            channel.write(message);
        } catch (IOException e) {
            System.out.println("Server is experiencing issues at the moment"); //TODO: logging
        }
    }

    public Optional<FormattedString> connect(){
        try {
            this.channel = SocketChannel.open(this.address);
            this.channel.configureBlocking(false);
           // this.channel.finishConnect();
        }catch(UnresolvedAddressException unresolved){
            return Optional.of(new FormattedString("application.error.address_unresolved", this.address.toString()));
        }catch(IOException io){
            io.getLocalizedMessage();
            return Optional.of(new FormattedString("application.error.connection", io.getMessage()));
        }
        return Optional.empty();
    }

    public boolean close(){
        try {
            if(this.channel != null)
                this.channel.close();
            return true;
        }catch(IOException io){
            return false;
        }
    }
}
