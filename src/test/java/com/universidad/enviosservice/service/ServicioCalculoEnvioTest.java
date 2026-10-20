package com.universidad.enviosservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServicioCalculoEnvioTest {

    private final ServicioCalculoEnvio servicio = new ServicioCalculoEnvio();

    // --- Suite de partida (Parte 1) ---

    @Test
    void calculaEnvioEstandarPesoBajo() {
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "REGULAR", false);
        assertEquals(50.0, costo, 0.01);
    }

    @Test
    void aplicaDescuentoClientePremium() {
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "PREMIUM", false);
        assertEquals(45.0, costo, 0.01);
    }

    // --- Pruebas agregadas (Parte 2) ---

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "EXPRESS,       156.0",
            "OVERNIGHT,     220.0",
            "INTERNACIONAL, 230.0"
    })
    void delegaEnLaEstrategiaDeCadaMetodoDeEnvio(String metodoEnvio, double esperado) {
        double costo = servicio.calcularCosto(metodoEnvio, 3.0, 100.0, "REGULAR", false);
        assertEquals(esperado, costo, 0.01);
    }

    @Test
    void aplicaRecargoDel15PorCientoAPaquetesFragiles() {
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "REGULAR", true);
        assertEquals(57.5, costo, 0.01);
    }

    @Test
    void aplicaDescuentoClienteCorporativo() {
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "CORPORATIVO", false);
        assertEquals(42.5, costo, 0.01);
    }

    @Test
    void aplicaElDescuentoDespuesDelRecargoPorFragilidad() {
        // 50 x 1,15 = 57,5 y luego x 0,9 = 51,75
        double costo = servicio.calcularCosto("ESTANDAR", 3.0, 100.0, "PREMIUM", true);
        assertEquals(51.75, costo, 0.01);
    }

    @Test
    void rechazaMetodoDeEnvioNoSoportado() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.calcularCosto("DRON", 3.0, 100.0, "REGULAR", false));
        assertEquals("Metodo de envio no soportado: DRON", error.getMessage());
    }
}
