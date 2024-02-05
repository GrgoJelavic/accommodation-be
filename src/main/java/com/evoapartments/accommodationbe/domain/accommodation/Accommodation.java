package com.evoapartments.accommodationbe.model.accommodation;

import com.evoapartments.accommodationbe.model.reservation.ReservedAccommodation;
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

//    @OneToMany(mappedBy = "accommodation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Rate> rates;
    private BigDecimal accommodationPrice;

    private boolean isReserved = false;

    @Lob
    private Blob photo;

    @OneToMany(mappedBy = "accommodation", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ReservedAccommodation> reservations;

    private String accommodationName;
//    private String description;
    //do capacity check when booking
    private Integer guestCapacity;
    private String address;
    private String city;
    private Integer zipCode;
    private String country;


//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "accommodation_status_id")
//    private AccommodationStatus;

//    @OneToMany(mappedBy = "accommodation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<AccommodationStaff> staffList;
//    extra added end

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id")
//    @JsonIgnore
    private Type type;

    public Accommodation() {
        this.reservations = new ArrayList<>();
    }

    //adds reservation to the accommodation(ManyToOne for both tables)
    public void addReservation(ReservedAccommodation reservation) {
        if (reservations == null) {
            reservations = new ArrayList<>();
        }
        reservations.add(reservation);
        reservation.setAccommodation(this);
        isReserved = true;
        String bookingCode = RandomStringUtils.randomNumeric(12);
        reservation.setBookingConfirmationCode(bookingCode);
    }
}