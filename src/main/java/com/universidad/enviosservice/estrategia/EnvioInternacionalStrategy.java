package com.universidad.enviosservice.estrategia;

/** INTERNACIONAL: 1,8 por km mas cargo fijo de 50; recargo de 100 sobre 5000 km. */
public class EnvioInternacionalStrategy implements EstrategiaEnvio {

    private static final TarifaPorPeso TARIFA = new TarifaPorPeso(1.8, 50, 3.0, 4.5);
    private static final double DISTANCIA_LARGA_KM = 5000;
    private static final double RECARGO_DISTANCIA_LARGA = 100;

    @Override
    public double calcular(double pesoKg, double distanciaKm) {
        double costo = TARIFA.costoBase(pesoKg, distanciaKm);
        if (distanciaKm > DISTANCIA_LARGA_KM) {
            costo += RECARGO_DISTANCIA_LARGA;
        }
        return costo;
    }
}
