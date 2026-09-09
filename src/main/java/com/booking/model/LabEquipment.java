package com.booking.model;

import java.util.Objects;

/**
 * Represents laboratory equipment available for reservation.
 */
public class LabEquipment extends Resource {

    private static final long serialVersionUID = 1L;

    private String equipmentType;
    private int quantity;

    /**
     * Constructs a new lab equipment resource.
     *
     * @param resourceId         unique identifier for the resource
     * @param name               display name of the equipment
     * @param location           lab or storage location
     * @param capacity           maximum concurrent usage capacity
     * @param availabilityStatus current availability state
     * @param equipmentType      category or type of equipment
     * @param quantity           total units available
     */
    public LabEquipment(String resourceId, String name, String location, int capacity,
                        AvailabilityStatus availabilityStatus, String equipmentType, int quantity) {
        super(resourceId, name, location, capacity, availabilityStatus);
        this.equipmentType = equipmentType;
        this.quantity = quantity;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.LAB_EQUIPMENT;
    }

    @Override
    public String getBookingRules() {
        return "Equipment must be picked up and returned within lab operating hours. "
                + quantity + " unit(s) of \"" + equipmentType + "\" available for concurrent use.";
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        LabEquipment that = (LabEquipment) o;
        return quantity == that.quantity
                && Objects.equals(equipmentType, that.equipmentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), equipmentType, quantity);
    }

    @Override
    public String toString() {
        return "LabEquipment{"
                + "resourceId='" + getResourceId() + '\''
                + ", name='" + getName() + '\''
                + ", location='" + getLocation() + '\''
                + ", capacity=" + getCapacity()
                + ", availabilityStatus=" + getAvailabilityStatus()
                + ", resourceType=" + getResourceType()
                + ", equipmentType='" + equipmentType + '\''
                + ", quantity=" + quantity
                + '}';
    }
}
