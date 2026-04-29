package marte.src.com.sigp.service;

import marte.src.com.sigp.repository.RepositoryExample;

public class ServiceExample {
    public void performService() {
        System.out.println("Servicio en acción...");
        RepositoryExample repo = new RepositoryExample();
        repo.getData();
    }
}
