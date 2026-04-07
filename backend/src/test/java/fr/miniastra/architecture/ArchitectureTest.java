package fr.miniastra.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Tests ArchUnit garantissant le respect de l'architecture DDD en couches.
 *
 * <p>Ces règles sont la barrière automatique contre la dette technique architecturale.
 * Elles s'exécutent à chaque build et échouent dès qu'une dépendance interdite apparaît.
 */
@DisplayName("Architecture DDD — règles de dépendances entre couches")
class ArchitectureTest {

    private static final String BASE_PACKAGE = "fr.miniastra";

    private static JavaClasses importedClasses;

    @BeforeAll
    static void importClasses() {
        importedClasses = new ClassFileImporter()
                // On exclut les tests de l'analyse pour éviter les faux positifs
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE_PACKAGE);
    }

    /**
     * Règle 1 : le domaine est totalement indépendant des couches externes.
     *
     * <p>Le domaine doit pouvoir être testé sans Spring, sans JPA, sans rien d'externe.
     * Toute violation indique que de la logique métier fuit vers une couche technique.
     */
    @Test
    @DisplayName("Règle 1 — domain n'importe rien de infrastructure, application, api")
    void domainShouldNotDependOnOtherLayers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE_PACKAGE + ".domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        BASE_PACKAGE + ".infrastructure..",
                        BASE_PACKAGE + ".application..",
                        BASE_PACKAGE + ".api.."
                );

        rule.check(importedClasses);
    }

    /**
     * Règle 2 : la couche applicative n'accède pas directement à l'infrastructure ni à l'API.
     *
     * <p>Les services applicatifs passent par les ports (interfaces du domaine),
     * jamais par les adaptateurs directement.
     */
    @Test
    @DisplayName("Règle 2 — application n'importe rien de infrastructure, api")
    void applicationShouldNotDependOnInfrastructureOrApi() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE_PACKAGE + ".application..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        BASE_PACKAGE + ".infrastructure..",
                        BASE_PACKAGE + ".api.."
                );

        rule.check(importedClasses);
    }

    /**
     * Règle 3 : la couche API ne touche pas directement l'infrastructure.
     *
     * <p>Les contrôleurs appellent les services applicatifs, qui eux-mêmes passent
     * par les ports domaine. Accéder directement à un repository JPA depuis un contrôleur
     * court-circuite l'architecture et la rend impossible à tester correctement.
     */
    @Test
    @DisplayName("Règle 3 — api n'importe rien de infrastructure directement")
    void apiShouldNotDependOnInfrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage(BASE_PACKAGE + ".api..")
                .should().dependOnClassesThat().resideInAPackage(
                        BASE_PACKAGE + ".infrastructure.."
                );

        rule.check(importedClasses);
    }

    /**
     * Règle 4 : {@code @Entity} est exclusivement dans {@code infrastructure.persistence.entity}.
     *
     * <p>Annoter un modèle domaine avec {@code @Entity} est la pire forme de couplage
     * dans une architecture DDD : le domaine devient dépendant de JPA,
     * et les migrations Flyway deviennent risquées.
     */
    @Test
    @DisplayName("Règle 4 — @Entity uniquement dans infrastructure.persistence.entity")
    void entitiesShouldOnlyResideInPersistencePackage() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(jakarta.persistence.Entity.class)
                .should().resideInAPackage(BASE_PACKAGE + ".infrastructure.persistence.entity..");

        rule.check(importedClasses);
    }

    /**
     * Règle 5 : {@code @RestController} est exclusivement dans {@code api.controller}.
     *
     * <p>Un service ou repository annoté {@code @RestController} est un antipattern
     * qui mélange les responsabilités et empêche une évolution propre de l'API.
     */
    @Test
    @DisplayName("Règle 5 — @RestController uniquement dans api.controller")
    void controllersShouldOnlyResideInApiControllerPackage() {
        ArchRule rule = classes()
                .that().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
                .should().resideInAPackage(BASE_PACKAGE + ".api.controller..");

        rule.check(importedClasses);
    }
}
