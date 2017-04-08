package cz.v4hack.v4hack2017;

import java.io.Serializable;
import java.util.ArrayList;

public class LineData implements Serializable {

    private ArrayList<LineData> list;
    private String station;
    private String lineNumber;
    private String type;
    private String firstDestination;
    private String secondDestination;
    private String firstTime;
    private String secondTime;

    public ArrayList<LineData> getList() {
        return list;
    }

    public void setList(ArrayList<LineData> list) {
        this.list = list;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFirstDestination() {
        return firstDestination;
    }

    public void setFirstDestination(String firstDestination) {
        this.firstDestination = firstDestination;
    }

    public String getSecondDestination() {
        return secondDestination;
    }

    public void setSecondDestination(String secondDestination) {
        this.secondDestination = secondDestination;
    }

    public String getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }

    public String getSecondTime() {
        return secondTime;
    }

    public void setSecondTime(String secondTime) {
        this.secondTime = secondTime;
    }
}
