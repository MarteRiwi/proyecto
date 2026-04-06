package com.sigp;

import com.sigp.controller.DoctorController;

public class Main {
    public static void main(String[] args) {
        System.out.println("Proyecto SIGP iniciado");
        DoctorController controller = new DoctorController();
        controller.run();
    }
}
