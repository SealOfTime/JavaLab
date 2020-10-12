package ru.sealoftime.labjava.core.view.cli;

public class PromptCancelledException extends Exception{
    public final String prompt;
    public PromptCancelledException(String prompt){
        this.prompt = prompt;
    }
}
