package com.universidad.enviosservice.service;

import com.universidad.enviosservice.estrategia.EnvioEstandarStrategy;
import com.universidad.enviosservice.estrategia.EnvioExpressStrategy;
import com.universidad.enviosservice.estrategia.EnvioInternacionalStrategy;
import com.universidad.enviosservice.estrategia.EnvioOvernightStrategy;
import com.universidad.enviosservice.estrategia.EstrategiaEnvio;

import java.util.Map;

/**
 * Orquestador del calculo de envio: selecciona la estrategia del metodo de envio
 * y aplica las reglas transversales a todos los metodos (fragilidad y tipo de cliente).
 */
public class ServicioCalculoEnvio {

    private static final double RECARGO_FRAGIL = 0.15;
    private static final double FACTOR_PREMIUM = 0.9;
    private static final double FACTOR_CORPORATIVO = 0.85;

    private final Map<String, EstrategiaEnvio> estrategias = Map.of(
            "ESTANDAR", new EnvioEstandarStrategy(),
            "EXPRESS", new EnvioExpressStrategy(),
            "OVERNIGHT", new EnvioOvernightStrategy(),
            "INTERNACIONAL", new EnvioInternacionalStrategy()
    );

    public double calcularCosto(String metodoEnvio, double pesoKg, double distanciaKm,
                                String tipoCliente, boolean esFragil) {
        EstrategiaEnvio estrategia = estrategias.get(metodoEnvio);
        if (estrategia == null) {
            throw new IllegalArgumentException("Metodo de envio no soportado: " + metodoEnvio);
        }

        double costo = estrategia.calcular(pesoKg, distanciaKm);

        if (esFragil) {
            costo = costo + costo * RECARGO_FRAGIL;
        }

        if (tipoCliente.equals("PREMIUM")) {
            costo = costo * FACTOR_PREMIUM;
        } else if (tipoCliente.equals("CORPORATIVO")) {
            costo = costo * FACTOR_CORPORATIVO;
        }

        return costo;
    }
}
