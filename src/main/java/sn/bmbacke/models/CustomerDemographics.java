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
@Table(name = "CustomerDemographics")
public class CustomerDemographics {
    @Id
    @Column(name = "CustomerTypeID", length = 10)
    private String id;

    @Lob
    @Column(name = "CustomerDesc")
    private String customerDesc;

}