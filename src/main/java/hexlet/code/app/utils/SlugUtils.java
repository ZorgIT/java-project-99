package hexlet.code.app.utils;

import org.apache.commons.text.CaseUtils;

public class SlugUtils {
    public static String generateSlug(String name) {
        String slug = CaseUtils.toCamelCase(name, false, '_')
                .toLowerCase()
                .replaceAll("[^a-z0-9-]", "");

        if (slug.isBlank()) {
            throw new IllegalArgumentException("Cannot generate slug from name: " + name);
        }
        return slug;
    }

    private String normalizeSlug(String slug) {
        if (slug == null) {
            return null;
        }
        return slug.toLowerCase().replaceAll("_", "");
    }
}
