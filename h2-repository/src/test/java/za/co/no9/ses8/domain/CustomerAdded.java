package za.co.no9.ses8.domain;

class CustomerAdded {
    public final String name;


    public CustomerAdded(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "CustomerAdded{" +
                "name='" + name + '\'' +
                '}';
    }
}
