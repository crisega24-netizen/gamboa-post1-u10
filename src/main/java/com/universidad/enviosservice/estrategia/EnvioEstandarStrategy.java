package com.universidad.enviosservice.estrategia;

/** ESTANDAR: 0,5 por km, recargo de 10 para distancias mayores a 500 km. */
public class EnvioEstandarStrategy implements EstrategiaEnvio {

    private static final TarifaPorPeso TARIFA = new TarifaPorPeso(0.5, 0, 0.8, 1.2);
    private static final double DISTANCIA_LARGA_KM = 500;
    private static final double RECARGO_DISTANCIA_LARGA = 10;

    @Override
    public double calcular(double pesoKg, double distanciaKm) {
        double costo = TARIFA.costoBase(pesoKg, distanciaKm);
        if (distanciaKm > DISTANCIA_LARGA_KM) {
            costo += RECARGO_DISTANCIA_LARGA;
        }
        return costo;
    }
}
