package com.example.starships.data

import com.google.gson.annotations.SerializedName

data class Starship(
    val name: String,
    val model: String,
    val manufacturer: String,

    @SerializedName("starship_class")
    val starshipClass: String,

    @SerializedName("cost_in_credits")
    val costInCredits: String,

    val length: String,
    val crew: String,
    val passengers: String,

    @SerializedName("max_atmosphering_speed")
    val maxAtmospheringSpeed: String,

    @SerializedName("hyperdrive_rating")
    val hyperdriveRating: String,

    @SerializedName("MGLT")
    val mglt: String,

    @SerializedName("cargo_capacity")
    val cargoCapacity: String,

    val consumables: String,
    val url: String
) {
    val id: String
        get() = url.trimEnd('/').substringAfterLast('/')
}

data class StarshipsResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Starship>
)