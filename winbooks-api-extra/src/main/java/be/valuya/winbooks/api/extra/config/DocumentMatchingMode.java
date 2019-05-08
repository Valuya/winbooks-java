package be.valuya.winbooks.api.extra.config;

/**
 * How to link an accounting entry with its document
 */
public enum DocumentMatchingMode {
    /**
     * Stream and cache all documents, and query the cache to find a match.
     */
    EAGERLY_CACHE_ALL_DOCUMENTS,
    /**
     * Do not attempt to find a document for each entry.
     */
    SKIP
}
