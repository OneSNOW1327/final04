package com.ex.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoDTO {
    private Integer id;
    private String orgname;
    private String sysname;
    private String filePath;
    
    public PhotoDTO(String sysname) {
        this.sysname = sysname;
    }
}
