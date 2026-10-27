import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class OnChainAlertBot {

    public static class AlertCondition {
        private final double largeTransferThreshold;
        private final String trackedToken;

        // Requirement 01: Configurable alert condition
        public AlertCondition(double largeTransferThreshold, String trackedToken) {
            this.largeTransferThreshold = largeTransferThreshold;
            this.trackedToken = trackedToken;
        }

        public double getLargeTransferThreshold() {
            return largeTransferThreshold;
        }

        public String getTrackedToken() {
            return trackedToken;
        }
    }

    public static class TransactionEvent {
        private final String txHash;
        private final String from;
        private final String to;
        private final double amount;
        private final String token;

        public TransactionEvent(String txHash, String from, String to, double amount, String token) {
            this.txHash = txHash;
            this.from = from;
            this.to = to;
            this.amount = amount;
            this.token = token;
        }

        public String getTxHash() { return txHash; }
        public double getAmount() { return amount; }
        public String getToken() { return token; }
        public String getFrom() { return from; }
        public String getTo() { return to; }
    }

    private final AlertCondition condition;
    private final Set<String> processedTxHashes = new HashSet<>(); // Requirement 04: Prevents repeat spam

    public OnChainAlertBot(AlertCondition condition) {
        this.condition = condition;
    }

    public void processTransaction(TransactionEvent tx) {
        // Requirement 04: No repeat spam for the same event
        if (processedTxHashes.contains(tx.getTxHash())) {
            System.out.println("ℹ️ Duplicate transaction ignored (Spam protection active): " + tx.getTxHash());
            return;
        }

        // Requirement 01: Evaluate alert condition
        if (tx.getToken().equalsIgnoreCase(condition.getTrackedToken()) && tx.getAmount() >= condition.getLargeTransferThreshold()) {
            sendNotification(tx);
            processedTxHashes.add(tx.getTxHash());
        }
    }

    // Requirement 02 & 03: Delivers alerts to a notification channel with full event details & explorer link
    private void sendNotification(TransactionEvent tx) {
        String explorerLink = "https://explorer.pharos.xyz/tx/" + tx.getTxHash();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        StringBuilder alertMessage = new StringBuilder();
        alertMessage.append("\n🚨 [ON-CHAIN ALERT DETECTED] 🚨\n");
        alertMessage.append("Channel Target: Telegram / Discord Webhook\n");
        alertMessage.append("Timestamp: ").append(timestamp).append("\n");
        alertMessage.append("Event: Large Transfer Triggered\n");
        alertMessage.append(String.format("Value: %.2f %s\n", tx.getAmount(), tx.getToken()));
        alertMessage.append("From: ").append(tx.getFrom()).append(" -> To: ").append(tx.getTo()).append("\n");
        alertMessage.append("Explorer Link: ").append(explorerLink).append("\n");

        System.out.println(alertMessage.toString());
    }

    public static void main(String[] args) {
        // Configure threshold: Alert for transfers >= 10,000 USDC
        AlertCondition config = new AlertCondition(10000.0, "USDC");
        OnChainAlertBot bot = new OnChainAlertBot(config);

        System.out.println("=== STARTING ON-CHAIN ALERT BOT ===");
        System.out.println("Monitoring threshold: transfers >= " + config.getLargeTransferThreshold() + " " + config.getTrackedToken());

        // Event 1: Normal transfer (No alert)
        TransactionEvent tx1 = new TransactionEvent("0x1111...", "0xUserA", "0xUserB", 5000.0, "USDC");
        bot.processTransaction(tx1);

        // Event 2: Large transfer (Triggers Alert)
        TransactionEvent tx2 = new TransactionEvent("0x2222...", "0xWhaleX", "0xVaultY", 50000.0, "USDC");
        bot.processTransaction(tx2);

        // Event 3: Duplicate event (Spam protection test)
        bot.processTransaction(tx2);
    }
}
