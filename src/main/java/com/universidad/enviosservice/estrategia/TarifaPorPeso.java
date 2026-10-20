package com.universidad.enviosservice.estrategia;

/**
 * Tarifa por distancia y tramos de peso compartida por los cuatro metodos de envio.
 * Todos usan los mismos tramos (hasta 5 kg, de 5 a 20 kg y mas de 20 kg) y solo
 * difieren en los valores, por eso la formula vive aqui una sola vez.
 *
 * @param tarifaPorKm   costo por kilometro recorrido
 * @param cargoFijo     cargo fijo del metodo de envio
 * @param tarifaKgMedio costo por kg en el tramo de 5 a 20 kg
 * @param tarifaKgAlto  costo por kg por encima de 20 kg
 */
public record TarifaPorPeso(double tarifaPorKm, double cargoFijo,
                            double tarifaKgMedio, double tarifaKgAlto) {

    private static final double PESO_TRAMO_BAJO_KG = 5;
    private static final double PESO_TRAMO_MEDIO_KG = 20;

    public double costoBase(double pesoKg, double distanciaKm) {
        double costo = distanciaKm * tarifaPorKm + cargoFijo;
        if (pesoKg <= PESO_TRAMO_BAJO_KG) {
            return costo;
        }
        if (pesoKg <= PESO_TRAMO_MEDIO_KG) {
            return costo + (pesoKg - PESO_TRAMO_BAJO_KG) * tarifaKgMedio;
        }
        return costo + (PESO_TRAMO_MEDIO_KG - PESO_TRAMO_BAJO_KG) * tarifaKgMedio
                + (pesoKg - PESO_TRAMO_MEDIO_KG) * tarifaKgAlto;
    }
}
