package de.toomuchcoffee.figurearchive.util;

import de.toomuchcoffee.figurearchive.entity.Photo;

public class PhotoUrlHelper {
    public static String getImageUrl(Photo photo, int width) {
        return photo.getUrls().stream()
                .filter(s -> s.getWidth() == width)
                .findAny()
                .map(Photo.PhotoUrl::getUrl)
                .orElse(photo.getUrls().get(0).getUrl());
    }
}
