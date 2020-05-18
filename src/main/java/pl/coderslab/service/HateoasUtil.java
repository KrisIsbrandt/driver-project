package pl.coderslab.service;

public final class HateoasUtil {

    private HateoasUtil() {
        throw new AssertionError();
    }

    public static String createLinkHeader(String uri, Relation rel) {
        return "<" + uri + ">; rel=\"" + rel.getRelationType() + "\"";
    }

    public enum Relation {
        REL_NEXT ("next"),
        REL_PREV ("prev"),
        REL_FIRST("first"),
        REL_LAST("last");

        private final String relationType;

        Relation(String relationType) {
            this.relationType = relationType;
        }

        public String getRelationType() {
            return relationType;
        }
    }
}
