package org.example.server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.server.util.RandomGenerator;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aleksey
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
public class DataObject {

    private Long id;
    private Integer age;
    private BigDecimal salary;
    private Instant birthDate;
    private String name;

    private Long accountNumber;
    private Long transactionId;
    private Long productCode;

    private Integer quantity;
    private Integer score;
    private Integer rating;

    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal tax;

    private Instant registrationDate;
    private Instant expirationDate;
    private Instant lastLoginDate;

    private String email;
    private String address;
    private String phoneNumber;

    private Boolean isAdmin;
    private Double longitude;
    private Double latitude;

    public static final Field[] DTO_FIELDS;

    static {
        DTO_FIELDS =  DataObject.class.getDeclaredFields();
        for (var field : DTO_FIELDS){
            field.setAccessible(true);
        }
    }

    public static DataObject getRandomInstance() {
        DataObject dataObject = new DataObject();

        dataObject.setId(RandomGenerator.getLong());
        dataObject.setAge(RandomGenerator.getInteger()); // Минимум 1
        dataObject.setSalary(RandomGenerator.getBigDecimal());
        dataObject.setBirthDate(RandomGenerator.getInstant());
        dataObject.setName(RandomGenerator.getString(100));

        dataObject.setAccountNumber(RandomGenerator.getLong());
        dataObject.setTransactionId(RandomGenerator.getLong());
        dataObject.setProductCode(RandomGenerator.getLong());

        dataObject.setQuantity(RandomGenerator.getInteger());
        dataObject.setScore(RandomGenerator.getInteger());
        dataObject.setRating(RandomGenerator.getInteger());

        dataObject.setPrice(RandomGenerator.getBigDecimal());
        dataObject.setDiscount(RandomGenerator.getBigDecimal());
        dataObject.setTax(RandomGenerator.getBigDecimal());

        dataObject.setRegistrationDate(RandomGenerator.getInstant());
        dataObject.setExpirationDate(RandomGenerator.getInstant());
        dataObject.setLastLoginDate(RandomGenerator.getInstant());

        dataObject.setEmail(RandomGenerator.getString(10));
        dataObject.setAddress(RandomGenerator.getString(10));
        dataObject.setPhoneNumber(RandomGenerator.getString(10));

        dataObject.setIsAdmin(RandomGenerator.getBoolean());
        dataObject.setLongitude(RandomGenerator.getDouble());
        dataObject.setLatitude(RandomGenerator.getDouble());

        return dataObject;
    }

    public static List<DataObject> getRandomInstanceList(long n) {
        List<DataObject> objectList = new ArrayList<>();
        for (int i = 0; i < n; ++i) {
            objectList.add(getRandomInstance());
        }
        return objectList;
    }
}
