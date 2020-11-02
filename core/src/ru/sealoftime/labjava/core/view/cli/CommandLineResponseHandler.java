package ru.sealoftime.labjava.core.view.cli;

import ru.sealoftime.labjava.core.model.data.concrete.SpaceMarine;
import ru.sealoftime.labjava.core.model.events.Event;
import ru.sealoftime.labjava.core.model.response.InfoResponse;
import ru.sealoftime.labjava.core.model.response.ListResponse;
import ru.sealoftime.labjava.core.model.response.Response;
import ru.sealoftime.labjava.core.model.response.SumOfHealthResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CommandLineResponseHandler {
    private final CommandLineUserInterface cli;
    public CommandLineResponseHandler(CommandLineUserInterface cli){
        this.cli=cli;
    }
    private final Map<String, Consumer<Response>> callbacks = new HashMap<>();
    {
        callbacks.put("help", this::onHelp);
        callbacks.put("history", this::onHistory);
        callbacks.put("clear", this.simple("clear"));
        callbacks.put("exit", this.simple("exit"));
        callbacks.put("info", this::onInfo);
        callbacks.put("remove_first", this.simple("remove_first"));
        callbacks.put("save", this.simple("save"));
        callbacks.put("show", this::onShow);
        callbacks.put("sum_of_health", this::onSumOfHealth);
        callbacks.put("add", this.simple("add"));
        callbacks.put("remove_greater", this.simple("remove_greater"));
        callbacks.put("update", this.simple("update"));
        callbacks.put("execute_script", this.simple("execute_script"));
        callbacks.put("filter_less_than_weapon_type", this::onFilterLessThanWeaponType);
        callbacks.put("print_field_ascending_chapter", this::onPrintFieldAscendingChapter);
        callbacks.put("remove_by_id", this.simple("remove_by_id"));
        callbacks.put("login", this.simple("login"));
    }
    public Consumer<Response> simple(String name){
        return (resp)->{
            if(resp.getStatus().equals(Response.ResponseStatus.FAIL))
                this.cli.print(((Response.ErrorResponse)resp).getErrorMessage());
            else
                this.cli.print("commandline.response.success." + name);
        };
    }

    public void onResponse(Event e){
        var resp = (Response)e;
        var command = resp.getCommand();
        var callback = this.callbacks.get(command);
        if(callback == null){
            this.cli.print("application.error.no_callback", command);
            return;
        }
        callback.accept(resp);
    }

    public void onHistory(Event event) {
        this.cli.printHistory();
    }

    public void onInfo(Response resp){
        var infos = (InfoResponse) resp;
        this.cli.print("commandline.response.info", infos.getType(), infos.getSize(), infos.getCreationDate());
    }

    public void onHelp(Response resp){
        var infos = (ListResponse<String>) resp;
        this.cli.print("commandline.help");
        infos.getData().forEach(this.cli::print);
    }
    public void onShow(Response resp){
        var items = (ListResponse<SpaceMarine>)resp;
        this.cli.print("commandline.response.show");
        items.getData().forEach((sm)->this.cli.print("commandline.response.show.spacemarine", sm.toString()));
    }
    public void onSumOfHealth(Response resp){
        var soh = (SumOfHealthResponse) resp;
        this.cli.print("commandline.response.sum_of_health", soh.getSum());
    }

    private void onFilterLessThanWeaponType(Response response) {
        var items = (ListResponse<SpaceMarine>) response;
        this.cli.print("commandline.response.on_filter_less_than_weapon_type");
        items.getData().forEach((sm)->this.cli.print("commandline.response.show.spacemarine", sm.toString()));
    }

    private void onPrintFieldAscendingChapter(Response response) {
        var items = (ListResponse<?>) response;
        this.cli.print("commandline.response.on_print_field_ascending_chapter");
        items.getData().forEach((item)->this.cli.print("commandline.response.print_field_ascending_chapter", item.toString()));
    }



}
