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
@Table(name = "Territories")
public class Territory {
    @Id
    @Column(name = "TerritoryID", length = 20)
    private String id;

    @Column(name = "TerritoryDescription", nullable = false, length = 50)
    private String territoryDescription;

    @ManyToOne
    @JoinColumn(name = "RegionID", nullable = false)
    private Region region;

    // Getters and Setters
}
