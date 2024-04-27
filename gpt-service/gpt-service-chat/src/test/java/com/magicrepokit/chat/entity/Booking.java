package com.magicrepokit.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    private String bookingNumber;
    private LocalDate bookingFrom;
    private LocalDate bookingTo;
    private Customer customer;
}
