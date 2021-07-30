package blue.starry.releasechime

import io.ktor.client.HttpClient
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.http.userAgent
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection

val AppHttpClient by lazy {
    HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }

        defaultRequest {
            userAgent("releasechime (+https://github.com/SlashNephy/releasechime)")
        }
    }
}

val AppDatabase by lazy {
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    Files.createDirectories(Paths.get("data"))

    Database.connect("jdbc:sqlite:data/database.db", "org.sqlite.JDBC")
}
