package com.scoutingalpha.playerInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.UUID;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate brithday;
}
