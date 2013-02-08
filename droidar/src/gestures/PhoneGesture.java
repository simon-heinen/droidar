package gestures;

/**
 * A list of possible gesture types. The gesture is explicitly called
 * **Phone**Gesture in order to avoid confusion with e.g. touch gestures that
 * can be performed on the screen. PhoneGestures involve moving the whole phone
 * (i.e. physical movement through space).
 * 
 * @author marmat (Martin Matysiak)
 */
public enum PhoneGesture {
	/* No gesture detected with confidence */
	NONE,
	/* A simple slashing gesture */
	SLASH,
	/* User holds the phone in a stable position, as if looking at it */
	LOOKING,
	/* A "punching" gesture that goes vertically upwards */
	UPPERCUT,
	/* A "360° turn" of the player (i.e., the player's nose faces all georgraphic directions once) */
	FULL_TURN
}
