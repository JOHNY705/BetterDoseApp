package hr.algebra.betterdose.api.druginteraction

import com.google.gson.annotations.SerializedName

data class DrugInteraction(
    @SerializedName("fullInteractionTypeGroup") val interactions: List<InteractionTypeGroup>?
)

data class InteractionTypeGroup(
    @SerializedName("fullInteractionType") val interactionType: List<InteractionType>?
)

data class InteractionType(
    @SerializedName("interactionPair") val interactionPair: List<InteractionPair>
)

data class InteractionPair(
    @SerializedName("interactionConcept") val interactionConcept: List<InteractionConcept>,
    @SerializedName("description") val description: String
)

data class InteractionConcept(
    @SerializedName("minConceptItem") val conceptItem: ConceptItem
)

data class ConceptItem(
    val name: String
)
