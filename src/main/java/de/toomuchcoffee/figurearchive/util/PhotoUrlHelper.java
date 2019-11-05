package de.toomuchcoffee.figurearchive.util;

import de.toomuchcoffee.figurearchive.entity.Photo;

public class PhotoUrlHelper {
    public static String getImageUrl(Photo photo, int width) {
        return photo.getUrls().stream()
                .filter(s -> s.getWidth() == width)
                .findFirst()
                .map(Photo.PhotoUrl::getUrl)
                .orElse("");
    }
}
