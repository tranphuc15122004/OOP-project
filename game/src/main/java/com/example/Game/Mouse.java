package com.example.Game;

public class Mouse {
    public  int x;
    public  int y;
    boolean Pressed;

    public void setPressed(boolean isPressed) {
        this.Pressed = isPressed;
    }

    public boolean isPressed() {
        return Pressed;
    }
}
