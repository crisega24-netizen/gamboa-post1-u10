package com.universidad.enviosservice.estrategia;

/**
 * Algoritmo de costo base de un metodo de envio (patron Strategy).
 * Cada implementacion encapsula la formula y las reglas propias de su metodo;
 * los ajustes transversales (fragilidad, tipo de cliente) los aplica el orquestador.
 */
public interface EstrategiaEnvio {

    double calcular(double pesoKg, double distanciaKm);
}
