import presenter.PresenterClass;
import view.ViewClass;

public class Main {
    public static void main(String[] args) {
        ViewClass v = new ViewClass();
        PresenterClass presenter = new PresenterClass(v);
    }
}
