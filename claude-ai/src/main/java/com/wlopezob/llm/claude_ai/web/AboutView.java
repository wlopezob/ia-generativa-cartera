package com.wlopezob.llm.claude_ai.web;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
public class AboutView extends VerticalLayout{
    public AboutView() {
        add(
                new H1("Acerca de la Aplicación"),
                new Html("""
                        <div>
                            <p>
                                Esta aplicación es una integración entre <strong>Spring Boot</strong>, <strong>Vaadin</strong> y <strong>Claude AI</strong> para crear un asistente conversacional que puede integrarse fácilmente a sistemas empresariales modernos.
                            </p>
                            <p>
                                Fue desarrollada por <strong>Geovanny Mendoza</strong>, ingeniero de software con más de 12 años de experiencia en el desarrollo de soluciones backend usando Java y Kotlin. Es líder del grupo de usuarios de Java en Barranquilla y conferencista en eventos como JConf y DevFest.
                            </p>
                            <p>
                                La arquitectura combina una interfaz responsiva con Vaadin y un backend flexible que utiliza modelos de lenguaje de Anthropic Claude a través de Spring AI.
                            </p>
                            <p>
                                Puedes conocer más sobre mi trabajo en <a href=\"https://geovannycode.com\" target=\"_blank\">https://geovannycode.com</a>
                            </p>
                        </div>
                        """.stripIndent())
        );
    }
}
