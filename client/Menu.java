package client;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private List<String> items;

    public Menu() {
        items = new ArrayList<>();
        items.add("Coffee");
        items.add("Tea");
        items.add("Sandwich");
        items.add("Cake");
    }

    public List<String> getItems() {
        return items;
    }
}
