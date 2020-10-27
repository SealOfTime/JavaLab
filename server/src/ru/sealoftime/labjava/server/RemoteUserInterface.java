package ru.sealoftime.labjava.server;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.jmx.Server;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.events.Event;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.view.UserInterface;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class RemoteUserInterface implements UserInterface, Closeable {
    private Socket remote;
    private OutputStream remoteOut;
    private InputStream remoteIn;
    private ApplicationContext ctx;

    public RemoteUserInterface(Socket remote, ApplicationContext ctx) throws IOException {
        this.remote = remote;
        this.remoteOut = remote.getOutputStream();
        this.remoteIn = remote.getInputStream();
        this.ctx = ctx;
    }

    @Override
    public void acceptUserInput() {
        try {
            var sizeBytes = this.remoteIn.readNBytes(4); //read size
            if(sizeBytes.length > 0) {
                var size = ByteBuffer.wrap(sizeBytes).getInt();
                ServerApplication.logger.info(String.format("Incoming message of size %d from %s", size, remote.getInetAddress()));
                var reqBytes = this.remoteIn.readNBytes(size);  //read the object
                if(reqBytes.length > 0) {
                    var req = reconstructRequest(reqBytes);
                    processRequest(req);
                    return;
                }
            }
            ServerApplication.logger.info("Client has disconnected.");
            ServerApplication.clients.remove(this);
            //TODO: Connection has been closed
        } catch (IOException e) {
            ServerApplication.logger.error("Client has died.");
            ServerApplication.clients.remove(this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); //todo: logging
        }
    }

    public void send(Event event){
        try {
            this.remoteOut.write(
                    this.constructMessage(event)
            );
        }catch(IOException e){
            ServerApplication.logger.error(ctx.getLocalization().localize("server.error.unexpected", e.getMessage()));
        }
    }

    private Request reconstructRequest(byte[] reqBytes) throws IOException, ClassNotFoundException{
        var bais = new ByteArrayInputStream(reqBytes);
        var remoteObjectIn = new ObjectInputStream(bais);

        var readObject = remoteObjectIn.readObject();

        ServerApplication.logger.info(String.format("Receive %s from %s", readObject.getClass().getSimpleName(), this.remote.getInetAddress().toString())); //TODO: logging

        if (readObject instanceof Request) {
            return (Request)readObject;
        }
        throw new InvalidObjectException("It should have been Request");
    }

    private void processRequest(Request req){
        var resp = this.ctx.getRequestExecutor().execute(req);
        this.send(resp);
    }

    private byte[] constructMessage(Object obj) throws IOException{
        var baos = new ByteArrayOutputStream();
        try (var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            var objectRaw = baos.toByteArray();

            return ByteBuffer.allocate(objectRaw.length + 4)
                            .putInt(objectRaw.length)
                            .put(objectRaw)
                            .flip()
                            .array();
        }
    }

    @Override
    public void close() throws IOException {
        this.remoteIn.close();
        this.remoteOut.close();
        this.remote.close();
    }
}
