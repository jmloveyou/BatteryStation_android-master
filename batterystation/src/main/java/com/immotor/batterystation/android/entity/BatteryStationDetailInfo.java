package com.immotor.batterystation.android.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashion on 2017/5/4.
 * 电池桩详细信息
 */

public class BatteryStationDetailInfo   implements Serializable {
// {
//      "result":
//      {
//          "id":"3C001100",
//          "type":0,
//          "ports":
//          [
//              {
//                  "current":0,
//                  "port":1,
//                  "battery":
//                  {
//                      "current":-159,
//                      "port":1,
//                      "soc":81,
//                      "temperature":35,
//                      "fault":0,
//                      "nominal_voltage":5760,
//                      "bID":"5E1416640410",
//                      "cycle":0,
//                      "nominal_current":3000,
//                      "voltage":5337,
//                      "capacity":100
//                  },
//                  "voltage":48
//              },
//              {
//                  "current":0,
//                  "port":2,
//                  "voltage":48
//              },
//              {
//                  "current":0,
//                  "port":3,
//                  "battery":
//                  {
//                      "current":0,
//                      "port":3,
//                      "soc":99,
//                      "temperature":28,
//                      "fault":0,
//                      "nominal_voltage":5760,
//                      "bID":"F56DB1630410",
//                      "cycle":1,
//                      "nominal_current":3000,
//                      "voltage":5359,
//                      "capacity":100
//                  },
//                  "voltage":48
//             },
//             {
//                  "current":0,
//                  "port":4,
//                  "voltage":48
//             },
//             {
//                  "current":0,
//                  "port":5,
//                  "voltage":48
//             },
//             {
//                  "current":0,
//                  "port":6,
//                  "battery":
//                  {
//                      "current":0,
//                      "port":6,
//                      "soc":90,
//                      "temperature":28,
//                      "fault":0,
//                      "nominal_voltage":5760,
//                      "bID":"241313640410",
//                      "cycle":0,
//                      "nominal_current":3000,
//                      "voltage":5238,
//                      "capacity":100
//                  },
//                  "voltage":48
//              }
//          ]
//      },
//      "code":"600"
// }

    private String id;
    private int type;
    private List<PortInStation> ports = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<PortInStation> getPorts() {
        return ports;
    }

    public void setPorts(List<PortInStation> ports) {
        this.ports = ports;
    }

    public void clone(BatteryStationDetailInfo info){
        id = info.id;
        type = info.type;
        ports.clear();
        ports.addAll(info.ports);
    }


    public static class PortInStation{
        private int current;
        private int port;
        private int voltage;
        private BatteryInStationInfo battery;

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getVoltage() {
            return voltage;
        }

        public void setVoltage(int voltage) {
            this.voltage = voltage;
        }

        public BatteryInStationInfo getBattery() {
            return battery;
        }

        public void setBattery(BatteryInStationInfo battery) {
            this.battery = battery;
        }

    }
}
