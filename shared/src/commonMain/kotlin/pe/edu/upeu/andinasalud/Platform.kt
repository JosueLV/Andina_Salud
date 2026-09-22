package pe.edu.upeu.andinasalud

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform