package gestures;

public enum StandardPhoneGesture implements PhoneGesture {
    /* No gesture detected with confidence */
    NONE,
    /* A simple slashing gesture */
    SLASH,
    /* User holds the phone in a stable position, as if looking at it */
    LOOKING,
    /* A "punching" gesture that goes vertically upwards */
    UPPERCUT,
    /*
     * A "360 degree turn" of the player (i.e., the player's nose faces all
     * georgraphic directions once)
     */
    FULL_TURN
}
