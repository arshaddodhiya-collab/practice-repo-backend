package com.test.practice.projection;

public interface PostView {
    Long getId();

    String getTitle();

    String getContent();

    // Nested projection for Category
    CategorySummary getCategory();

    interface CategorySummary {
        Long getId();

        String getName();
    }
}
