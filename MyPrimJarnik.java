package graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import net.datastructures.Entry;
import net.datastructures.Vertex;
import support.graph.CS16AdaptableHeapPriorityQueue;
import support.graph.CS16Edge;
import support.graph.CS16GraphVisualizer;
import support.graph.CS16Vertex;
import support.graph.Graph;
import support.graph.MinSpanForest;

/**
 * In this class you will implement a slightly modified version
 * of the Prim-Jarnik algorithm for generating Minimum Spanning trees.
 * The original version of this algorithm will only generate the 
 * minimum spanning tree of the connected vertices in a graph, given
 * a starting vertex. Like Kruskal's, this algorithm can be modified to 
 * produce a minimum spanning forest with very little effort.
 *
 * See the handout for details on Prim-Jarnik's algorithm.
 * Like Kruskal's algorithm this algorithm makes extensive use of 
 * the decorator pattern, so make sure you know it.
 */
public class MyPrimJarnik<V> implements MinSpanForest<V> {

    /** 
     * This method implements Prim-Jarnik's algorithm and extends 
     * it slightly to account for disconnected graphs. You must return 
     * the collection of edges of the Minimum Spanning Forest (MSF) for 
     * the given graph, g.
     * 
     * This algorithm must run in O((|E| + |V|)log(|V|)) time
     * @param g Your graph
     * @param visualizer Only used if you implement the optional animation.
     * @return returns a data structure that contains the edges of your MSF that implements java.util.Collection
     */
    @Override
    public Collection<CS16Edge<V>> genMinSpanForest(Graph<V> g, CS16GraphVisualizer<V> visualizer) {
        CS16AdaptableHeapPriorityQueue<Integer, CS16Vertex<V>> myHeap = new CS16AdaptableHeapPriorityQueue<>();
        Collection<CS16Edge<V>> MST = new Vector<>();

        MyDecorator<CS16Vertex<V>,Integer> dist = new MyDecorator<>();
        MyDecorator<CS16Vertex<V>,CS16Vertex<V>> prev = new MyDecorator<>();
        MyDecorator<CS16Vertex<V>,Entry<Integer, CS16Vertex<V>>> entry = new MyDecorator<>();

        Iterator<CS16Vertex<V>> vIterator = g.vertices();

        while(vIterator.hasNext()){
            CS16Vertex<V> next = vIterator.next();
            dist.setDecoration(next, Integer.MAX_VALUE);
            prev.setDecoration(next, null);
            Entry<Integer, CS16Vertex<V>> myEntry = myHeap.insert(0, next);
            entry.setDecoration(next, myEntry);
        }

        while(!myHeap.isEmpty()){
            CS16Vertex<V> minVertex = myHeap.removeMin().getValue();
            entry.removeDecoration((minVertex));
            if(prev.getDecoration(minVertex) != null){
                MST.add(g.connectingEdge(minVertex,prev.getDecoration(minVertex)));
            }
            Iterator<CS16Edge<V>> eIterator = g.incomingEdges(minVertex);
            while(eIterator.hasNext()){
                CS16Edge<V> myEdge = eIterator.next();
                CS16Vertex<V> myVertex = g.opposite(minVertex, myEdge);
                if(dist.getDecoration(myVertex) > myEdge.element()){
                    dist.setDecoration(myVertex, myEdge.element());
                    prev.setDecoration(myVertex, minVertex);
                    if (entry.getDecoration(myVertex) != null){
                        myHeap.replaceKey(entry.getDecoration(myVertex), dist.getDecoration(myVertex));
                    }
                }
            }

        }
        return MST;
      }
    }
