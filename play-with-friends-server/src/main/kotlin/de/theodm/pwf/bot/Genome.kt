//package de.theodm.pwf.bot
//
//import de.theodm.pwf.routing.model.bot.RsConnection
//import de.theodm.pwf.routing.model.bot.RsGenome
//import de.theodm.pwf.routing.model.bot.RsNode
//import kotlin.math.exp
//import kotlin.math.max
//import kotlin.math.min
//
//data class ConnectionKey(
//    val inode: Int,
//    val onode: Int
//)
//
//data class Genome(
//    val nodes: Map<Int, Node>,
//    val connections: Map<ConnectionKey, Connection>
//)
//
//fun RsNode.toNode() = Node(
//    response = this.response,
//    bias = this.bias,
//    key = this.key,
//    activation = this.activation,
//    aggregation = this.aggregation
//)
//
//fun RsConnection.toConnection() = Connection(
//    key = ConnectionKey(this.keyInput, this.keyOutput),
//    weight = this.weight,
//    enabled = this.enabled
//)
//
//fun RsGenome.toGenome(): Genome {
//    return Genome(
//        nodes = this.nodes
//            .map { it.toNode() }
//            .associateBy { it.key },
//        connections = this.connections
//            .map { it.toConnection() }
//            .associateBy { it.key }
//    )
//}
//
//data class Node(
//    val response: Double,
//    val bias: Double,
//    val key: Int,
//    val activation: String,
//    val aggregation: String
//)
//
//data class Connection(
//    val key: ConnectionKey,
//    val weight: Double,
//    val enabled: Boolean
//)
//
//fun requiredForOutput(
//    input_node_keys: Set<Int>,
//    output_node_keys: Set<Int>,
//    connections: List<ConnectionKey>
//): Set<Int> {
//    var required = output_node_keys
//    var s = output_node_keys
//
//    while (true) {
//        // Ausgehend von den Ausgangsknoten,
//        // finde die letzten Verbindungen.
//        // aber nicht solche, die wir schon gefunden haben.
//        val t =  connections
//            .filter { (a, b) -> b in s && a !in s }
//            .map { (a, _) -> a }
//
//        if (t.isEmpty())
//            break
//
//        val layerNodes = t
//            .filter { it !in input_node_keys }
//
//        if (layerNodes.isEmpty())
//            break
//
//        required = required
//            .union(layerNodes)
//
//        // Als nächstes die Verbindungen
//        // der nun gefundenen Knoten suchen
//        s = s
//            .union(t)
//    }
//
//    return required
//}
//
//fun feedForwardLayers(
//    input_node_keys: Set<Int>,
//    output_node_keys: Set<Int>,
//    connections: List<ConnectionKey>
//): List<Set<Int>> {
//    val required = requiredForOutput(
//        input_node_keys,
//        output_node_keys,
//        connections
//    )
//
//    val layers = mutableListOf<Set<Int>>()
//    var s = input_node_keys
//
//    while (true) {
//        // Ausgehend von den Eingangsknoten
//        // suchen wir die nachfolgend verbundenen
//        // Knoten. Nicht, wenn wir die Knoten bereits bearbeitet
//        // haben (sind in s).
//        val c = connections
//            .filter { (a, b) -> a in s && b !in s }
//            .map { (_, b) -> b }
//
//        val t = mutableSetOf<Int>()
//        for (n in c) {
//            if (n !in required) {
//                continue
//            }
//
//            // Keep only the used nodes whose entire input set is contained in s.
//            val allInputNodesInS = connections
//                .filter { (_, b) -> b == n }
//                .map { (a, _) -> a }
//                .all { a -> a in s }
//
//            if (!allInputNodesInS) {
//                continue
//            }
//
//            t.add(n)
//        }
//
//        if (t.isEmpty()) {
//            break
//        }
//
//        layers.add(t)
//        s = s.union(t)
//    }
//
//    return layers
//}
//
//data class NodeEval(
//    val node: Int,
//    val activationFunction: (z: Double) -> Double,
//    val aggregationFunction: (v: List<Double>) -> Double,
//    val bias: Double,
//    val response: Double,
//    val inputs: List<Pair<Int, Double>>
//)
//
//fun createFFNetwork(
//    genome: Genome,
//    input_node_keys: Set<Int>,
//    output_node_keys: Set<Int>
//): FFNetwork {
//    val enabledConnections = genome
//        .connections
//        .values
//        .filter { it.enabled }
//        .map { it.key }
//
//    val layers = feedForwardLayers(
//        input_node_keys,
//        output_node_keys,
//        enabledConnections
//    )
//
//
//    val node_evals = mutableListOf<NodeEval>()
//
//    for (layer in layers) {
//        for (node in layer) {
//            val inputs = mutableListOf<Pair<Int, Double>>()
//            val node_expr = mutableListOf<(v: List<Double>) -> Double>()
//
//            for (connKey in enabledConnections) {
//                val (inode, onode) = connKey
//                if (node == onode) {
//                    val cg = genome.connections[connKey]!!
//
//                    inputs.add(inode to cg.weight)
//
//                    node_expr.add { v -> v[inode] * cg.weight }
//                }
//            }
//
//            val ng = genome.nodes[node]!!
//
//            val aggregationFunction = { d: List<Double> -> d.sum() }
//            val activationFunction = { z: Double ->
//                val z = max(-60.0, min(60.0, 5.0 * z))
//
//                1.0 / (1.0 + exp(-z))
//            }
//
//            node_evals.add(
//                NodeEval(
//                    node = node,
//                    activationFunction = activationFunction,
//                    aggregationFunction = aggregationFunction,
//                    bias = ng.bias,
//                    response = ng.response,
//                    inputs = inputs
//            ))
//
//        }
//    }
//
//    return FFNetwork(input_node_keys, output_node_keys, node_evals)
//}
//
//data class FFNetwork(
//    val input_node_keys: Set<Int>,
//    val output_node_keys: Set<Int>,
//    val nodeEvals: List<NodeEval>
//) {
//    fun activate(
//        inputs: List<Double>
//    ): List<Double> {
//        require(inputs.size == input_node_keys.size)
//
//        val values = (input_node_keys + output_node_keys)
//            .associateWith { 0.0 }
//            .toMutableMap()
//
//        for ((k, v) in (input_node_keys zip inputs)) {
//            values[k] = v
//        }
//
//        for (nodeEval in nodeEvals) {
//            val nodeInputs = mutableListOf<Double>()
//
//            val links = nodeEval.inputs
//            for ((i, w) in links) {
//                nodeInputs.add(values[i]!! * w)
//            }
//
//            val s = nodeEval.aggregationFunction(nodeInputs)
//            values[nodeEval.node] = nodeEval.activationFunction(nodeEval.bias + nodeEval.response * s)
//        }
//
//        return output_node_keys
//            .map { values[it]!! }
//    }
//}