

public class TestImmutable {

    public static void main(String[] args) {

        
    }


}

class ImmutableObj {
    private final Integer lastNum;
    private final Integer[] lastNums;

    private ImmutableObj(Integer lastNum, Integer[] lastNums) {
        this.lastNum = lastNum;
        this.lastNums = lastNums;
    }

    public static void save(Integer a, Integer[] a1) {
        ImmutableObj obj = new ImmutableObj(a, a1);


    }


}