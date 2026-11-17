public class Main {
    public static void main(String[] args) {
        FlightBookingFacade bookingFacade = new FlightBookingFacade();

        System.out.println("=== TEST 1: Standard Successful Booking ===");
        String ticket1 = bookingFacade.bookFlight(
                "Nurlan Sadykov",
                "KZ405",
                "11B",
                280.00,
                2,
                24.5,
                "EUR",
                "4400430188921045"
        );
        System.out.println("Result 1: " + ticket1 + "\n");

        System.out.println("=== TEST 2: Blocked Crew Seat (Row 13) ===");
        String ticket2 = bookingFacade.bookFlight(
                "Alina Omarova",
                "KZ405",
                "13A",
                280.00,
                1,
                16.0,
                "USD",
                "4400430188921045"
        );
        System.out.println("Result 2: " + ticket2 + "\n");

        System.out.println("=== TEST 3: Payment Failure & Automatic Seat Rollback ===");
        String ticket3 = bookingFacade.bookFlight(
                "Bauyrzhan Alimov",
                "KZ405",
                "09F",
                280.00,
                1,
                19.0,
                "KZT",
                "1234"
        );
        System.out.println("Result 3: " + ticket3 + "\n");

        System.out.println("=== TEST 4: Silver Loyalty Tier Booking ===");
        String ticket4 = bookingFacade.bookFlight(
                "Madina Zhanabayeva",
                "KZ512",
                "04A",
                400.00,
                3,
                30.0,
                "USD",
                "4400430188921045",
                "SILVER"
        );
        System.out.println("Result 4: " + ticket4 + "\n");
    }
}
