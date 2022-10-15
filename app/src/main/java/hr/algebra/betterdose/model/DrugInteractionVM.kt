package hr.algebra.betterdose.model

data class DrugInteractionVM(
    val firstDrugName: String,
    val secondDrugName: String,
    val interactionDescription: String
)
