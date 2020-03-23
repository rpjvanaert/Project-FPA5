package GUILogic;

import GUILogic.SimulatorLogic.Simulator;
import javafx.scene.control.Tab;

class SimulatorTab {
    private Tab simulatorTab;
    private Simulator simulator;

    SimulatorTab() {
        this.simulatorTab = new Tab("Simulator");
        this.simulator = new Simulator();
        this.simulatorTab.setContent(this.simulator.getSimulatorLayout());
    }

    Simulator getSimulator() {
        return simulator;
    }

    Tab getSimulatorTab() {
        return simulatorTab;
    }
}