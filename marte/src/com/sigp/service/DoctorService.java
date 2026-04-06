package com.sigp.service;

import com.sigp.exception.CustomException;
import com.sigp.model.Doctor;
import com.sigp.repository.DoctorRepository;

import java.util.List;

public class DoctorService {
    private final DoctorRepository repository;

    public DoctorService() {
        this.repository = new DoctorRepository();
    }

    public void registrarDoctor(String nombreCompleto, int edad, String cedula, String especialidad)
            throws CustomException {
        validarCampos(nombreCompleto, edad, cedula, especialidad);

        if (repository.buscarPorCedula(cedula) != null) {
            throw new CustomException("Ya existe un doctor registrado con la cedula " + cedula + ".");
        }

        Doctor doctor = new Doctor(nombreCompleto, edad, cedula, especialidad);
        repository.guardarDoctor(doctor);
    }

    public List<Doctor> obtenerDoctores() {
        return repository.obtenerDoctores();
    }

    public Doctor obtenerUltimoDoctorRegistrado() {
        List<Doctor> doctores = repository.obtenerDoctores();
        if (doctores.isEmpty()) {
            return null;
        }
        return doctores.get(doctores.size() - 1);
    }

    public Doctor buscarDoctorPorId(int id) throws CustomException {
        if (id <= 0) {
            throw new CustomException("El ID debe ser mayor a cero.");
        }

        Doctor doctor = repository.buscarPorId(id);
        if (doctor == null) {
            throw new CustomException("No se encontro un doctor con el ID " + id + ".");
        }

        return doctor;
    }

    public void actualizarDoctor(int id, String nombreCompleto, int edad, String cedula, String especialidad)
            throws CustomException {
        validarCampos(nombreCompleto, edad, cedula, especialidad);

        Doctor doctorExistente = buscarDoctorPorId(id);
        Doctor doctorConCedula = repository.buscarPorCedula(cedula);

        if (doctorConCedula != null && doctorConCedula.getId() != id) {
            throw new CustomException("Ya existe otro doctor registrado con la cedula " + cedula + ".");
        }

        doctorExistente.setNombreCompleto(nombreCompleto);
        doctorExistente.setEdad(edad);
        doctorExistente.setCedula(cedula);
        doctorExistente.setEspecialidad(especialidad);
    }

    public void eliminarDoctorPorCedula(String cedula) throws CustomException {
        if (cedula == null || cedula.trim().isEmpty()) {
            throw new CustomException("La cedula es obligatoria para eliminar un doctor.");
        }

        boolean eliminado = repository.eliminarPorCedula(cedula.trim());
        if (!eliminado) {
            throw new CustomException("No se encontro un doctor con la cedula " + cedula + ".");
        }
    }

    private void validarCampos(String nombreCompleto, int edad, String cedula, String especialidad)
            throws CustomException {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new CustomException("El nombre completo es obligatorio.");
        }
        if (edad <= 0) {
            throw new CustomException("La edad debe ser mayor a cero.");
        }
        if (cedula == null || cedula.trim().isEmpty()) {
            throw new CustomException("La cedula es obligatoria.");
        }
        if (especialidad == null || especialidad.trim().isEmpty()) {
            throw new CustomException("La especialidad es obligatoria.");
        }
    }
}
