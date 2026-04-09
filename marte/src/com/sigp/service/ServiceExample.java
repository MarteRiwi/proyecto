package com.sigp.service;

import com.sigp.repository.RepositoryExample;

public class ServiceExample {
    public void performService() {
        System.out.println("Servicio en acción...");
        RepositoryExample repo = new RepositoryExample();
        repo.getData();
    }
}
