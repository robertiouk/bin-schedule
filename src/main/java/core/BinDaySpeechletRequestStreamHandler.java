package core;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * A Speechlet Handler that calculates the bin collection schedules.
 */
public final class BinDaySpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds;
    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        supportedApplicationIds = new HashSet<>();
        supportedApplicationIds.add("amzn1.ask.skill.e0273f40-0a08-4ebb-b666-6db90c900e95");
    }

    /**
     * Constructor.
     */
    public BinDaySpeechletRequestStreamHandler() {
        super(new BinDaySpeechlet(), supportedApplicationIds);
    }
}
