package ru.sealoftime.labjava.core.model.requests.network;

import jdk.jshell.spi.ExecutionControl;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.Value;
import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.data.concrete.UserData;
import ru.sealoftime.labjava.core.model.requests.Request;
import ru.sealoftime.labjava.core.model.response.Response;

public abstract class NetworkRequest extends Request {
    public NetworkRequest(UserData userData){
        this.setUserData(userData);
    }

    @Override
    @SneakyThrows
    public Response execute(ApplicationContext ctx) {
        throw new ExecutionControl.NotImplementedException("Must be executed on receiptance.");
    }


    public static class LoginRequest extends NetworkRequest {
        public LoginRequest(UserData userData) {
            super(userData);
        }
    }

    public static class RegisterRequest extends NetworkRequest{
        public RegisterRequest(UserData userData) {
            super(userData);
        }
    }
}

