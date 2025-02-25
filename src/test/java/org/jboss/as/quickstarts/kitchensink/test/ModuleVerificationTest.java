package org.jboss.as.quickstarts.kitchensink.test;

import org.jboss.as.quickstarts.kitchensink.SpringBootApp;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModuleVerificationTest {

    @Test
    void createApplicationModuleModel() {
        ApplicationModules modules = ApplicationModules.of(SpringBootApp.class);
        modules.verify();
        new Documenter(modules)
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml();
        modules.forEach(System.out::println);
    }
}
