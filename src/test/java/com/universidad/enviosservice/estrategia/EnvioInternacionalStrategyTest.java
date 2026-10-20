package com.universidad.enviosservice.estrategia;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvioInternacionalStrategyTest {

    private final EnvioInternacionalStrategy estrategia = new EnvioInternacionalStrategy();

    @ParameterizedTest(name = "{0} kg, {1} km -> {2}")
    @CsvSource({
            "3.0,  100.0, 230.0",   // pesoKg <= 5: 180 + 50
            "8.0,  100.0, 239.0",   // 5 < pesoKg <= 20: 230 + 3 x 3,0
            "25.0, 100.0, 297.5"    // pesoKg > 20: 230 + 15 x 3,0 + 5 x 4,5
    })
    void calculaCostoPorTramoDePeso(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }

    @ParameterizedTest(name = "{1} km -> {2}")
    @CsvSource({
            "3.0, 5000.0, 9050.0",  // limite: 5000 km no tiene recargo
            "3.0, 6000.0, 10950.0"  // mas de 5000 km: recargo de 100
    })
    void aplicaRecargoSoloSobre5000Km(double pesoKg, double distanciaKm, double esperado) {
        assertEquals(esperado, estrategia.calcular(pesoKg, distanciaKm), 0.01);
    }
}
