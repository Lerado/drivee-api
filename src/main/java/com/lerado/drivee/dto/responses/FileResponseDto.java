package com.lerado.drivee.dto.responses;

import java.util.Arrays;

import lombok.Data;

@Data
public class FileResponseDto {

    private String name;
    private String originalName;
    private String downloadUrl;
    private String previewUrl;
    private String extension;
    private long createdAt;

    public FileResponseDto(String name, String downloadUrl, String previewUrl, String extension)  {
        this.setDownloadUrl(downloadUrl);
        this.setPreviewUrl(previewUrl);
        this.setName(name);
        this.setExtension(extension);
    }

    public void setName(String value) {
        
        this.name = value;
        
        // Set original name and createdAt by truncating the file name
        // Name = ${createdAt}_${originalName}
        String[] nameSplit = value.split("_");
        if (nameSplit.length < 2) return;
        this.createdAt = Long.valueOf(nameSplit[0]);
        this.setOriginalName(String.join("_", Arrays.asList(nameSplit).subList(1, nameSplit.length)));
    }
}
