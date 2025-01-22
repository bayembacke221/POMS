package sn.bmbacke.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Region")
public class Region {
    @Id
    @Column(name = "RegionID")
    private Integer id;

    @Column(name = "RegionDescription", nullable = false, length = 50)
    private String regionDescription;

}




// EmployeeTerritory.java


// EmployeeTerritoryId.java


// CustomerDemographics.java


// CustomerCustomerDemo.java


// CustomerCustomerDemoId.java
