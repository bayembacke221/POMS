package sn.bmbacke.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;

@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class EmployeeTerritoryId implements Serializable {
    @Column(name = "EmployeeID")
    private Integer employeeId;

    @Column(name = "TerritoryID", length = 20)
    private String territoryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeTerritoryId that = (EmployeeTerritoryId) o;
        return Objects.equals(employeeId, that.employeeId) &&
                Objects.equals(territoryId, that.territoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, territoryId);
    }


}
