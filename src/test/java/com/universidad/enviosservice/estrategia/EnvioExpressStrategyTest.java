package com.universidad.enviosservice.estrategia;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvioExpressStrategyTest {

    private final EnvioExpressStrategy estrategia = new EnvioExpressStrategy();

    @ParameterizedTest(name = "{0} kg, {1} km -> {2}")
    @CsvSource({
            "3.0,  100.0, 156.0",   // pesoKg <= 5: 120 x 1,3
            "8.0,  100.0, 161.85",  // 5 < pesoKg <= 20: (120 + 4,5) x 1,3
            "25.0, 100.0, 198.25"   // pesoKg > 20: (120 + 22,5 + 10) x 1,3
    })
    void calculaCostoPorTramoDePesoConFactorDePrioridad(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }

    @ParameterizedTest(name = "{1} km -> {2}")
    @CsvSource({
            "3.0, 500.0, 780.0",    // limite: 600 x 1,3, sin recargo
            "3.0, 600.0, 968.5"     // (720 + 25) x 1,3: el recargo tambien se multiplica
    })
    void aplicaRecargoSobre500KmAntesDelFactor(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }
}
