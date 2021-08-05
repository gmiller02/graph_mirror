package graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import support.graph.CS16Edge;
import support.graph.CS16Vertex;
import support.graph.Graph;
import support.graph.PageRank;

/**
 * In this class you will implement one of many different versions
 * of the PageRank algorithm. This algorithm will only work on
 * directed graphs. Keep in mind that there are many different ways
 * to handle sinks.
 *
 * Make sure you review the help slides and handout for details on
 * the PageRank algorithm.
 *
 */
public class MyPageRank<V> implements PageRank<V> {
	private Graph<V> _g;
	private List<CS16Vertex<V>> _vertices;
	private Map<CS16Vertex<V>, Double> _vertsToRanks;
	private static final double _dampingFactor = 0.85;
	private static final int _maxIterations = 100;
	private static final double _error = 0.01;

	/**
	 * TODO: Feel free to add in anything else necessary to store the information
	 * needed to calculate the rank. Maybe make something to keep track of sinks,
	 * your ranks, and your outgoing edges?
	 */

	/**
	 * The main method that does the calculations! You'll want to call the methods
	 * that initialize your variables here. You'll also want to decide on a
	 * type of loop - for loop, do while, or while loop - for your calculations.
	 *
	 * @return A Map of every Vertex to its corresponding rank
	 *
	 */
	@Override
	public Map<CS16Vertex<V>, Double> calcPageRank(Graph<V> g) {
		_g = g;
		_vertsToRanks = new HashMap<>();
		MyDecorator<CS16Vertex<V>, Double> rank = new MyDecorator<>();
		MyDecorator<CS16Vertex<V>,Double> prev = new MyDecorator<>();

		Iterator<CS16Vertex<V>> vIterator = g.vertices();

		while (vIterator.hasNext()) {
			CS16Vertex<V> v = vIterator.next();
			rank.setDecoration(v, (1.0 / g.getNumVertices()));
		}

		for (int i = 0; i <= _maxIterations; i++) {
			Iterator<CS16Vertex<V>> vIterator2 = g.vertices();

			while (vIterator2.hasNext()) {
				CS16Vertex<V> v2 = vIterator2.next();
				prev.setDecoration(v2, rank.getDecoration(v2));
			}

			Iterator<CS16Vertex<V>> vIterator3 = g.vertices();
			while(vIterator3.hasNext()) {
				CS16Vertex<V> v3 = vIterator3.next();
				rank.setDecoration(v3, 0.0);
				Iterator<CS16Edge<V>> incoming = g.incomingEdges(v3);

				while (incoming.hasNext()) {
					CS16Edge<V> edge = incoming.next();
					CS16Vertex<V> vertex = g.opposite(v3, edge);

					double rankD = rank.getDecoration(v3);
					double prevD = prev.getDecoration(vertex);

					rank.setDecoration(v3, rankD + (prevD / g.numOutgoingEdges(vertex)));
				}
				i++;
			}
		}

	Iterator<CS16Vertex<V>> vIterator4 = g.vertices();
	while(vIterator4.hasNext()){
		CS16Vertex<V> v3 = vIterator4.next();
		_vertsToRanks.put(v3, rank.getDecoration(v3));
	}
		return _vertsToRanks;
	}

	/**
	 * Method used to account for sink pages (those with no outgoing
	 * edges). There are multiple ways you can implement this, check
	 * the lecture and help slides!
	 */
	private void handleSinks() {
		// TODO: Fill this in
	}

	// Feel free to add helper methods below.


}
