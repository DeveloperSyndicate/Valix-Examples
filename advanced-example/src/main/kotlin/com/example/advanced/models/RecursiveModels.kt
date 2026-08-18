package com.example.advanced.models

import io.valix.annotations.NotBlank
import io.valix.annotations.NotEmpty
import io.valix.annotations.Valid

data class Cell(
    @NotBlank
    val value: String
)

data class GameBoard(
    @NotEmpty
    @Valid
    val grid: List<List<Cell>>
)

data class MapConfig(
    @NotEmpty
    @Valid
    val meta: Map<String, Cell>
)
