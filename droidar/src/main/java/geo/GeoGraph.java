package geo;

import gl.Renderable;
import gl.scenegraph.MeshComponent;

import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

import system.Container;
import system.EventManager;
import util.EfficientList;
import util.EfficientListQualified;
import util.Log;
import worldData.AbstractObj;
import worldData.Updateable;
import worldData.Visitor;
import worldData.World;

/**
 * A {@link GeoGraph} is a simple graph which holds {@link GeoObj}s and supports
 * shortest path search eg. it extends {@link AbstractObj} and can be added to a
 * virtual {@link World} directly
 * 
 * @author Spobo
 * 
 */
public class GeoGraph extends AbstractObj implements Container<GeoObj> {

	private static final boolean DEBUG1 = false;
	private static final boolean DEBUG2 = false;
	private static final String LOG_TAG = "GeoGraph";

	private EfficientListQualified<GeoObj> myNodes;
	private EfficientList<Edge> myEdges;
	private boolean isPath;
	private boolean nonDirectional = true;
	private boolean useEdges;

	/**
	 * this constructor will automatically enable edges!
	 */
	public GeoGraph() {
		useEdges = true;
	}

	public GeoGraph(boolean usesEdges) {
		this.useEdges = usesEdges;
	}

	public boolean isNonDirectional() {
		return nonDirectional;
	}

	// public GeoGraph(GeoGraph graphToClone) {
	// setUseEdges(graphToClone.isUsingItsEdges());
	// setIsPath(graphToClone.isPath());
	// setNonDirectional(graphToClone.isNonDirectional());
	// try {
	// if (graphToClone.getMyEdges() != null)
	// myEdges = graphToClone.getMyEdges().clone();
	//
	// } catch (CloneNotSupportedException e) {
	// myEdges = new EfficientList<Edge>();
	// e.printStackTrace();
	// }
	// try {
	// if (graphToClone.getMyItems() != null)
	// myGeoObjects = graphToClone.getMyItems().clone();
	// } catch (CloneNotSupportedException e) {
	// myGeoObjects = new EfficientListQualified<GeoObj>();
	// e.printStackTrace();
	// }
	//
	// }

	public GeoGraph dijkstra(GeoObj startPoint, GeoObj target) {

		Log.d("GeoGraph", "Running Dijkstra-algo from " + startPoint + " to "
				+ target);

		if (startPoint == null || target == null) {
			Log.e("GeoGraph",
					"Dijkstra-algo error: startPoint or target were null!");
			return null;
		}

		if (startPoint == target) {
			Log.w("GeoGraph",
					"Dijkstra-algo warning: startPoint and target were the same points.");
			GeoGraph g = new GeoGraph();
			g.add(startPoint);
			return g;
		}

		// first move source item to first position because dijstra uses first
		// item as startPoint
		if (!moveObjToFirstPosition(startPoint)) {
			return null;
		}

		int nodesArrayLength = myNodes.myLength;

		// init edgeWeight Array:
		int[][] edgeWeight = new int[nodesArrayLength][nodesArrayLength];
		for (int i = 0; i < nodesArrayLength; i++) {
			// init GeoObj id's (from 0 to n):
			myNodes.get(i).dijkstraId = i;
			// and init edgeWeight array:
			Arrays.fill(edgeWeight[i], Integer.MAX_VALUE);
		}

		final int edgesLength = myEdges.myLength;

		if (DEBUG1) {
			System.out.println(myNodes);
			Log.d("GeoGraph", "EdgesArrayLength=" + edgesLength);
			System.out.println(myEdges);
		}

		for (int i = 0; i < edgesLength; i++) {
			final Edge e = myEdges.get(i);
			edgeWeight[e.from.dijkstraId][e.to.dijkstraId] = e.weight;
			if (nonDirectional)
				edgeWeight[e.to.dijkstraId][e.from.dijkstraId] = e.weight;
		}

		if (DEBUG2)
			debugShowDist(edgeWeight);

		// initialize:
		int[] specialPathLength = new int[nodesArrayLength];
		GeoObj[] previousNodeInPath = new GeoObj[nodesArrayLength];
		EfficientList<GeoObj> canidateSet = new EfficientList<GeoObj>();

		// init arrays:
		for (int i = 0; i < nodesArrayLength; i++) {
			canidateSet.add(myNodes.get(i));
			specialPathLength[i] = edgeWeight[0][i];
			if (specialPathLength[i] != Integer.MAX_VALUE) {
				previousNodeInPath[i] = myNodes.get(0);
			}
		}

		if (DEBUG2) {
			// debugShowGeoObjArray("canidateSet.myArray", canidateSet);
			// debugShowGeoObjArray("previousNodeInPath", previousNodeInPath);
			debugShowIntArray("specialPathLenth", specialPathLength);
			Log.d("GeoGraph", "nodesArrayLength=" + nodesArrayLength);
			Log.d("GeoGraph", "Dijkstra-init ok, starting crawl:");
		}

		// crawl the graph
		for (int i = 0; i < nodesArrayLength - 1; i++) {
			// find the lightest Edge among the candidates
			int lightest = Integer.MAX_VALUE;
			GeoObj n = myNodes.get(0);

			int candLength = canidateSet.myLength;

			for (int i2 = 0; i2 < candLength; i2++) {
				GeoObj g = canidateSet.get(i2);
				if (specialPathLength[g.dijkstraId] < lightest) {
					n = g;
					lightest = specialPathLength[g.dijkstraId];
				}
			}
			canidateSet.remove(n);

			if (DEBUG2) {
				if (n != null)
					Log.d("GeoGraph", i + ": removed " + n + " (id="
							+ n.dijkstraId + ") from canidateSet");
				else
					Log.d("GeoGraph", i
							+ ": removed null (no id) from canidateSet");
				// debugShowGeoObjArray(i + ": canidateSet",
				// canidateSet.myArray);
				Log.d("GeoGraph", i + ": lightest=" + lightest);
			}

			// see if any Edges from this GeoObj yield a shorter path than from
			// source->that GeoObj n
			for (int j = 0; j < nodesArrayLength; j++) {
				if (specialPathLength[n.dijkstraId] != Integer.MAX_VALUE
						&& edgeWeight[n.dijkstraId][j] != Integer.MAX_VALUE
						&& specialPathLength[n.dijkstraId]
								+ edgeWeight[n.dijkstraId][j] < specialPathLength[j]) {
					// found one, update the path
					specialPathLength[j] = specialPathLength[n.dijkstraId]
							+ edgeWeight[n.dijkstraId][j];
					if (DEBUG2) {
						debugShowIntArray(i + "," + j + ": specialPathLength",
								specialPathLength);
					}
					previousNodeInPath[j] = n;
				}
			}
		}

		GeoGraph result = new GeoGraph();
		result.setIsPath(true);
		int loc = target.dijkstraId;
		result.add(target);
		// backtrack from the target by P(revious), adding to the result list
		while (previousNodeInPath[loc] != myNodes.get(0)) {
			if (previousNodeInPath[loc] == null) {
				Log.d("GeoGraph", "  -> No path found :(");
				return null;
			}
			result.insert(0, previousNodeInPath[loc]);
			loc = previousNodeInPath[loc].dijkstraId;
		}
		result.insert(0, myNodes.get(0));
		result.addEdgesToCreatePath();
		Log.d("GeoGraph", "  -> Resulting path has length "
				+ result.myNodes.myLength);

		return result;
	}

	public void addEdgesToCreatePath() {
		addEdgesToCreatePath(null);
	}

	public void addEdgesToCreatePath(EdgeListener li) {
		GeoObj lastPos = null;
		final int l = myNodes.myLength;
		GeoObj p;
		for (int i = 0; i < l; i++) {
			p = myNodes.get(i);
			if (lastPos != null) {
				if (li != null) {
					li.addEdgeToGraph(this, lastPos, p);
				} else {
					// add default mesh:
					this.addEdge(lastPos, p, null);
				}
			}
			lastPos = p;
		}
		setIsPath(true);

	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}

	private void debugShowIntArray(String string, int[] dist) {
		Log.d("GeoGraph", string);
		String line = "   = ";
		for (int i = 0; i < dist.length; i++) {
			if (dist[i] == Integer.MAX_VALUE) {
				line += "inf, ";
			} else {
				line += dist[i] + ", ";
			}
		}
		Log.d("GeoGraph", line);
	}

	private void debugShowDist(int[][] dist) {
		Log.d("GeoGraph", "Distance Matrix:");
		String line = "";
		for (int i = 0; i < dist.length; i++) {
			line = "   " + i + ": ";
			for (int j = 0; j < dist[i].length; j++) {
				if (dist[i][j] != Integer.MAX_VALUE) {
					line += dist[i][j] + ",";
				} else {
					line += "inf ,";
				}
			}
			Log.d("GeoGraph", line);
		}
	}

	private boolean moveObjToFirstPosition(GeoObj obj) {
		if ((myNodes == null))
			return false;
		if (myNodes.remove(obj)) {
			return myNodes.insert(0, obj);
		}
		return false;
	}

	@Override
	public boolean insert(int pos, GeoObj geoObj) {
		if (myNodes == null)
			myNodes = new EfficientListQualified<GeoObj>();
		return myNodes.insert(pos, geoObj);
	}

	/**
	 * @param geoObj
	 * @return true if the {@link GeoObj} was added and false if it already
	 *         existed or was NULL
	 */
	@Override
	public boolean add(GeoObj geoObj) {
		if (myNodes == null)
			myNodes = new EfficientListQualified<GeoObj>();
		if (myNodes.contains(geoObj) == -1) {
			myNodes.add(geoObj);
			return true;
		}
		return false;
	}

	/**
	 * if this is set to true the visualization will connect all elements with
	 * edges. now the order of the nodes is important and findPathTo() shouldnt
	 * be used anymore
	 * 
	 * TODO create edges so that order of nodes is unimportant again??
	 * 
	 * @param isPath
	 */
	public void setIsPath(boolean isPath) {
		this.isPath = isPath;
	}

	public boolean isPath() {
		return isPath;
	}

	// public Obj toObj(GeoObj relativeNullPoint) {
	// Obj o = new Obj();
	// MeshGroup m = GLFactory.f().newMeshGroup(null, null);
	// Object[] a = myGeoObjects.myArray;
	// int size = myGeoObjects.myLength;
	// GeoObj lastPos = null;
	// final boolean isPath = isPath();
	// for (int i = 0; i < size; i++) {
	// GeoObj g = ((GeoObj) a[i]);
	// /*
	// * if the position of an geoObject isn't calculated automatically
	// * then calculate it now:
	// */
	// if (!GeoObj.autoCalcTheVirtualPosOfGeoObjects)
	// g.calcVirtualPosition(relativeNullPoint);
	// /*
	// * then add the default selection commands set for the graph to the
	// * objects:
	// */
	// g.setSelectionCommands(this);
	// m.add(g);
	// if (isPath) {
	// if (lastPos != null) {
	// m.add(createNewPath(lastPos, g, relativeNullPoint));
	// }
	// lastPos = g;
	// }
	// }
	//
	// o.set(Consts.COMP_GRAPHICS, m);
	// return o;
	// }

	public GeoObj findBestPointFor(String searchTerm) {
		Log.d("GeoGraph", "Searching graph for " + searchTerm);
		GeoGraph searchResults = findGeoObjects(searchTerm);

		if (searchResults == null) {
			Log.d("GeoGraph", "  -> Nothing found for '" + searchTerm + "'");
			return null;
		}
		Log.d("GeoGraph", "  -> Found item that matches");
		return searchResults.getAllItems().get(0);
	}

	/**
	 * @param start
	 *            can be null, then the current device position will be used as
	 *            the start point automatically
	 * @param target
	 * @return
	 */
	public GeoGraph findPath(GeoObj start, GeoObj target) {
		if (start == null) {
			start = getClosesedObjTo(EventManager.getInstance()
					.getCurrentLocationObject());
		}
		return dijkstra(start, target);
	}

	/**
	 * @param pos
	 * @return the {@link GeoObj} contained in this {@link GeoGraph} which has
	 *         the smallest distance to the specified pos-{@link GeoObj}
	 */
	public GeoObj getClosesedObjTo(GeoObj pos) {
		GeoObj result = null;
		if (myNodes == null)
			return null;
		double minDistance;

		GeoObj a = myNodes.get(0);
		if (a != null) {
			minDistance = a.getDistance(pos);
			result = a;
		} else {
			return null;
		}

		for (int i = 1; i < myNodes.myLength; i++) {
			GeoObj o = myNodes.get(i);
			double distance = o.getDistance(pos);
			if (distance < minDistance) {
				minDistance = distance;
				result = o;
			}
		}
		return result;
	}

	public GeoGraph findGeoObjects(String searchTerm) {
		if (myNodes == null)
			return null;
		GeoGraph searchResults = null;
		for (int i = 0; i < myNodes.myLength; i++) {
			GeoObj o = myNodes.get(i);
			float matchQuality = o.matchesSearchTerm(searchTerm);
			if (matchQuality > 0) {
				if (searchResults == null) {
					searchResults = new GeoGraph();
				}
				searchResults.insertWithDefinedQuality(matchQuality, o);
			}
		}
		return searchResults;
	}

	private void insertWithDefinedQuality(float matchQuality, GeoObj o) {
		if (myNodes == null)
			myNodes = new EfficientListQualified<GeoObj>();
		myNodes.add(o, matchQuality);
	}

	/**
	 * @param from
	 * @param to
	 * @param edgeMeshComp
	 *            if this is null a default mesh from {@link Edge}
	 *            .getDefaultMesh() will be used
	 * @return the added edge or null if edge already existed in the graph
	 */
	public Edge addEdge(GeoObj from, GeoObj to, MeshComponent edgeMeshComp) {
		if (myEdges == null)
			myEdges = new EfficientList<Edge>();
		if (hasEdge(from, to) == -1) {
			if (edgeMeshComp == null)
				edgeMeshComp = Edge.getDefaultMesh(this, from, to, this
						.getInfoObject().getColor());
			Edge e = new Edge(from, to, edgeMeshComp);
			myEdges.add(e);
			return e;
		} else {
			Log.e(LOG_TAG, "Tried to add new edge but edge from " + from
					+ " to " + to + " already existed!");
		}
		return null;
	}

	/**
	 * @param from
	 * @param to
	 * @return the position of the edge in the list or -1 if its not in the list
	 */
	public int hasEdge(GeoObj from, GeoObj to) {
		if (myEdges == null)
			return -1;

		// TODO move this method to efficient list and Edge.compare(edge) ?

		for (int i = 0; i < myEdges.myLength; i++) {
			Edge e = myEdges.get(i);
			if ((e.from == from && e.to == to)
					|| (e.from == to && e.to == from)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * if this is disabled, all edges have a direction and an edge from A to B
	 * cant be used to get from B to A. Default value is true
	 * 
	 * @param nonDirectional
	 */
	public void setNonDirectional(boolean nonDirectional) {
		this.nonDirectional = nonDirectional;
	}

	@Override
	public String toString() {
		if (myNodes != null) {
			if (HasInfoObject())
				return "GeoGraph '" + getInfoObject().getShortDescr()
						+ "' (size=" + myNodes.myLength + ")";
			return "GeoGraph <noname>(size=" + myNodes.myLength + ")";
		} else {
			if (HasInfoObject())
				return "GeoGraph '" + getInfoObject().getShortDescr()
						+ "' (size=no objects in graph)";
			return "GeoGraph <noname>(size=no objects in graph)";
		}
	}

	@Override
	public void render(GL10 gl, Renderable parent) {
		if (myNodes == null)
			return;
		{
			for (int i = 0; i < myNodes.myLength; i++) {
				myNodes.get(i).render(gl, this);
			}
		}
		{
			if ((isPath || useEdges) && myEdges != null) {
				for (int i = 0; i < myEdges.myLength; i++) {
					myEdges.get(i).render(gl, this);
				}
			}
		}
	}

	@Override
	public boolean update(float timeDelta, Updateable parent) {
		if (myNodes == null)
			return true;
		setMyParent(parent);
		{
			for (int i = 0; i < myNodes.myLength; i++) {
				if (!myNodes.get(i).update(timeDelta, this)) {
					// remove node if no longer needed (it returned false)
					remove(myNodes.get(i));
				}
			}
		}
		if (useEdges && myEdges != null) {
			for (int i = 0; i < myEdges.myLength; i++) {
				if (!myEdges.get(i).update(timeDelta, this)) {
					remove(myEdges.get(i));
				}
			}
		}
		return true;
	}

	/**
	 * tries to remove a {@link GeoObj} from this graph, this can be a node or
	 * an edge too!
	 * 
	 * @param x
	 * @return
	 */
	@Override
	public boolean remove(GeoObj x) {
		// first try to remove item from the nodes
		if (myNodes.remove(x)) {
			x.setRemoved();
			return true;
		}
		// then fom the edges
		if (myEdges.remove(x)) {
			x.setRemoved();
			return true;
		}
		return false;
	}

	public EfficientList<Edge> getEdges() {
		if (myEdges == null)
			myEdges = new EfficientList<Edge>();
		return myEdges;
	}

	/**
	 * this flag is necessary to not automatically remove new created
	 * {@link GeoGraph}s. This way .isEmpty() will only return true if the
	 * {@link GeoGraph} was cleared at least one time
	 */
	private boolean isClearedAtLeastOneTime = false;

	@Override
	public void clear() {
		isClearedAtLeastOneTime = true;
		if (myNodes != null)
			myNodes.clear();
		if (myEdges != null)
			myEdges.clear();
	}

	@Override
	public void removeEmptyItems() {
		/*
		 * TODO there is a flag isDeleted in GeoObj so search for these imtems
		 * here?
		 */
	}

	public boolean isEmpty() {
		return getAllItems().myLength == 0;
	}

	@Override
	public boolean isCleared() {
		if (getAllItems().myLength == 0 && isClearedAtLeastOneTime) {
			return true;
		}
		return false;
	}

	@Override
	public EfficientListQualified<GeoObj> getAllItems() {
		if (myNodes == null)
			myNodes = new EfficientListQualified<GeoObj>();
		return myNodes;
	}

	@Override
	public int length() {
		return getAllItems().myLength;
	}

	/**
	 * enable this if all edges that are available should be displayed too
	 * 
	 * @param useThem
	 */
	public void setUseEdges(boolean useThem) {
		useEdges = useThem;
	}

	public boolean isUsingItsEdges() {
		return useEdges;
	}

	public boolean hasEdges() {
		if (myEdges != null && myEdges.myLength > 0)
			return true;
		return false;
	}

	public EfficientList<GeoObj> getConnectedNodesOf(GeoObj obj) {
		EfficientList<GeoObj> result = new EfficientList<GeoObj>();
		for (int i = 0; i < myEdges.myLength; i++) {
			if (myEdges.get(i).from.equals(obj)) {
				result.add(myEdges.get(i).to);
			} else if (myEdges.get(i).to.equals(obj)) {
				result.add(myEdges.get(i).from);
			}
		}
		return result;
	}

	public EfficientList<GeoObj> getFollowingNodesOf(GeoObj obj) {

		if (isNonDirectional()) {
			return getConnectedNodesOf(obj);
		}

		EfficientList<GeoObj> result = new EfficientList<GeoObj>();
		if (myEdges != null) {
			for (int i = 0; i < myEdges.myLength; i++) {
				if (myEdges.get(i).from.equals(obj)) {
					result.add(myEdges.get(i).to);
				}
			}
		}
		return result;
	}

	public static GeoGraph convertToGeoGraph(EfficientList<GeoObj> list,
			boolean directional, SimpleNodeEdgeListener l) {
		return convertToGeoGraph(list, directional, l, l);
	}

	public static GeoGraph convertToGeoGraph(EfficientList<GeoObj> list,
			boolean directional, NodeListener nl, EdgeListener el) {
		GeoGraph result = new GeoGraph();
		result.setNonDirectional(!directional);

		for (int i = 0; i < list.myLength; i++) {
			if (i == 0) {
				nl.addFirstNodeToGraph(result, list.get(0));
			} else if (i == list.myLength - 1) {
				nl.addLastNodeToGraph(result, list.get(list.myLength - 1));
			} else {
				nl.addNodeToGraph(result, list.get(i));
			}
			if (i < list.myLength - 1) {
				el.addEdgeToGraph(result, list.get(i), list.get(i + 1));
			}
		}

		return result;
	}

	/**
	 * @param from
	 * @param to
	 * @return null of there is no edge for these nodes
	 */
	public Edge getEdge(GeoObj from, GeoObj to) {
		int pos = hasEdge(from, to);
		if (pos == -1)
			return null;
		return getEdges().get(pos);
	}
}
