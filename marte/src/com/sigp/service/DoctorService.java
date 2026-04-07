package com.sigp.service;

import com.sigp.exception.CustomException;
import com.sigp.model.Doctor;
import com.sigp.repository.DoctorRepository;
import java.util.List;

/**
 * Servicio de gestión de doctores.
 * Contiene la lógica de negocio: validaciones, registro, actualización y eliminación.
 * Basado en el proyecto de Stiven, usando CustomException para errores controlados.
 */
public class DoctorService {

    private final DoctorRepository repository;

    public DoctorService() {
        this.repository = new DoctorRepository();
    }

    /** Registra un nuevo doctor después de validar sus campos y verificar cédula duplicada. */
    public void registrarDoctor(String nombreCompleto, int edad, String cedula, String especialidad)
            throws CustomException {
        validarCampos(nombreCompleto, edad, cedula, especialidad);

        if (repository.buscarPorCedula(cedula) != null) {
            throw new CustomException("Ya existe un doctor registrado con la cédula " + cedula + ".");
        }

        Doctor doctor = new Doctor(nombreCompleto, edad, cedula, especialidad);
        repository.guardarDoctor(doctor);
    }

    /** Retorna la lista completa de doctores. */
    public List<Doctor> obtenerDoctores() {
        return repository.obtenerDoctores();
    }

    /** Retorna el último doctor registrado (útil para mostrar el ID asignado). */
    public Doctor obtenerUltimoRegistrado() {
        List<Doctor> doctores = repository.obtenerDoctores();
        if (doctores.isEmpty()) return null;
        return doctores.get(doctores.size() - 1);
    }

    /** Busca un doctor por ID interno. Lanza excepción si no existe. */
    public Doctor buscarDoctorPorId(int id) throws CustomException {
        if (id <= 0) {
            throw new CustomException("El ID debe ser mayor a cero.");
        }
        Doctor doctor = repository.buscarPorId(id);
        if (doctor == null) {
            throw new CustomException("No se encontró un doctor con el ID " + id + ".");
        }
        return doctor;
    }

    /** Actualiza los datos de un doctor existente por su ID. */
    public void actualizarDoctor(int id, String nombreCompleto, int edad, String cedula, String especialidad)
            throws CustomException {
        validarCampos(nombreCompleto, edad, cedula, especialidad);

        Doctor existente = buscarDoctorPorId(id);
        Doctor conCedula = repository.buscarPorCedula(cedula);

        // Permite la misma cédula si pertenece al mismo doctor
        if (conCedula != null && conCedula.getId() != id) {
            throw new CustomException("Ya existe otro doctor con la cédula " + cedula + ".");
        }

        existente.setNombreCompleto(nombreCompleto);
        existente.setEdad(edad);
        existente.setCedula(cedula);
        existente.setEspecialidad(especialidad);
    }

    /** Elimina un doctor buscándolo por su cédula. */
    public void eliminarDoctorPorCedula(String cedula) throws CustomException {
        if (cedula == null || cedula.trim().isEmpty()) {
            throw new CustomException("La cédula es obligatoria para eliminar un doctor.");
        }
        boolean eliminado = repository.eliminarPorCedula(cedula.trim());
        if (!eliminado) {
            throw new CustomException("No se encontró un doctor con la cédula " + cedula + ".");
        }
    }

    // ── Validaciones internas ──────────────────────────────────────────────────

    private void validarCampos(String nombreCompleto, int edad, String cedula, String especialidad)
            throws CustomException {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new CustomException("El nombre completo es obligatorio.");
        }
        if (!nombreCompleto.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{3,60}$")) {
            throw new CustomException("Nombre inválido. Solo letras y espacios, entre 3 y 60 caracteres.");
        }
        if (edad < 18 || edad > 75) {
            throw new CustomException("Edad inválida. Un doctor debe tener entre 18 y 75 años.");
        }
        if (cedula == null || !cedula.matches("^[0-9]{6,10}$")) {
            throw new CustomException("Cédula inválida. Debe contener entre 6 y 10 dígitos numéricos.");
        }
        if (especialidad == null || especialidad.trim().isEmpty()) {
            throw new CustomException("La especialidad es obligatoria.");
        }
    }
}