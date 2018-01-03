package com.immotor.batterystation.android.bluetooth;

import java.util.HashMap;

/**
 * Created by Ashion on 2016/8/3.
 */
public class BLGattAttributes {

    private static HashMap<String, String> attributes = new HashMap();

    public static String SCOOTER_SERVICE_UUID = "14839AC4-7D7E-415C-9A42-167340CF2339";
    public static String COMMAND_CHARACTERISTIC_UUID = "8B00ACE7-EB0B-49B0-BBE9-9AEE0A26E1A3";
    public static String NOTIFICATION_CHARACTERISTIC_UUID = "0734594A-A8E7-4B1A-A6B1-CD5243059A57";

   /* static {
        // Sample Services.
        attributes.put(SCOOTER_SERVICE_UUID, "Scooter Service");   //SCOOTER_SERVICE_UUID
        attributes.put(COMMAND_CHARACTERISTIC_UUID, "Command Characteristic UUID");   //COMMAND_CHARACTERISTIC_UUID
        attributes.put(NOTIFICATION_CHARACTERISTIC_UUID, "Notification Characteristic UUID");   //NOTIFICATION_CHARACTERISTIC_UUID
    }*/

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
