import de.abas.esdk.gradle.EsdkConfig
import de.abas.esdk.gradle.dsl.app
import de.abas.esdk.gradle.dsl.infosystems
import de.abas.esdk.gradle.dsl.screens

plugins {
	kotlin("jvm") version "1.3.72"
	id("com.github.kt3k.coveralls") version "2.8.2"
//	id("de.abas.esdk") version "0.13.3"
	id("de.abas.esdk") version "0.13.4-SNAPSHOT"
}

val NEXUS_HOST: String by project
val NEXUS_PORT: String by project
val NEXUS_NAME: String by project
val NEXUS_PASSWORD: String by project
val NEXUS_VERSION: String by project

val ABAS_HOMEDIR: String by project
val ABAS_CLIENTDIR: String by project
val ABAS_CLIENTID: String by project

val EDP_HOST: String by project
val EDP_PORT: String by project
val EDP_USER: String by project
val EDP_PASSWORD: String by project

val SSH_HOST: String by project
val SSH_PORT: String by project
val SSH_USER: String by project
val SSH_PASSWORD: String by project
val SSH_KEY: String by project

tasks {
	withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).configureEach {
		kotlinOptions.jvmTarget = "1.8"
	}
}

repositories {
	maven { url = uri("http://$NEXUS_HOST:$NEXUS_PORT/nexus/content/repositories/$NEXUS_NAME-SNAPSHOT") }
	maven { url = uri("http://$NEXUS_HOST:$NEXUS_PORT/nexus/content/repositories/$NEXUS_NAME") }
	maven { url = uri("https://registry.abas.sh/artifactory/abas.maven-public/") }
}

esdk {
	app {
		name = "g30l0"
		vendorId = "ag"
		appId = "g30l0"
		shared = false
		infosystems("IS.OW1.G30L0")
		tables = listOf("Kunde")
		screens()
		data()
		enums()
		namedTypes()
		languages = "DA"
		essentialsVersions("2017r4n00-2017r4n17")
//		screens("Customer:Customer" to listOf("A", "D"), "TestDb:TestStructure" to listOf("A"))
	}
	abas {
		homeDir = ABAS_HOMEDIR
		clientDir = ABAS_CLIENTDIR
		clientId = ABAS_CLIENTID
		edpHost = EDP_HOST
		edpPort = EDP_PORT.toInt()
		edpUser = EDP_USER
		edpPassword = EDP_PASSWORD
	}
	nexus {
		nexusHost = NEXUS_HOST
		nexusPort = NEXUS_PORT.toInt()
		nexusRepoName = NEXUS_NAME
		nexusPassword = NEXUS_PASSWORD
		nexusVersion = NEXUS_VERSION
	}
	ssh {
		host = SSH_HOST
		port = SSH_PORT.toInt()
		user = SSH_USER
		password = SSH_PASSWORD
		key = SSH_KEY
	}
	installType = "SSH"
}

//val esdk: EsdkConfig = extensions["esdk"] as EsdkConfig
//
//esdk.apply {
//	app.apply {
//		name = "g30l0"
//		vendorId = "ag"
//		appId = "g30l0"
//		shared = false
//		infosystems = listOf("IS.OW1.G30L0")
//		tables = listOf("Kunde")
//		screens = mapOf()
//		data = listOf()
//		enums = listOf()
//		namedTypes = listOf()
//		languages = "DA"
//		essentialsVersions = listOf("2017r4n00-2017r4n17")
//	}
//
//	abas.apply {
//		homeDir = ABAS_HOMEDIR
//		clientDir = ABAS_CLIENTDIR
//		clientId = ABAS_CLIENTID
//		edpHost = EDP_HOST
//		edpPort = EDP_PORT.toInt()
//		edpUser = EDP_USER
//		edpPassword = EDP_PASSWORD
//	}
//
//	nexus.apply {
//		nexusHost = NEXUS_HOST
//		nexusPort = NEXUS_PORT.toInt()
//		nexusRepoName = NEXUS_NAME
//		nexusPassword = NEXUS_PASSWORD
//		nexusVersion = NEXUS_VERSION
//	}
//
//	ssh.apply {
//		host = SSH_HOST
//		port = SSH_PORT.toInt()
//		user = SSH_USER
//		password = SSH_PASSWORD
//		key = SSH_KEY
//	}
//	installType = "SSH"
//}

group = "de.abas.esdk.g30l0"

val provided by configurations
val integTestImplementation by configurations

dependencies {
	provided("de.abas.homedir:log4j:1.0.0")
	provided("de.abas.homedir:abas-db-base:1.0.0")
	provided("de.abas.homedir:jedp:1.0.0")
	provided("de.abas.homedir:abas-jfop-runtime-api:1.0.0")
	provided("de.abas.homedir:abas-erp-common:1.0.0")
	provided("de.abas.homedir:abas-enums:1.0.0")

	implementation("de.abas.homedir:abas-axi2:1.0.0")
	implementation("de.abas.homedir:abas-axi:1.0.0")
	implementation("de.abas.homedir:abas-db-internal:1.0.0")
	implementation("de.abas.clientdir:abas-db:1.0.0-SNAPSHOT")
	implementation("de.abas.clientdir:abas-db-infosys:1.0.0-SNAPSHOT")
	implementation("fr.dudie:nominatim-api:3.3")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.72")

	runtimeOnly("de.abas.homedir:commons-collections:1.0.0")
	runtimeOnly("de.abas.homedir:abas-jfop-base:1.0.0")
	runtimeOnly("de.abas.homedir:jcl-over-slf4j:1.0.0")
	runtimeOnly("de.abas.homedir:slf4j-api:1.0.0")

	testImplementation("junit:junit:4.12")
	testImplementation("org.hamcrest:hamcrest-all:1.3")
	testImplementation("de.abas.esdk.test.util:esdk-test-utils:0.0.2")
	testImplementation("org.mockito:mockito-all:1.10.19")
	testImplementation("org.powermock:powermock-module-junit4:1.6.2")
	testImplementation("org.powermock:powermock-api-mockito:1.6.2")

	integTestImplementation("de.abas.homedir:abas-db-util:1.0.0")
}
