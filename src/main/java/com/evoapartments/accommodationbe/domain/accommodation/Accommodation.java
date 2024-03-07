package com.evoapartments.accommodationbe.domain.accommodation;

import com.evoapartments.accommodationbe.domain.reservation.ReservedAccommodation;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal accommodationPrice;
    private boolean isReserved = false;
    private String accommodationName;
    private Integer guestCapacity;
    private String address;
    private String city;
    private Integer zipCode;
    private String country;
    @Lob
    private Blob photo;

    @OneToMany(mappedBy = "accommodation", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ReservedAccommodation> reservations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id")
    private Type type;

    public Accommodation() {
        this.reservations = new ArrayList<>();
    }

    public void addReservation(ReservedAccommodation reservation, String eventId) {
        if (reservations == null) {
            reservations = new ArrayList<>();
        }
        reservations.add(reservation);
        reservation.setAccommodation(this);
        isReserved = true;
        String bookingCode = RandomStringUtils.randomNumeric(12);
        reservation.setBookingConfirmationCode(bookingCode);
        reservation.setCalendarEventId(eventId);
    }
}