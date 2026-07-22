package com.roottherapy.backend.content.gallery;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GalleryImageRepository
        extends JpaRepository<GalleryImage, Long> {

    /*
     * Public pages only receive visible, non-archived images.
     */
    List<GalleryImage>
    findByVisibleTrueAndArchivedFalseOrderByDisplayOrderAsc();

    /*
     * Admins can see active and archived images.
     */
    List<GalleryImage>
    findAllByOrderByDisplayOrderAsc();
}