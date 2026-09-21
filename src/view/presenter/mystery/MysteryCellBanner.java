package view.presenter.mystery;

// T-10: Mystery Cell banners are bordered to stand out.
final class MysteryCellBanner {

    private static final String BORDER = "=".repeat(40);

    private MysteryCellBanner() {
    }

    static String around(String text) {
        return "\n" + BORDER + "\n" + text + "\n" + BORDER;
    }
}
