package com.magicrepokit.chat.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String bookingNumber) {
        super("预定" + bookingNumber + " 不存在!");
    }
}
