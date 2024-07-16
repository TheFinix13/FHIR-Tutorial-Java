package com.fiyinstutorials.fhirtutorial.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdentifierDTO {
        private String resourceType;
        private Long resourceId;
        private String system;
        private String value;
}
