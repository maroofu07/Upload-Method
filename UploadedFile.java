package com.example.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "uploaded_file")
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    public UploadedFile() {}

    public UploadedFile(String fileName) {
        this.fileName = fileName;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
