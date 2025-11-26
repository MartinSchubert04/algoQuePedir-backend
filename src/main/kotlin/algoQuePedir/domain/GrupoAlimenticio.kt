package algoQuePedir.domain

enum class GrupoAlimenticio {
    CEREALES_TUBERCULOS, AZUCARES_DULCES, LACTEOS, FRUTAS_VERDURAS, GRASAS_ACEITES, PROTEINAS;

    companion object {
        private val jsonMappings = mapOf(
            "CEREALES_Y_TUBERCULOS" to CEREALES_TUBERCULOS,
            "FRUTAS_Y_VERDURAS" to FRUTAS_VERDURAS,
            "AZUCARES_Y_DULCES" to AZUCARES_DULCES
        )

        fun fromJsonValue(value: String): GrupoAlimenticio {
            return jsonMappings[value.uppercase()]
                ?: valueOf(value)
        }
    }
}