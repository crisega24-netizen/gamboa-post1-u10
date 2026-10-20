# Envios Service — Análisis de Métricas de Calidad


Proyecto Maven plano (sin Spring Boot) que calcula el costo de un envío según el método de transporte, el peso, la distancia, la fragilidad y el tipo de cliente. La clase `ServicioCalculoEnvio` concentra deliberadamente toda la lógica en un solo método, `calcularCosto()`, y la suite de partida solo ejercita dos de sus caminos. Esta primera parte diagnostica el problema con métricas reales de **SonarQube** y **JaCoCo**.

```
gamboa-post1-u10/
├── pom.xml                       ← Java 21, JUnit 5, JaCoCo (prepare-agent + report)
├── sonar-project.properties      ← configuración comentada del análisis
├── docs/                         ← capturas de SonarQube y JaCoCo
└── src/
    ├── main/java/com/universidad/enviosservice/service/
    │   └── ServicioCalculoEnvio.java
    └── test/java/com/universidad/enviosservice/service/
        └── ServicioCalculoEnvioTest.java
```

## Cómo ejecutar el análisis

```bash
# 1. SonarQube local (Docker)
docker run -d --name sonarqube -p 9000:9000 -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true sonarqube:community

# 2. Compilar, probar, generar jacoco.xml y enviar el análisis
./mvnw clean verify sonar:sonar -Dsonar.token=<TOKEN_DEL_PROYECTO>

# 3. Reporte HTML de cobertura
open target/site/jacoco/index.html
```

> El token no se versiona: se pasa por línea de comandos. SonarScanner for Maven no lee `sonar-project.properties`, por eso sus propiedades están replicadas en el `<properties>` del `pom.xml`.

## Entorno de análisis

**Decisión de diseño 1 — SonarQube Community Build local con Docker** (servidor 26.10, imagen `sonarqube:community`), en lugar de SonarCloud:

- **Control total de la configuración.** La Parte 2 exige crear un Quality Profile y un Quality Gate propios («Estándar Laboratorio»). El plan gratuito de SonarCloud no permite Quality Profiles ni Quality Gates personalizados, mientras que la instancia local los permite sin restricciones.
- **Sin dependencia de conectividad ni de límites del plan gratuito.** El análisis corre contra `localhost:9000` y el código no queda expuesto en un dashboard público.
- **Costo asumido:** consume recursos de la máquina (se asignaron 4 GB de memoria a Docker Desktop) y el contenedor debe mantenerse en ejecución durante todo el laboratorio. La base de datos embebida es suficiente para un laboratorio, aunque no para producción.

## Exclusiones del análisis

**Decisión de diseño 2 — No se excluye ninguna clase** (`sonar.exclusions` queda sin valor). El proyecto no contiene clase de arranque (`Main`), DTOs, configuración ni código generado: todo `src/main/java` es lógica de negocio. Excluir algo solo serviría para mejorar artificialmente el porcentaje de cobertura y ocultaría justamente el código que se quiere medir. Las pruebas no necesitan exclusión porque `sonar.tests=src/test/java` ya las separa del cálculo de cobertura.

## Estado inicial del análisis

| Métrica | Valor real observado | Fuente |
|---------|----------------------|--------|
| Complejidad ciclomática de `calcularCosto()` | **20** | JaCoCo (Cxty por método) · SonarQube: 20 en el proyecto |
| Complejidad cognitiva de `ServicioCalculoEnvio` | **32** | SonarQube · Measures → Complexity |
| Cobertura de líneas de `ServicioCalculoEnvio` | **23,8 %** (10 de 42 líneas) | SonarQube · JaCoCo |
| Cobertura de ramas de `ServicioCalculoEnvio` | **18,4 %** (7 de 38 condiciones; 31 sin cubrir) | SonarQube · JaCoCo |
| Coverage global (líneas + condiciones) | **21,3 %** | SonarQube |
| Code Smells reportados | **0** (Maintainability A) | SonarQube · perfil `Sonar way core` |
| Bugs / Vulnerabilidades / Duplicación | 0 / 0 / 0,0 % | SonarQube |
| Pruebas unitarias | 2 | SonarQube · Surefire |

## Interpretación

**Qué método concentra el problema.** `calcularCosto(String, double, double, String, boolean)` tiene complejidad ciclomática 20: 19 puntos de decisión (4 comparaciones de `metodoEnvio`, 8 tramos de peso, 3 recargos o restricciones por distancia, la fragilidad y 2 tipos de cliente) más 1. Según la Tabla 3.2 de la guía es una CC «Alta» (11–20). El otro método de la clase, el constructor implícito, tiene CC 1; por eso JaCoCo reporta 21 para la clase. La vista *Code* de SonarQube agrega la complejidad por archivo, así que el valor por método se confirmó en la tabla de la clase del reporte de JaCoCo.

**Qué ramas quedaron sin cubrir.** Las dos pruebas recorren el mismo camino: `ESTANDAR`, peso ≤ 5 kg, distancia ≤ 500 km, no frágil, cliente `REGULAR` o `PREMIUM`. Solo cubren 7 de los 38 resultados de rama. Las 31 ramas sin ejercitar, marcadas en rojo y amarillo en el reporte HTML de JaCoCo, son:

| Zona del método | Ramas sin cubrir |
|-----------------|-----------------|
| `ESTANDAR` | peso > 5 kg (tramos 5–20 kg y > 20 kg) y recargo por distancia > 500 km |
| `EXPRESS` | la rama completa: sus tres tramos de peso, el recargo > 500 km y el factor 1,3 |
| `OVERNIGHT` | la rama completa, incluida la excepción por distancia > 300 km |
| `INTERNACIONAL` | la rama completa: tres tramos de peso y recargo > 5000 km |
| Método no soportado | el `else` que lanza `IllegalArgumentException` |
| Ajustes transversales | `esFragil == true` y el descuento `CORPORATIVO` |

Ningún resultado cubre un valor límite (5 kg, 20 kg, 300 km, 500 km o 5000 km), de modo que un error de `<=` por `<` en cualquier tramo pasaría inadvertido.

**Por qué la complejidad alta y la cobertura baja coinciden en el mismo método.** La clase solo tiene un método con lógica, y su baja cobertura es consecuencia directa de su complejidad: con 20 caminos independientes, dos pruebas solo pueden recorrer una fracción mínima. La cobertura baja es el síntoma; la complejidad, la causa estructural.

**Un hallazgo del propio dashboard: rating A con CC = 20.** SonarQube reporta 0 Code Smells y Maintainability A, porque el perfil por defecto (`Sonar way core`) no tiene activa la regla de complejidad ciclomática por método (S1541) y tampoco reportó la complejidad cognitiva de 32. El dashboard luce sano mientras el método concentra todo el riesgo. Esto justifica configurar en la Parte 2 un Quality Profile propio con S1541 en 10.

**Decisión de diseño 3 — Señal principal: la complejidad ciclomática por método.**

- **Coverage** no puede ser la señal principal: es el síntoma. Un 18,4 % de ramas indica que faltan pruebas, pero no dice por qué faltan ni qué cambio de diseño las haría manejables.
- **Cognitive Complexity (32)** mide la dificultad de lectura y penaliza el anidamiento. Es útil para priorizar legibilidad, pero no se traduce en un número de caminos que probar ni tiene el umbral por método (10) que usa la guía y la regla S1541.
- **Cyclomatic Complexity (20)** cuenta los caminos independientes del método, lo que explica directamente por qué dos pruebas cubren tan poco. Además, es la métrica que la refactorización de la Parte 2 promete reducir de forma medible (al menos un 50 %) y la que el Quality Gate controlará por método.
- **El número de Code Smells (0)** queda descartado como señal porque, con el perfil por defecto, no detecta el problema.

## Capturas del análisis inicial

**Dashboard de SonarQube (Overall Code)**

![Dashboard SonarQube](docs/sonar-dashboard-inicial.png)

**Reporte JaCoCo de `ServicioCalculoEnvio` (CC y cobertura por método)**

![Reporte JaCoCo](docs/jacoco-inicial.png)

## Herramientas utilizadas

- Java 21 (compilado con JDK 25, `maven.compiler.release=21`), Apache Maven (Maven Wrapper)
- JUnit 5, JaCoCo 0.8.13
- SonarQube Community Build 26.10 en Docker, SonarScanner for Maven 5.0
- Git, GitHub
