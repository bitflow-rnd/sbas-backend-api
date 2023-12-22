package org.sbas

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition
import org.eclipse.microprofile.openapi.annotations.info.Contact
import org.eclipse.microprofile.openapi.annotations.info.Info
import org.eclipse.microprofile.openapi.annotations.info.License
import jakarta.ws.rs.ApplicationPath
import jakarta.ws.rs.core.Application

// Testable at http://localhost:8080/q/swagger-ui
@OpenAPIDefinition(
    info = Info(
        title = "Example API",
        version = "1.0.1",
        contact = Contact(
            name = "Example API Support",
            url = "http://exampleurl.com/contact",
            email = "techsupport@example.com"),
        license = License(
            name = "Apache 2.0",
            url = "http://www.apache.org/licenses/LICENSE-2.0.html"))
)
@ApplicationPath("/api")
class SbasApplication : Application()
