package geo;

public interface NodeListener {

	boolean addFirstNodeToGraph(GeoGraph graph, GeoObj objectToAdd);

	boolean addNodeToGraph(GeoGraph graph, GeoObj objectToAdd);

	boolean addLastNodeToGraph(GeoGraph graph, GeoObj objectToAdd);
}