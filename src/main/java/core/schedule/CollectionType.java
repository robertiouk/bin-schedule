package core.schedule;

/**
 * Defines a bin collection type.
 */
public enum CollectionType {
    Recycling("Recycling"),
    Garden("Garden"),
    GeneralRubbish("General Rubbish");

    private final String speechName;

    /**
     * Constructor.
     *
     * @param speechName sets the speech name.
     */
    CollectionType(final String speechName) {
        this.speechName = speechName;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return speechName;
    }
}
