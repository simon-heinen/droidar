package gestures;

/**
 * To identify gestures in a structured way, we're making use of the
 * "Extensible Enum Pattern". When creating your own gestures, create an enum
 * that implements this interface and you will be able to pass instances of your
 * enum in the required locations (e.g. PhoneGestureDetector.getType()).
 * 
 * @see https://blogs.oracle.com/darcy/entry/enums_and_mixins
 * @author kaktus621@gmail.com (Martin Matysiak)
 */
public interface PhoneGesture {

}
