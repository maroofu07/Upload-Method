package com.example.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Entity.UploadedFile;

public interface FileRepository extends JpaRepository<UploadedFile, Long> {
}
