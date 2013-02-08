package gl.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;

import util.EfficientList;
import util.Log;
import util.Vec;
import android.content.res.Resources;

/*
 * TODO
 * 
 * http://en.wikipedia.org/wiki/Obj#Vertex.2FTexture-coordinate
 * 
 * http://local.wasp.uwa.edu.au/~pbourke/dataformats/obj/
 */
@Deprecated
public class ObjParser {
	private static final String LOG_TAG = "Obj-Parser";

	private final String VERTEX = "v";
	private final String FACE = "f";
	private final String TEXCOORD = "vt";
	private final String NORMAL = "vn";
	private final String OBJECT = "o";
	private final String MATERIAL_LIB = "mtllib";
	private final String USE_MATERIAL = "usemtl";
	private final String NEW_MATERIAL = "newmtl";
	private final String DIFFUSE_TEX_MAP = "map_Kd";

	private HashMap<String, ObjMaterial> materialMap;

	private Resources resources;
	private String resourceID;
	private String packageID;

	/**
	 * A material key will be something like Material_XY.TGA and will be defined
	 * in one of the importet mtl files
	 */
	private String currentMaterialKey;

	public ObjParser(Resources resources, String resourceID) {
		this.resources = resources;
		this.resourceID = resourceID;
		if (resourceID.indexOf(":") > -1)
			this.packageID = resourceID.split(":")[0];
	}

	public void parse() throws Exception {
		long startTime = Calendar.getInstance().getTimeInMillis();

		InputStream fileIn = resources.openRawResource(resources.getIdentifier(
				resourceID, null, null));
		BufferedReader buffer = new BufferedReader(
				new InputStreamReader(fileIn));

		// materialMap = new HashMap<String, ObjMaterial>();

		Log.d(LOG_TAG, "Start parsing object " + resourceID);
		Log.d(LOG_TAG, "Start time " + startTime);

		EfficientList<Vec> vertexList = new EfficientList<Vec>();
		EfficientList<TexturCoord> textureList = new EfficientList<TexturCoord>();
		EfficientList<int[]> shapeList = new EfficientList<int[]>();
		EfficientList<Vec> normalsList = new EfficientList<Vec>();

		String line;

		while ((line = buffer.readLine()) != null) {

			StringTokenizer lineElements = new StringTokenizer(line, " ");

			if (lineElements.countTokens() == 0)
				continue;

			String type = lineElements.nextToken();

			if (type.equals(VERTEX)) {
				Vec vertex = new Vec();
				vertex.x = Float.parseFloat(lineElements.nextToken());
				vertex.y = Float.parseFloat(lineElements.nextToken());
				vertex.z = Float.parseFloat(lineElements.nextToken());
				vertexList.add(vertex);
			} else if (type.equals(FACE)) {
				/*
				 * something like "f 102 24 91 7" so its a face with 4 vertices
				 */
				int verticesCount = lineElements.countTokens() - 1;
				int[] indiceArray = new int[verticesCount];
				for (int i = 0; i < verticesCount; i++) {
					indiceArray[i] = Integer.parseInt(lineElements.nextToken());
				}
				shapeList.add(indiceArray);

			} else if (type.equals(TEXCOORD)) {
				TexturCoord texCoord = new TexturCoord(
						Float.parseFloat(lineElements.nextToken()),
						Float.parseFloat(lineElements.nextToken()));
				textureList.add(texCoord);
			} else if (type.equals(NORMAL)) {
				Vec normal = new Vec();
				normal.x = Float.parseFloat(lineElements.nextToken());
				normal.y = Float.parseFloat(lineElements.nextToken());
				normal.z = Float.parseFloat(lineElements.nextToken());
				normalsList.add(normal);
			} else if (type.equals(MATERIAL_LIB)) {
				loadMaterialLib(lineElements.nextToken());
			} else if (type.equals(USE_MATERIAL)) {
				currentMaterialKey = lineElements.nextToken();
			}
		}

		long endTime = Calendar.getInstance().getTimeInMillis();
		Log.d(LOG_TAG, "End time " + (endTime - startTime));
	}

	public class TexturCoord {
		float x;
		float y;

		public TexturCoord(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	private void loadMaterialLib(String libID) {
		StringBuffer resourceID = new StringBuffer(packageID);
		StringBuffer libIDSbuf = new StringBuffer(libID);
		int dotIndex = libIDSbuf.lastIndexOf(".");
		if (dotIndex > -1)
			libIDSbuf = libIDSbuf.replace(dotIndex, dotIndex + 1, "_");

		resourceID.append(":raw/");
		resourceID.append(libIDSbuf.toString());

		InputStream fileIn = resources.openRawResource(resources.getIdentifier(
				resourceID.toString(), null, null));
		BufferedReader buffer = new BufferedReader(
				new InputStreamReader(fileIn));
		String line;
		String currentMaterial = "";

		try {
			while ((line = buffer.readLine()) != null) {
				String[] parts = line.split(" ");
				if (parts.length == 0)
					continue;
				String type = parts[0];

				if (type.equals(NEW_MATERIAL)) {
					if (parts.length > 1) {
						currentMaterial = parts[1];
						materialMap.put(currentMaterial, new ObjMaterial(
								currentMaterial));
					}
				} else if (type.equals(DIFFUSE_TEX_MAP)) {
					if (parts.length > 1) {
						materialMap.get(currentMaterial).diffuseTextureMap = parts[1];
						StringBuffer texture = new StringBuffer(packageID);
						texture.append(":drawable/");

						StringBuffer textureName = new StringBuffer(parts[1]);
						dotIndex = textureName.lastIndexOf(".");
						if (dotIndex > -1)
							texture.append(textureName.substring(0, dotIndex));
						else
							texture.append(textureName);

						int bmResourceID = resources.getIdentifier(
								texture.toString(), null, null);
						// Bitmap b =
						// Utils.makeBitmapFromResourceId(bmResourceID);
						// textureAtlas.addBitmapAsset(new BitmapAsset(
						// currentMaterial, texture.toString()));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// private class ObjFace extends ParseObjectFace {
	// public ObjFace(String line, String materialKey, int faceLength) {
	// super();
	// this.materialKey = materialKey;
	// this.faceLength = faceLength;
	// boolean emptyVt = line.indexOf("//") > -1;
	// if (emptyVt)
	// line = line.replace("//", "/");
	// StringTokenizer parts = new StringTokenizer(line);
	// parts.nextToken();
	// StringTokenizer subParts = new StringTokenizer(parts.nextToken(),
	// "/");
	// int partLength = subParts.countTokens();
	// hasuv = partLength >= 2 && !emptyVt;
	// hasn = partLength == 3 || (partLength == 2 && emptyVt);
	//
	// v = new int[faceLength];
	// if (hasuv)
	// uv = new int[faceLength];
	// if (hasn)
	// n = new int[faceLength];
	//
	// for (int i = 1; i < faceLength + 1; i++) {
	// if (i > 1)
	// subParts = new StringTokenizer(parts.nextToken(), "/");
	//
	// int index = i - 1;
	// v[index] = (short) (Short.parseShort(subParts.nextToken()) - 1);
	// if (hasuv)
	// uv[index] = (short) (Short.parseShort(subParts.nextToken()) - 1);
	// if (hasn)
	// n[index] = (short) (Short.parseShort(subParts.nextToken()) - 1);
	// }
	// }
	// }

	private class ObjMaterial {
		public String name;
		public String diffuseTextureMap;
		public float offsetU;
		public float offsetV;

		public ObjMaterial(String name) {
			this.name = name;
		}
	}
}
