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
@Table(name = "EmployeeTerritories")
public class EmployeeTerritory {
    @EmbeddedId
    private EmployeeTerritoryId id;

    @ManyToOne
    @MapsId("employeeId")
    @JoinColumn(name = "EmployeeID")
    private Employee employee;

    @ManyToOne
    @MapsId("territoryId")
    @JoinColumn(name = "TerritoryID")
    private Territory territory;

}
