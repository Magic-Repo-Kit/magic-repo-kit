package com.magicrepokit.chat.service.tool;

import com.magicrepokit.chat.entity.Booking;
import com.magicrepokit.chat.service.BookingService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookingTools {
    @Autowired
    private BookingService bookingService;

    /**
     * 获取预定详情
     * @param bookingNumber
     * @param customerName
     * @param customerSurname
     * @return
     */
    @Tool
    public Booking getBookingDetails(String bookingNumber, String customerName, String customerSurname) {
        System.out.println("==========================================================================================");
        System.out.printf("[Tool]: 获取预定详情 %s for %s %s...%n", bookingNumber, customerName, customerSurname);
        System.out.println("==========================================================================================");

        return bookingService.getBookingDetails(bookingNumber, customerName, customerSurname);
    }

    @Tool
    public void cancelBooking(String bookingNumber, String customerName, String customerSurname) {
        System.out.println("==========================================================================================");
        System.out.printf("[Tool]: 取消预订 %s for %s %s...%n", bookingNumber, customerName, customerSurname);
        System.out.println("==========================================================================================");

        bookingService.cancelBooking(bookingNumber, customerName, customerSurname);
    }

}
