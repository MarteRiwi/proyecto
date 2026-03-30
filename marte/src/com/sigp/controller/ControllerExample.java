package com.sigp.controller;

import com.sigp.service.ServiceExample;

public class ControllerExample {
    public void run() {
        System.out.println("Controlador ejecutando lógica...");
        ServiceExample service = new ServiceExample();
        service.performService();
    }
}
