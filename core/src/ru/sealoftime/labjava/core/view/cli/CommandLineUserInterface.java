package ru.sealoftime.labjava.core.view.cli;

import ru.sealoftime.labjava.core.ApplicationContext;
import ru.sealoftime.labjava.core.model.response.Response;
import ru.sealoftime.labjava.core.util.Either;
import ru.sealoftime.labjava.core.util.FormattedString;
import ru.sealoftime.labjava.core.util.UnsafeFunction;
import ru.sealoftime.labjava.core.view.UserInterface;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

public class CommandLineUserInterface implements TextExecutionContext, UserInterface, Closeable{

    public Scanner sc;

    private CommandRegistry commandRegistry;
    public void setCommandRegistry(CommandRegistry cr){
        this.commandRegistry = cr;
    }
    private ApplicationContext ctx;

    private LinkedList<String> history;

    public CommandLineUserInterface(CommandRegistry cr, ApplicationContext ctx){
        this.commandRegistry = cr;
        this.sc = new Scanner(System.in);
        this.ctx = ctx;
        this.history = new LinkedList<>();
    }

    @Override
    public void acceptUserInput() {

        if (sc.hasNextLine()){
            String rawInput = sc.nextLine().trim();
            if(rawInput.isBlank())
                return;

            String[] input = rawInput.split("\\s+");
            var command = input[0];
            Command commandOrNone = this.commandRegistry.get(command);
            if(commandOrNone == null)
                this.print("commandline.error.no_such_command");
            else {
                history.add(command);
                if(history.size() > 10) history.poll();

                var req = commandOrNone.constructRequest(this, this.ctx, input);
                if(req.isPresent()) {
                    var resp = this.ctx.getRequestExecutor().execute(req.get());
                    if(resp != null)
                        if(resp.getStatus().equals(Response.ResponseStatus.SUCCESS)){
                            this.ctx.getEventBus().notify(resp);
    //                        this.print("commandline.command." + command + ".success");
    //                        if(resp instanceof ListResponse){
    //                            var listResponse = (ListResponse<?>)resp;
    //                            for(Object o : listResponse.getData())
    //                                this.print(o.toString());
    //                        }
                        }else
                            this.print(((Response.ErrorResponse)resp).getErrorMessage());
                }
            }
        }
    }
    public void printHistory(){
        this.print("commandline.response.history");
        this.history.forEach((cmd)->
                this.print("commandline.response.history.element", cmd));
    }

    public void print(FormattedString fs){
        this.print(fs.getLine(), fs.getData());
    }
    @Override
    public void print(String rawOutput, Object... data){
        System.out.println(ctx.getLocalization().localize(rawOutput, data));
    }

    public boolean confirm(String query){
        while(true){
            var prompt = this.prompt(query);
            if(prompt.isPresent()){
                switch(prompt.get()){
                    case "+": return true;
                    case "-": return false;
                }
            }
        }
    }

    /**Asks user to enter a string
     * @return Empty optional sometimes
     * @throws PromptCancelledException when user has cancelled the prompt
     * @see Optional
     * **/
    public Optional<String> prompt(String query){
        this.print(query);
        if(this.sc.hasNextLine()){
            String prompt = sc.nextLine().trim();
            return Optional.of(prompt);
        }
        return Optional.empty();
    }
    public <T> Optional<T> promptValue(String query, UnsafeFunction<String, T, ? extends Exception> parser){
        Either<T, String> result = this.prompt(query, (s)->{
            try{
                return Either.left(parser.apply(s));
            }catch(Exception e){
                return Either.right("");
            }
        });
        if(result.isLeft())
            return Optional.of(result.left());
        if(!result.right().isEmpty())
            print(result.right());
        return Optional.empty();
    }

    @Override
    public <T> Either<T, String> prompt(String query, Function<String, Either<T, String>> parser){
        var rawResultOrNot = this.prompt(query);
        if(rawResultOrNot.isEmpty())
            return Either.right("");

        var rawResult = rawResultOrNot.get();
        var result = parser.apply(rawResult);
        return result;
    }

    @Override
    public <T> Either<T, String> promptOrCancel(String query, Function<String, Either<T, String>> parser){
        var parsedPrompt = this.prompt(query, parser);
        while(parsedPrompt.isRight()){
            this.print(parsedPrompt.right());
            if(this.confirm("commandline.prompt.cancel"))
                return Either.right("commandline.prompt.cancelled");
            parsedPrompt = this.prompt(query, parser);
        }
        return parsedPrompt;
    }

    @Override
    public void close() throws IOException {
        this.sc.close();
    }
}
