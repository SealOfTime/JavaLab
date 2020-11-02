package ru.sealoftime.labjava.server;

import lombok.SneakyThrows;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.jmx.Server;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.data.concrete.UserData;
import ru.sealoftime.labjava.core.model.events.Event;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.requests.network.NetworkRequest;
import ru.sealoftime.labjava.core.model.response.LoginResponse;
import ru.sealoftime.labjava.core.model.response.Response;
import ru.sealoftime.labjava.core.view.UserInterface;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;


public class RemoteUserInterface implements UserInterface, Closeable {
    private Socket remote;
    private OutputStream remoteOut;
    private InputStream remoteIn;
    private ApplicationContext ctx;
    private UserData session;

    private static ThreadLocal<RemoteUserInterface> client = new ThreadLocal<>();
    public static synchronized void setSession(RemoteUserInterface ui){ client.set(ui); }
    public static UserData getSession(){ return client.get().session; }

    private ExecutorService processPool = Executors.newCachedThreadPool();
    private ForkJoinPool sendPool = ForkJoinPool.commonPool();
    public boolean isConnected;

    public RemoteUserInterface(Socket remote, ApplicationContext ctx) throws IOException {
        this.remote = remote;
        this.remoteOut = remote.getOutputStream();
        this.remoteIn = remote.getInputStream();
        this.ctx = ctx;
        isConnected = true;
        setSession(this);
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
                    var resp = new CompletableFuture<Response>();
                    processPool.execute(()->{
                        setSession(this);
                        this.send(processRequest(req));
                    });
                    return;
                }
            }
            ServerApplication.logger.info("Client has disconnected.");
            ServerApplication.clients.remove(this);
            this.isConnected = false;
            //TODO: Connection has been closed
        } catch (IOException e) {
            ServerApplication.logger.error("Client has died.");
            ServerApplication.clients.remove(this);
            this.isConnected = false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); //todo: logging
        }
    }

    public void send(Event event){
        final var ud = getSession();
        sendPool.execute(()->{
            setSession(this);
            try {
                this.remoteOut.write(
                        this.constructMessage(event)
                );
            }catch(IOException e){
                ServerApplication.logger.error(ctx.getLocalization().localize("server.error.unexpected", e.getMessage()));
            }
        });

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

    private Response processRequest(Request req){
        if(req instanceof NetworkRequest)
            return processNetworkRequest((NetworkRequest)req);
        if(getSession() == null || !getSession().equals(req.getUserData()))
            return Response.fail("login", "client.error.not_logged_in");
        return this.ctx.getRequestExecutor().execute(req);
    }

    @SneakyThrows
    private Response processNetworkRequest(NetworkRequest req){
        String cmd = "login";
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        var uData = req.getUserData();
        var login = uData.getUsername();
        var password = uData.getPassword();
        if(req instanceof NetworkRequest.LoginRequest){
            var check = ServerApplication.dcm.checkCredentials(login, password);
            if(check > 0){
                setSession(this);
                this.session = uData;
                return new LoginResponse(uData);
            }else if(check == -1)
                return Response.fail("login", "client.error.invalid_password");
            else
                return Response.fail("login", "client.error.auth.unknown");
        } else if(req instanceof NetworkRequest.RegisterRequest){
            var execs = ServerApplication.dcm.performQueryWithOneStatement(
                    "INSERT INTO users (login, password) VALUES (?, ?)", (stmt)->{
                           stmt.setString(1, login);
                           stmt.setString(2, ServerApplication.dcm.get_SHA_512_SecurePassword(password, "whyDoIStillExist"));
                           return stmt.executeUpdate();
             });
            if(execs > 0){
                setSession(this);
                this.session = uData;
                return new LoginResponse(uData);
            }
            return Response.fail("login", "client.error.register.login_occupied");
        }
        return Response.fail("login", "client.error.invalid_credentials");
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
