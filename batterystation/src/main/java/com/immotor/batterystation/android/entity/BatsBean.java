package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by jm on 2017/9/20 0020.
 */

public class BatsBean implements Serializable {
        /**
         * nominalVoltage : 100
         * nominalCurrent : 120
         * damage : 3
         * current : 10
         * soc : 90
         * temperature : 20
         * fault : 63
         * id : 1103DCB1B1AE
         * cycleCount : 10
         * portNumber : 0
         * voltage : 10
         * capacity : 10
         */

        private int nominalVoltage;
        private int nominalCurrent;
        private int damage;
        private int current;
        private int soc;
        private int temperature;
        private int fault;
        private String id;
        private int cycleCount;
        private int portNumber;
        private int voltage;
        private int capacity;

        public int getNominalVoltage() {
            return nominalVoltage;
        }

        public void setNominalVoltage(int nominalVoltage) {
            this.nominalVoltage = nominalVoltage;
        }

        public int getNominalCurrent() {
            return nominalCurrent;
        }

        public void setNominalCurrent(int nominalCurrent) {
            this.nominalCurrent = nominalCurrent;
        }

        public int getDamage() {
            return damage;
        }

        public void setDamage(int damage) {
            this.damage = damage;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getSoc() {
            return soc;
        }

        public void setSoc(int soc) {
            this.soc = soc;
        }

        public int getTemperature() {
            return temperature;
        }

        public void setTemperature(int temperature) {
            this.temperature = temperature;
        }

        public int getFault() {
            return fault;
        }

        public void setFault(int fault) {
            this.fault = fault;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getCycleCount() {
            return cycleCount;
        }

        public void setCycleCount(int cycleCount) {
            this.cycleCount = cycleCount;
        }

        public int getPortNumber() {
            return portNumber;
        }

        public void setPortNumber(int portNumber) {
            this.portNumber = portNumber;
        }

        public int getVoltage() {
            return voltage;
        }

        public void setVoltage(int voltage) {
            this.voltage = voltage;
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }
}
