package cz.reindl.flashlight;

public enum State {

    ON("ON"),
    OFF("OFF");

    String status;

    State(String status) {
        this.status = status;
    }

}
