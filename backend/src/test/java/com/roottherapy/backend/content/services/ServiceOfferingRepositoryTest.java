package com.roottherapy.backend.content.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ServiceOfferingRepositoryTest {

    @Autowired
    private ServiceOfferingRepository repository;

    // JUNIT-SVC-010
    @Test
    void findPublishedNonArchivedOrderByDisplayOrder_returnsCorrectRows() {
        String token = UUID.randomUUID().toString();

        ServiceOffering later = saveService(
                "Later " + token,
                "later-" + token,
                5,
                true,
                false
        );
        ServiceOffering earlier = saveService(
                "Earlier " + token,
                "earlier-" + token,
                1,
                true,
                false
        );
        ServiceOffering unpublished = saveService(
                "Unpublished " + token,
                "unpublished-" + token,
                0,
                false,
                false
        );
        ServiceOffering archived = saveService(
                "Archived " + token,
                "archived-" + token,
                0,
                true,
                true
        );

        List<ServiceOffering> results = repository
                .findByPublishedTrueAndArchivedFalseOrderByDisplayOrderAsc();

        Set<Long> testIds = Set.of(
                later.getId(),
                earlier.getId(),
                unpublished.getId(),
                archived.getId()
        );
        List<ServiceOffering> testResults = results.stream()
                .filter(service -> testIds.contains(service.getId()))
                .toList();

        assertEquals(2, testResults.size());
        assertEquals(earlier.getId(), testResults.get(0).getId());
        assertEquals(later.getId(), testResults.get(1).getId());
        assertTrue(testResults.stream().allMatch(ServiceOffering::getPublished));
        assertTrue(testResults.stream().noneMatch(ServiceOffering::getArchived));
    }

    // JUNIT-SVC-011
    @ParameterizedTest(name = "published={0}, archived={1}")
    @CsvSource({
            "false, false",
            "true, true"
    })
    void findPublicBySlug_unpublishedOrArchived_returnsEmpty(
            boolean published,
            boolean archived
    ) {
        String token = UUID.randomUUID().toString();
        String slug = "hidden-service-" + token;

        saveService(
                "Hidden service " + token,
                slug,
                0,
                published,
                archived
        );

        assertTrue(
                repository.findBySlugAndPublishedTrueAndArchivedFalse(slug).isEmpty()
        );
    }

    private ServiceOffering saveService(
            String title,
            String slug,
            int displayOrder,
            boolean published,
            boolean archived
    ) {
        ServiceOffering service = new ServiceOffering(
                title,
                slug,
                "Test category",
                "Test short description"
        );
        service.setFullDescription(List.of("Test full description"));
        service.setPoints(List.of("Test point"));
        service.setKeywords(List.of("test"));
        service.setDisplayOrder(displayOrder);
        service.setPublished(published);
        service.setArchived(archived);
        return repository.saveAndFlush(service);
    }
}
