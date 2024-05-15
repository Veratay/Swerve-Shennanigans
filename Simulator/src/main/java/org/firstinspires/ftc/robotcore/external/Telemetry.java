package org.firstinspires.ftc.robotcore.external;

import aperture.simulator.Renderer;
import aperture.simulator.Simulator;

import java.util.ArrayList;
import java.util.HashMap;

public class Telemetry {
    HashMap<String,Integer> captionHashMap = new HashMap<>();
    ArrayList<String> txt = new ArrayList<>();
    public void addData(String str, Object obj) {
        Integer idx = captionHashMap.get(str);
        if(idx!=null) {
            txt.set(idx,str + obj.toString());
        } else {
            captionHashMap.put(str,txt.size());
            txt.add(str + obj.toString());
        }
    }
    public void addData(String str, String format, Object obj) {
        addData(str,str + String.format(format,obj) + "\n");
    }

    public void addLine(String str) {
        txt.add(str);
    }
    public void update() {
        String displayTxt = "";
        for (int i=0; i<txt.size(); i++) {
            displayTxt += txt.get(i);
        }
        captionHashMap.clear();
        txt.clear();

        Renderer.updateTelemetryString(displayTxt);

        Simulator.update();
    }
}
