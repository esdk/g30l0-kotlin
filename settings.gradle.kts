//includeBuild("c:\\customizing\\esdk\\abas-essentials-sdk")

includeBuild("c:\\customizing\\esdk\\abas-essentials-sdk") {
	dependencySubstitution {
		substitute(module("de.abas.esdk:gradlePlugin")).with(project(":gradlePlugin"))
	}
}
