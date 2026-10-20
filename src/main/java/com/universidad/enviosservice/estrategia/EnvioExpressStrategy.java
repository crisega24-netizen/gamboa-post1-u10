package com.universidad.enviosservice.estrategia;

/** EXPRESS: 1,2 por km, recargo de 25 sobre 500 km y factor de prioridad de 1,3. */
public class EnvioExpressStrategy implements EstrategiaEnvio {

    private static final TarifaPorPeso TARIFA = new TarifaPorPeso(1.2, 0, 1.5, 2.0);
    private static final double DISTANCIA_LARGA_KM = 500;
    private static final double RECARGO_DISTANCIA_LARGA = 25;
    private static final double FACTOR_PRIORIDAD = 1.3;

    @Override
    public double calcular(double pesoKg, double distanciaKm) {
        double costo = TARIFA.costoBase(pesoKg, distanciaKm);
        if (distanciaKm > DISTANCIA_LARGA_KM) {
            costo += RECARGO_DISTANCIA_LARGA;
        }
        return costo * FACTOR_PRIORIDAD;
    }
}
