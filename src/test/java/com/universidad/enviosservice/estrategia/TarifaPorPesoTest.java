package com.universidad.enviosservice.estrategia;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TarifaPorPesoTest {

    // Tarifa sencilla para que cada tramo sea evidente: 1 por km, cargo fijo 10,
    // 2 por kg entre 5 y 20 kg, 3 por kg por encima de 20 kg
    private final TarifaPorPeso tarifa = new TarifaPorPeso(1.0, 10, 2.0, 3.0);

    @ParameterizedTest(name = "{0} kg -> {2}")
    @CsvSource({
            "5.0,  100.0, 110.0",   // limite superior del tramo bajo (inclusive)
            "5.5,  100.0, 111.0",   // apenas entra al tramo medio
            "20.0, 100.0, 140.0",   // limite superior del tramo medio (inclusive)
            "21.0, 100.0, 143.0"    // tramo alto: 15 kg a 2,0 + 1 kg a 3,0
    })
    void respetaLosLimitesDeLosTramosDePeso(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, tarifa.costoBase(pesoKg, distanciaKm), 0.001);
    }
}
