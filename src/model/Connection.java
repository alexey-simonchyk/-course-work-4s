package model;

import controller.Controller;

public abstract class Connection extends Thread {

    protected final String PASS_MOVE = "PASS";
    protected final String TAKE_MOVE = "TAKE";
    protected final String END_GAME_WIN = "WIN";
    protected final String END_GAME_LOSE = "LOSE";
    protected final String END_GAME_NO_WIN = "NO_WIN";
    protected volatile Player player;
    protected volatile Game game;
    protected volatile Controller controller;

    protected void setIsStop() {}
    protected void sendCommand(boolean isPass) {}
    protected void sendUpdate(Card card) {}
    protected void sendMessage(String message) {}
    protected boolean checkReceivedData(byte[] data) { return true; }

}
