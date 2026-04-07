package fr.miniastra;

import fr.miniastra.api.controller.CatalogController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Smoke test vérifiant que la couche Web (controllers, exception handler) se charge
 * sans erreur et sans nécessiter de datasource.
 *
 * <p>Utilise {@code @WebMvcTest} pour charger uniquement la couche MVC.
 * {@link CatalogController} n'a aucune dépendance sur la persistance,
 * ce qui le rend idéal pour ce test.
 */
@WebMvcTest(CatalogController.class)
@DisplayName("Smoke test — démarrage du contexte Web")
class ApplicationContextTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Le contexte Web démarre et le catalogue est accessible")
    void catalogEndpointsAreAvailable() throws Exception {
        mockMvc.perform(get("/api/catalog/passenger-models")).andExpect(status().isOk());
        mockMvc.perform(get("/api/catalog/freight-models")).andExpect(status().isOk());
    }
}
