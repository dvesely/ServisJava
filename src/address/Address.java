package address;


public class Address {
    private Integer id;
    private String street;
    private int number;
    private String city;
    private int zipCode;
    private String country;

    public Address(String street, int number, String city, int zipCode, String country) {
        this (null, street, number, city, zipCode, country);
    }
    
    public Address(Integer id, String street, int number, String city, int zipCode, String country) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }
    
    public Object[] toObjectArray() {
        if (id != null) {
            return new Object[]{id, street, number, city, zipCode, country};
        }else {
            return new Object[]{street, number, city, zipCode, country};
        }        
    }
    
}
