package assignment2.food;

public enum FoodEnum {
    Fruit,
    Cake,
    IceCream,
    Lollipop,
    Pickle,
    SwissCheese;

    private static final FoodEnum values[] = values();
    
    public static FoodEnum getFoodEnum(int x){
        if (x < 0 || x > values.length) return null;
        return values[x];
    }
}

