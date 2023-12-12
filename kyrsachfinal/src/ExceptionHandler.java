public class ExceptionHandler {
    public static void handle(Exception e) {
        logError(e);
        showErrorDialog(e.getMessage());
    }

    private static void logError(Exception e) {
        System.out.println("Произошла ошибка: " + e.getMessage());
        e.printStackTrace();
    }

    private static void showErrorDialog(String message) {
        System.err.println("Ошибка: " + message);
    }
}
