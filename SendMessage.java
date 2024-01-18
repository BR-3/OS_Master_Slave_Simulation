package yg;

public class SendMessage {
    public static int sendingToSlaveA = 0;
    public static int sendingToSlaveB = 0;

    public static void sendToSlaveA() {sendingToSlaveA++;}
    public static void sendToSlaveB() {sendingToSlaveB++;}
    public static void readFromSlaveA() {sendingToSlaveA--;}
    public static void readFromSlaveB() {sendingToSlaveB--;}
}
