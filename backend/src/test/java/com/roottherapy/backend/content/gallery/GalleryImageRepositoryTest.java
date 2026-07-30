package com.roottherapy.backend.content.gallery;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GalleryImageRepositoryTest {

    @Autowired
    private GalleryImageRepository repository;

    // JUNIT-GAL-009
    @Test
    void findVisibleNonArchivedOrderByDisplayOrder_returnsCorrectRows() {
        String token = UUID.randomUUID().toString();

        GalleryImage later = saveImage(token + "-later", 5, true, false);
        GalleryImage earlier = saveImage(token + "-earlier", 1, true, false);
        GalleryImage hidden = saveImage(token + "-hidden", 0, false, false);
        GalleryImage archived = saveImage(token + "-archived", 0, true, true);

        List<GalleryImage> results = repository
                .findByVisibleTrueAndArchivedFalseOrderByDisplayOrderAsc();

        Set<Long> testIds = Set.of(
                later.getId(),
                earlier.getId(),
                hidden.getId(),
                archived.getId()
        );

        List<GalleryImage> testResults = results.stream()
                .filter(image -> testIds.contains(image.getId()))
                .toList();

        assertEquals(2, testResults.size());
        assertEquals(earlier.getId(), testResults.get(0).getId());
        assertEquals(later.getId(), testResults.get(1).getId());
        assertTrue(testResults.stream().allMatch(GalleryImage::getVisible));
        assertFalse(testResults.stream().anyMatch(GalleryImage::getArchived));
    }

    private GalleryImage saveImage(
            String token,
            int displayOrder,
            boolean visible,
            boolean archived
    ) {
        GalleryImage image = new GalleryImage(
                "/uploads/gallery/" + token + ".jpg",
                "Caption " + token,
                "Alt text " + token
        );
        image.setDisplayOrder(displayOrder);
        image.setVisible(visible);
        image.setArchived(archived);
        return repository.saveAndFlush(image);
    }
}
