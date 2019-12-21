package core.schedule;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Concrete implementation of a schedule loader.
 */
public class ScheduleLoaderImpl implements ScheduleLoader {
    private static final String TABLE_NAME = "collection_dates";
    private static final AmazonDynamoDB DDB = AmazonDynamoDBClientBuilder.defaultClient();
    private static final String PRIMARY_KEY = "postcode";
    private static final String DATES_FIELD = "start_dates";
    private static final String EXCEPTIONS_FIELD = "exceptions";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Default constructor.
     */
    ScheduleLoaderImpl() {

    }

    @Override
    public ZonedDateTime getCollectionEventDate(final String postcode,
                                                final CollectionEvent collectionEvent) {
        final GetItemRequest request = getGetItemRequest(postcode);

        final Map<String,AttributeValue> returnedItem = DDB.getItem(request).getItem();
        if (returnedItem != null) {
            final AttributeValue dateMap = returnedItem.get(DATES_FIELD);
            final String dateString = dateMap.getM().get(collectionEvent.toString()).getS();

            return getDateTime(dateString);
        } else {
            System.out.format("No item found with the key %s!\n", postcode);
        }

        return null;
    }

    private ZonedDateTime getDateTime(final String dateString) {
        return LocalDateTime.parse(
                String.format("%s 23:59:59", dateString), DATE_TIME_FORMATTER)
                .atZone(ZoneId.of("Z"));
    }

    private GetItemRequest getGetItemRequest(final String postcode) {
        final HashMap<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(PRIMARY_KEY, new AttributeValue(postcode));
        return new GetItemRequest()
                .withKey(keyToGet)
                .withTableName(TABLE_NAME);
    }

    @Override
    public Collection<ZonedDateTime> getScheduleExceptions(final String postcode) {
        final GetItemRequest request = getGetItemRequest(postcode);

        final Map<String,AttributeValue> returnedItem = DDB.getItem(request).getItem();
        if (returnedItem != null) {
            final AttributeValue dateMap = returnedItem.get(EXCEPTIONS_FIELD);
            return dateMap.getL().stream()
                    .map(AttributeValue::getS)
                    .map(this::getDateTime)
                    .collect(Collectors.toSet());
        } else {
            System.out.format("No item found with the key %s!\n", postcode);
        }

        return null;
    }
}
