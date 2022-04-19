package com.ozangunalp;

public class PointerEvent {

    public String userId;
    public String sessionId;

    public String pointerType;

    public int screenX;
    public int screenY;

    public int clientX;
    public int clientY;

    public String xpath;

    public PointerEvent() {
    }

    @Override
    public String toString() {
        return "PointerEvent{" +
                "userId='" + userId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", pointerType='" + pointerType + '\'' +
                ", screenX=" + screenX +
                ", screenY=" + screenY +
                ", clientX=" + clientX +
                ", clientY=" + clientY +
                ", xpath='" + xpath + '\'' +
                '}';
    }
}
