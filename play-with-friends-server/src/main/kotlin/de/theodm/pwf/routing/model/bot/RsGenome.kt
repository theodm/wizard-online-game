package de.theodm.pwf.routing.model.bot

data class RsTestFFNRequest(
    val genome: RsGenome,
    val inputs: List<Double>
){

}

data class RsGenome(
    val nodes: List<RsNode>,
    val connections: List<RsConnection>,
    val inputNodeKeys: List<Int>,
    val outputNodeKeys: List<Int>
) {
}

data class RsNode(
    val response: Double,
    val bias: Double,
    val key: Int,
    val activation: String,
    val aggregation: String
)

data class RsConnection(
    val keyInput: Int,
    val keyOutput: Int,
    val weight: Double,
    val enabled: Boolean
)