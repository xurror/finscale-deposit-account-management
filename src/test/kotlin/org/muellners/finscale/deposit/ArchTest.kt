package org.muellners.finscale.deposit

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("org.muellners.finscale.deposit")

        noClasses()
            .that()
                .resideInAnyPackage("org.muellners.finscale.deposit.service..")
            .or()
                .resideInAnyPackage("org.muellners.finscale.deposit.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..org.muellners.finscale.deposit.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses)
    }
}
