public class FlightBookingFacade {

    private final SeatInventorySystem seatSystem;
    private final BaggagePricingEngine baggageEngine;
    private final CurrencyConverter currencyConverter;
    private final PaymentProcessor paymentProcessor;
    private final TicketIssuer ticketIssuer;

    public FlightBookingFacade() {
        this.seatSystem = new SeatInventorySystem();
        this.baggageEngine = new BaggagePricingEngine();
        this.currencyConverter = new CurrencyConverter();
        this.paymentProcessor = new PaymentProcessor();
        this.ticketIssuer = new TicketIssuer();
    }

    public String bookFlight(String passengerName,
                             String flightCode,
                             String seatNumber,
                             double baseFareUSD,
                             int bagCount,
                             double bagWeightKg,
                             String currency,
                             String cardNumber) {
        return bookFlight(passengerName, flightCode, seatNumber, baseFareUSD,
                bagCount, bagWeightKg, currency, cardNumber, "STANDARD");
    }

    public String bookFlight(String passengerName,
                             String flightCode,
                             String seatNumber,
                             double baseFareUSD,
                             int bagCount,
                             double bagWeightKg,
                             String currency,
                             String cardNumber,
                             String loyaltyTier) {
        String lockToken = null;

        try {
            // 1. connect and lock seat
            seatSystem.connectToReservationServer();
            lockToken = seatSystem.lockSeat(flightCode, seatNumber, passengerName);

            // 2. check tier and calculate baggage fee
            double bagFee = 0.0;
            if (!"SILVER".equalsIgnoreCase(loyaltyTier) && !"GOLD".equalsIgnoreCase(loyaltyTier)) {
                bagFee = baggageEngine.calculateBaggageFeeUSD(bagCount, bagWeightKg);
            }
            double totalUSD = baseFareUSD + bagFee;

            // 3. convert currency
            double total = currencyConverter.convertFromUSD(totalUSD, currency);

            // 4. process payment
            paymentProcessor.initializeGateway();
            if ("SILVER".equalsIgnoreCase(loyaltyTier)) {
                paymentProcessor.applyPromoCode("SILVER-FREE-BAG");
            }
            String tx = paymentProcessor.chargeCard(cardNumber, total, currency);

            // 5. generate ticket
            return ticketIssuer.generateTicket(passengerName, flightCode, lockToken, tx);

        } catch (RuntimeException e) {
            // rollback if failed
            if (lockToken != null) {
                seatSystem.releaseSeatLock(lockToken);
            }
            System.out.println("[Facade] Booking failed: " + e.getMessage());
            return "BOOKING_FAILED: " + e.getMessage();
        } finally {
            // close connection
            seatSystem.disconnect();
        }
    }
}
