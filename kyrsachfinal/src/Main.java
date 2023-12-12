public class Main {
    public static void main(String[] args) {
        try {
            GUI gui = new GUI();
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }
}
