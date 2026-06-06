package com.mobile.backendjava.dm.dto.rrg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RRGResponseDTO {
    private List<RRGItemDTO> items;
}
