package com.magicrepokit.chat.exception;

public class BookingCannotBeCancelledException extends RuntimeException {
    public BookingCannotBeCancelledException(String bookingNumber) {
        super("预定" + bookingNumber + " 无法取消!");
    }
}
