package core.schedule;

/**
 * Defines key events to do with collection types.
 */
public enum CollectionEvent {
    FirstRubbish("rubbish"),
    FirstGarden("first_garden"),
    LastGarden("last_garden");

    private final String fieldName;

    CollectionEvent(final String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return fieldName;
    }
}
