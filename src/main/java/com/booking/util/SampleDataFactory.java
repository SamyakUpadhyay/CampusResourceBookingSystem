package com.booking.util;
import com.booking.model.Admin;
import com.booking.model.AvailabilityStatus;
import com.booking.model.EventSpace;
import com.booking.model.LabEquipment;
import com.booking.model.Resource;
import com.booking.model.Staff;
import com.booking.model.Student;
import com.booking.model.StudyRoom;

import com.booking.model.Booking;
import com.booking.model.BookingStatus;
import com.booking.model.Resource;
import com.booking.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory for generating sample data to initialise the system during development.
 */
public class SampleDataFactory {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private SampleDataFactory() {
        // Utility class — no instances permitted.
    }

    /**
     * Creates a default set of sample users for testing.
     *
     * @return list of pre-configured {@link User} instances
     */
    public static List<User> createSampleUsers() {
        List<User> users = new ArrayList<>();

        users.add(new Student(
                "U-STU-001",
                "student",
                PasswordEncoder.encode("student123"),
                "Alex Student",
                "alex.student@campus.edu",
                "S10001",
                "School of Computer Science"));

        users.add(new Staff(
                "U-STF-001",
                "staff",
                PasswordEncoder.encode("staff123"),
                "Sam Staff",
                "sam.staff@campus.edu",
                "E20001",
                "Facilities Office"));

        users.add(new Admin(
                "U-ADM-001",
                "admin",
                PasswordEncoder.encode("admin123"),
                "Jordan Admin",
                "jordan.admin@campus.edu",
                "A30001"));

        return users;
    }

    /**
     * Creates a default set of sample resources for testing.
     *
     * @return list of pre-configured {@link Resource} instances
     */
    public static List<Resource> createSampleResources() {

        List<Resource> resources = new ArrayList<>();

        // ===== Study Rooms =====
        resources.add(new StudyRoom(
                "SR001",
                "Study Room A",
                "Library Level 1",
                6,
                AvailabilityStatus.AVAILABLE,
                "L1-101",
                true,
                true));

        resources.add(new StudyRoom(
                "SR002",
                "Study Room B",
                "Library Level 2",
                8,
                AvailabilityStatus.AVAILABLE,
                "L2-205",
                true,
                false));

        resources.add(new StudyRoom(
                "SR003",
                "Quiet Study Room",
                "Block C",
                4,
                AvailabilityStatus.MAINTENANCE,
                "C-302",
                false,
                true));

        // ===== Lab Equipment =====
        resources.add(new LabEquipment(
                "LE001",
                "Digital Oscilloscope",
                "Engineering Lab",
                1,
                AvailabilityStatus.AVAILABLE,
                "Electronics",
                10));

        resources.add(new LabEquipment(
                "LE002",
                "DSLR Camera",
                "Media Lab",
                1,
                AvailabilityStatus.AVAILABLE,
                "Camera",
                6));

        resources.add(new LabEquipment(
                "LE003",
                "VR Headset",
                "Innovation Lab",
                1,
                AvailabilityStatus.UNAVAILABLE,
                "VR",
                4));

        // ===== Event Spaces =====
        resources.add(new EventSpace(
                "ES001",
                "Main Hall",
                "Student Centre",
                200,
                AvailabilityStatus.AVAILABLE,
                250,
                true));

        resources.add(new EventSpace(
                "ES002",
                "Innovation Hub",
                "Engineering Building",
                80,
                AvailabilityStatus.AVAILABLE,
                100,
                true));

        resources.add(new EventSpace(
                "ES003",
                "Seminar Room",
                "Business School",
                50,
                AvailabilityStatus.UNAVAILABLE,
                60,
                false));

        return resources;
    }

    /**
     * Creates a default set of sample bookings for testing.
     *
     * @return list of pre-configured {@link Booking} instances
     */
    public static List<Booking> createSampleBookings() {
        List<Booking> bookings = new ArrayList<>();

        LocalDateTime tomorrowMorning = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0);

        bookings.add(new Booking(
                "B-SAMPLE-001",
                "U-STU-001",
                "SR001",
                tomorrowMorning,
                tomorrowMorning.plusHours(1),
                BookingStatus.PENDING));

        bookings.add(new Booking(
                "B-SAMPLE-002",
                "U-STF-001",
                "LE001",
                tomorrowMorning.plusDays(1),
                tomorrowMorning.plusDays(1).plusHours(2),
                BookingStatus.CONFIRMED));

        return bookings;
    }
}
