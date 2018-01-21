package core;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import core.schedule.BinSchedule;
import core.schedule.BinScheduleProvider;
import core.schedule.BinScheduleProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Calculates the bin core.schedule.
 */
public class BinDaySpeechlet implements SpeechletV2, ScheduleToSpeechCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(BinDaySpeechlet.class);
    private static final String CARD_TITLE = "BinDay";
    private static final Locale LOCALE = Locale.ENGLISH;
    private static final String UNKNOWN_INTENT = "I'm sorry, I did not understand the question. Please try something else";
    private final Map<String, Supplier<SpeechletResponse>> intentMap = new HashMap<>();
    private BinScheduleProvider scheduleProvider;

    /**
     * Constructor.
     */
    BinDaySpeechlet() {
        scheduleProvider = new BinScheduleProviderImpl();
    }

    /** {@inheritDoc} */
    @Override
    public void onSessionStarted(final SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        LOG.info("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());

        intentMap.put("CurrentScheduleIntent", this::getCurrentBinSchedule);
        intentMap.put("NextScheduleIntent", this::getNextBinSchedule);
    }

    /** {@inheritDoc} */
    @Override
    public SpeechletResponse onLaunch(final SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        LOG.info("onLaunch requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        return getWelcomeResponse();
    }

    /** {@inheritDoc} */
    @Override
    public SpeechletResponse onIntent(final SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        final IntentRequest request = requestEnvelope.getRequest();
        LOG.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                requestEnvelope.getSession().getSessionId());

        final Intent intent = request.getIntent();
        final String intentName = (intent != null) ? intent.getName() : null;

        if (intentMap.containsKey(intentName)) {
            return Optional.ofNullable(intentMap.get(intentName))
                    .orElseGet(() -> this::getUnknownResponse).get();
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        } else {
            return getUnknownResponse();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onSessionEnded(final SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        LOG.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        // any cleanup logic goes here
    }

    private SpeechletResponse getUnknownResponse() {
        return SpeechHelper.getAskResponse(CARD_TITLE, UNKNOWN_INTENT);
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getWelcomeResponse() {
        final String speechText = "Welcome to the Bin Day Schedule Helper, you can ask when the next bin collection is, " +
                "what the current bin cycle is, and more.";
        return SpeechHelper.getAskResponse(CARD_TITLE, speechText);
    }

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelpResponse() {
        final String speechText = "You can ask, what is this weeks bin collection?";
        return SpeechHelper.getAskResponse(CARD_TITLE, speechText);
    }

    private SpeechletResponse getCurrentBinSchedule()
    {
        final String speechText = getBinScheduleText(ZonedDateTime.now());

        // Create the Simple card content.
        SimpleCard card = SpeechHelper.getSimpleCard(CARD_TITLE, speechText);

        // Create the plain text output.
        final PlainTextOutputSpeech speech = SpeechHelper.getPlainTextOutputSpeech(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    private SpeechletResponse getNextBinSchedule()
    {
        final String speechText = getNextBinScheduleText(ZonedDateTime.now());

        // Create the Simple card content.
        SimpleCard card = SpeechHelper.getSimpleCard(CARD_TITLE, speechText);

        // Create the plain text output.
        final PlainTextOutputSpeech speech = SpeechHelper.getPlainTextOutputSpeech(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    /** {@inheritDoc} */
    @Override
    public String getBinScheduleText(final ZonedDateTime today) {
        final BinSchedule binSchedule = scheduleProvider.getCurrentBinSchedule(today);

        final ZonedDateTime collectionDate = binSchedule.getCollectionDate();

        final String tense;
        if (collectionDate.isAfter(today)) {
            tense = "will be";
        }
        else
        {
            tense = "was";
        }

        final DayOfWeek day = collectionDate.getDayOfWeek();
        final String dayOfWeek = day.getDisplayName(TextStyle.FULL, LOCALE);

        final String collectionTypes = binSchedule.getCollectionType().stream()
                .map(Object::toString)
                .collect(Collectors.joining(" and "));
        return String.format("This weeks collection %s on %s and %s %s",
                tense,
                dayOfWeek,
                tense,
                collectionTypes);
    }

    /** {@inheritDoc} */
    @Override
    public String getNextBinScheduleText(final ZonedDateTime today) {
        final BinSchedule binSchedule = scheduleProvider.getNextBinSchedule(today);

        final ZonedDateTime collectionDate = binSchedule.getCollectionDate();

        final DayOfWeek day = collectionDate.getDayOfWeek();
        final String dayOfWeek = day.getDisplayName(TextStyle.FULL, LOCALE);

        final String collectionTypes = binSchedule.getCollectionType().stream()
                .map(Object::toString)
                .collect(Collectors.joining(" and "));
        return String.format("Next weeks collection will be on %s and will be %s",
                dayOfWeek,
                collectionTypes);
    }
}
