package com.evoapartments.accommodationbe.domain.reservation;

import com.evoapartments.accommodationbe.domain.accommodation.Accommodation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservedAccommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String guestFullName;
    private String guestEmail;

    @Column(name = "check_in")
    private LocalDate checkInDate;

    @Column(name = "check_out")
    private LocalDate checkOutDate;

    @Column(name = "adults")
    private int numberOfAdults;

    @Column(name = "children")
    private int numberOfChildren;

    @Column(name = "total_guests")
    private int totalNumberOfGuests;

    @Column(name = "confirmation_code")
    private String reservationConfirmationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;

    public void calculateTotalNumberOfGuests(){
        this.totalNumberOfGuests = this.numberOfAdults + this.numberOfChildren;
    }

    public void setNumberOfAdults(int numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
        calculateTotalNumberOfGuests();
    }

    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
        calculateTotalNumberOfGuests();
    }

    public void setBookingConfirmationCode(String bookingConfirmationCode) {
        this.reservationConfirmationCode = bookingConfirmationCode;
    }
}
