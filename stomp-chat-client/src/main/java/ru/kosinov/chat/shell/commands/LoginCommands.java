package ru.kosinov.chat.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.kosinov.chat.shell.service.AuthorizationService;

@ShellComponent
public class LoginCommands {

    @Autowired
    private  AuthorizationService service;

    @ShellMethod("Authorization command for chat server")
    public String login(
            @ShellOption(defaultValue = "") String uname,
            @ShellOption(defaultValue = "") String password  ) {
        return this.service.authorize(uname, password);
    }
}
