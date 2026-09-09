package com.booking.service;

import com.booking.model.AvailabilityStatus;
import com.booking.model.Resource;
import com.booking.model.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Generic manager for collections of campus resources.
 * <p>
 * Stores resources in an {@link ArrayList} and provides typed search and
 * maintenance operations while preserving encapsulation of the internal list.
 *
 * @param <T> the specific {@link Resource} subtype managed by this class
 */
public class ResourceManager<T extends Resource> {

    private final List<T> resources;

    /**
     * Constructs an empty resource manager.
     */
    public ResourceManager() {
        this.resources = new ArrayList<>();
    }

    /**
     * Adds a resource to the managed collection.
     *
     * @param resource the resource to add
     * @throws IllegalArgumentException if {@code resource} is {@code null} or its
     *                                  {@link Resource#getResourceId()} already exists
     */
    public void addResource(T resource) {
        Objects.requireNonNull(resource, "Resource must not be null");

        if (findById(resource.getResourceId()) != null) {
            throw new IllegalArgumentException(
                    "Resource with id already exists: " + resource.getResourceId());
        }

        resources.add(resource);
    }

    /**
     * Removes a resource from the managed collection by its identifier.
     *
     * @param resourceId identifier of the resource to remove
     * @return {@code true} if a resource was removed; {@code false} if no match was found
     */
    public boolean removeResource(String resourceId) {
        if (resourceId == null || resourceId.isBlank()) {
            return false;
        }

        return resources.removeIf(resource -> resourceId.equals(resource.getResourceId()));
    }

    /**
     * Replaces an existing resource that shares the same identifier.
     *
     * @param resource the updated resource instance
     * @return {@code true} if an existing resource was updated; {@code false} if not found
     * @throws IllegalArgumentException if {@code resource} is {@code null}
     */
    public boolean updateResource(T resource) {
        Objects.requireNonNull(resource, "Resource must not be null");

        for (int i = 0; i < resources.size(); i++) {
            if (resource.getResourceId().equals(resources.get(i).getResourceId())) {
                resources.set(i, resource);
                return true;
            }
        }

        return false;
    }

    /**
     * Returns all resources whose {@link Resource#getResourceType()} matches the given type.
     *
     * @param type the {@link ResourceType} to search for
     * @return a new list containing matching resources; empty if {@code type} is {@code null}
     *         or no matches exist
     */
    public List<T> searchByType(ResourceType type) {
        if (type == null) {
            return List.of();
        }

        List<T> results = new ArrayList<>();
        for (T resource : resources) {
            if (type == resource.getResourceType()) {
                results.add(resource);
            }
        }
        return results;
    }

    /**
     * Returns all resources located at the given campus location.
     * Comparison is case-insensitive and trims surrounding whitespace.
     *
     * @param location the location to search for
     * @return a new list containing matching resources; empty if {@code location} is
     *         {@code null}, blank, or no matches exist
     */
    public List<T> searchByLocation(String location) {
        if (location == null || location.isBlank()) {
            return List.of();
        }

        String normalizedLocation = location.trim();
        List<T> results = new ArrayList<>();
        for (T resource : resources) {
            if (resource.getLocation() != null
                    && resource.getLocation().equalsIgnoreCase(normalizedLocation)) {
                results.add(resource);
            }
        }
        return results;
    }

    /**
     * Returns all resources whose availability status is {@link AvailabilityStatus#AVAILABLE}.
     *
     * @return a new list containing available resources; empty if none are available
     */
    public List<T> getAvailable() {
        List<T> results = new ArrayList<>();
        for (T resource : resources) {
            if (resource.getAvailabilityStatus() == AvailabilityStatus.AVAILABLE) {
                results.add(resource);
            }
        }
        return results;
    }

    /**
     * Finds a resource by its unique identifier.
     *
     * @param resourceId identifier of the resource to find
     * @return the matching resource, or {@code null} if not found or {@code resourceId} is invalid
     */
    public T findById(String resourceId) {
        if (resourceId == null || resourceId.isBlank()) {
            return null;
        }

        for (T resource : resources) {
            if (resourceId.equals(resource.getResourceId())) {
                return resource;
            }
        }
        return null;
    }

    /**
     * Returns an unmodifiable view of all resources managed by this instance.
     *
     * @return an unmodifiable list of all resources
     */
    public List<T> getAllResources() {
        return List.copyOf(resources);
    }

    /**
     * Returns the number of resources currently managed.
     *
     * @return resource count
     */
    public int getResourceCount() {
        return resources.size();
    }
}
