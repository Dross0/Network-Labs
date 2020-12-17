package ru.gaidamaka.presenter.event;


public class ExitEvent extends UserEvent {
    public ExitEvent() {
        super(UserEventType.EXIT);
    }
}
