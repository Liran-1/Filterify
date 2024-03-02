package com.example.filterify.utils;

    public enum ImageFilter {
        BLUR("BLUR"),
        CONTOUR("CONTOUR"),
        DETAIL("DETAIL"),
        EDGE_ENHANCE("EDGE_ENHANCE"),
        EDGE_ENHANCE_MORE("EDGE_ENHANCE_MORE"),
        EMBOSS("EMBOSS"),
        FIND_EDGES("FIND_EDGES"),
        SHARPEN("SHARPEN"),
        SMOOTH("SMOOTH"),
        SMOOTH_MORE("SMOOTH_MORE");

        private final String value;

        ImageFilter(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public ImageFilter fromValue(String value) {
            for (ImageFilter filter : ImageFilter.values()) {
                if (filter.getValue().equalsIgnoreCase(value)) {
                    return filter;
                }
            }
            throw new IllegalArgumentException("Unknown filter: " + value);
        }



}
