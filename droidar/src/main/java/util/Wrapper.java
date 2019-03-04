package util;

/**
 * The wrapper class is very useful because it can hold an object reference
 * which might change at runtime. so instead of creating a static variable where
 * these objects are placed in, this wrapper can be used. Imagine it like a bag
 * where you can put something in and take it out and everyone who wants to use
 * the objects inside only have to know about the bag
 * 
 * @author Spobo
 * 
 */
public class Wrapper {

	public enum Type {
		String, Int, Bool, Float, Object, None, Wrapper
	}

	private Type myType;
	private String myS;
	private boolean myB;
	private int myI;
	private Object myObject;
	private float myF;

	public Wrapper() {
		myType = Type.None;
	}

	public Wrapper(float initFloat) {
		setTo(initFloat);
	}

	public Wrapper(int initInt) {
		setTo(initInt);
	}

	public Wrapper(Object o) {
		setTo(o);
	}

	public Wrapper(boolean b) {
		setTo(b);
	}

	public Wrapper(String initString) {
		setTo(initString);
	}

	public boolean equals(boolean b) {
		if (b == myB)
			return true;
		return false;
	}

	public boolean equals(float f) {
		if (f == myI)
			return true;
		return false;
	}

	public boolean equals(int i) {
		if (i == myI)
			return true;
		return false;
	}

	public boolean equals(String s) {
		if (s.equals(myS))
			return true;
		return false;
	}

	public boolean getBooleanValue() {
		return myB;
	}

	private float getFloatValue() {
		return myF;
	}

	public int getIntValue() {
		return myI;
	}

	public Object getObject() {
		return myObject;
	}

	public String getStringValue() {
		return myS;
	}

	public Type getType() {
		return myType;
	}

	public void setTo(boolean newBooleanValue) {
		myB = newBooleanValue;
		myType = Type.Bool;
	}

	public void setTo(float floatValue) {
		myF = floatValue;
		myType = Type.Float;
	}

	public void setTo(int newIntValue) {
		myI = newIntValue;
		myType = Type.Int;
	}

	public void setTo(Object object) {
		myObject = object;
		myType = Type.Object;
	}

	public void setTo(Wrapper w) {
		switch (w.getType()) {
		case Bool:
			setTo(w.getBooleanValue());
			break;
		case Int:
			setTo(w.getIntValue());
			break;
		case Float:
			setTo(w.getFloatValue());
			break;
		case String:
			setTo(w.getStringValue());
			break;
		case Object:
			setTo(w.getObject());
			break;
		default:
			break;
		}
	}

	public void setTo(String newStringValue) {
		myS = newStringValue;
		myType = Type.String;
	}

	@Override
	public String toString() {
		switch (myType) {
		case Object:
			return "wrapper (obj=" + myObject + ")";
		case String:
			return "wrapper (string=" + myS + ")";
		case Int:
			return "wrapper (int=" + myI + ")";
		case Bool:
			return "wrapper (bool=" + myB + ")";
		case Float:
			return "wrapper (float=" + myF + ")";
		case None:
			return "wrapper (empty)";
		}
		return "wrapper (empty)";
	}

	public void clear() {
		myType = Type.None;
		myS = null;
		myB = false;
		myI = 0;
		myObject = null;
		myF = 0;
	}

	// TODO write tests

}
